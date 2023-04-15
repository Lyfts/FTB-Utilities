package com.feed_the_beast.ftbutilities.command.tp;

import com.feed_the_beast.ftblib.lib.command.CmdBase;
import com.feed_the_beast.ftblib.lib.command.CommandUtils;
import com.feed_the_beast.ftblib.lib.math.ChunkDimPos;
import com.feed_the_beast.ftblib.lib.math.TeleporterDimPos;
import com.feed_the_beast.ftbutilities.FTBUtilities;
import com.feed_the_beast.ftbutilities.FTBUtilitiesConfig;
import com.feed_the_beast.ftbutilities.data.ClaimedChunks;
import com.feed_the_beast.ftbutilities.data.FTBUtilitiesPlayerData;

import net.minecraft.block.material.Material;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;

public class CmdRTP extends CmdBase {
	public CmdRTP() {
		super("rtp", Level.ALL);
	}

	@Override
	public void processCommand(ICommandSender sender, String[] args) throws CommandException {
		EntityPlayerMP player = getCommandSenderAsPlayer(sender);
		FTBUtilitiesPlayerData data = FTBUtilitiesPlayerData.get(CommandUtils.getForgePlayer(player));
		data.checkTeleportCooldown(sender, FTBUtilitiesPlayerData.Timer.RTP);
		FTBUtilitiesPlayerData.Timer.RTP.teleport(player,
				playerMP -> findBlockPos(playerMP.mcServer.worldServerForDimension(FTBUtilitiesConfig.world.spawn_dimension), player, 0),
				null);
	}

	private TeleporterDimPos findBlockPos(World world, EntityPlayerMP player, int depth) {
		if (++depth > FTBUtilitiesConfig.world.rtp_max_tries) {
			player.addChatMessage(FTBUtilities.lang(player, "ftbutilities.lang.rtp.fail"));
			return TeleporterDimPos.of(player);
		}

		double dist = FTBUtilitiesConfig.world.rtp_min_distance + world.rand.nextDouble()
				* (FTBUtilitiesConfig.world.rtp_max_distance - FTBUtilitiesConfig.world.rtp_min_distance);
		double angle = world.rand.nextDouble() * Math.PI * 2D;

		int x = MathHelper.floor_double(Math.cos(angle) * dist);
		int y = 256;
		int z = MathHelper.floor_double(Math.sin(angle) * dist);

		if (!isInsideWorldBorder(world, x, y, z)) {
			return findBlockPos(world, player, depth);
		}

		if (ClaimedChunks.instance != null && ClaimedChunks.instance
				.getChunk(new ChunkDimPos(x >> 4, z >> 4, world.provider.dimensionId)) != null) {
			return findBlockPos(world, player, depth);
		}

		// TODO: Find a better way to check for biome without loading the chunk
		BiomeGenBase biome = world.getBiomeGenForCoords(x, z);
		if (biome.biomeName.contains("ocean")) {
			return findBlockPos(world, player, depth);
		}

		Chunk chunk = world.getChunkFromChunkCoords(x >> 4, z >> 4);

		while (y > 0) {
			y--;

			if (chunk.getBlock(x, y, z).getMaterial() != Material.air) {
				return TeleporterDimPos.of(x + 0.5D, y + 2.5D, z + 0.5D, world.provider.dimensionId);
			}
		}

		return findBlockPos(world, player, depth);
	}

	private boolean isInsideWorldBorder(World world, double x, double y, double z) {
		return x > -30000000 && x < 30000000 && z > -30000000 && z < 30000000 && y > -30000000 && y < 30000000;
	}

}