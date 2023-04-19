package com.feed_the_beast.ftbutilities.handlers;

import com.feed_the_beast.ftblib.lib.math.ChunkDimPos;
import com.feed_the_beast.ftbutilities.FTBUtilitiesConfig;
import com.feed_the_beast.ftbutilities.data.ClaimedChunk;
import com.feed_the_beast.ftbutilities.data.ClaimedChunks;
import com.feed_the_beast.ftbutilities.data.FTBUtilitiesUniverseData;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.world.ExplosionEvent;
import net.minecraftforge.event.world.WorldEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * @author LatvianModder
 */
public class FTBUtilitiesWorldEventHandler {
	public static final FTBUtilitiesWorldEventHandler INST = new FTBUtilitiesWorldEventHandler();
	@SubscribeEvent
	public void onMobSpawned(EntityJoinWorldEvent event) // FIXME: LivingSpawnEvent.CheckSpawn
	{
		if (!event.world.isRemote && !isEntityAllowed(event.entity)) {
			event.entity.setDead();
			event.setCanceled(true);
		}
	}

	@SubscribeEvent
	public void onDimensionUnload(WorldEvent.Unload event) {
		if (ClaimedChunks.isActive() && event.world.provider.dimensionId != 0) {
			ClaimedChunks.instance.markDirty();
		}
	}

	private static boolean isEntityAllowed(Entity entity) {
		if (entity instanceof EntityPlayer) {
			return true;
		}

		if (FTBUtilitiesConfig.world.safe_spawn
				&& FTBUtilitiesUniverseData.isInSpawn(MinecraftServer.getServer(), new ChunkDimPos(entity))) {
			//TODO: is this a bad way of getting server here?
			if (entity instanceof IMob) {
				return false;
			} else {
				return !(entity instanceof EntityChicken) || entity.riddenByEntity == null;
			}
		}

		return true;
	}

	@SubscribeEvent
	public void onExplosionDetonate(ExplosionEvent.Detonate event) {
		World world = event.world;
		Explosion explosion = event.explosion;

		if (world.isRemote || explosion.affectedBlockPositions.isEmpty()) {
			return;
		}

		List<ChunkPosition> list = new ArrayList<>(explosion.affectedBlockPositions);
		explosion.affectedBlockPositions.clear();
		Map<ChunkDimPos, Boolean> map = new HashMap<>();
		final MinecraftServer server = MinecraftServer.getServer(); //TODO: maybe bad

		Function<ChunkDimPos, Boolean> func = pos -> {
			if (pos.dim == 0 && FTBUtilitiesConfig.world.safe_spawn
					&& FTBUtilitiesUniverseData.isInSpawn(server, pos)) {
				return false;
			} else {
				if (FTBUtilitiesConfig.world.enable_explosions.isDefault()) {
					ClaimedChunk chunk = ClaimedChunks.isActive() ? ClaimedChunks.instance.getChunk(pos) : null;
					return chunk == null || chunk.hasExplosions();
				}

				return FTBUtilitiesConfig.world.enable_explosions.isTrue();
			}
		};

		for (ChunkPosition pos : list) {
			if (map.computeIfAbsent(new ChunkDimPos(pos.chunkPosX, pos.chunkPosY, pos.chunkPosZ, world.provider.dimensionId), func)) {
				explosion.affectedBlockPositions.add(pos);
			}
		}
	}
}
