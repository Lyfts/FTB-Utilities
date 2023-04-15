package com.feed_the_beast.ftbutilities.command.tp;

import com.feed_the_beast.ftblib.lib.command.CmdBase;
import com.feed_the_beast.ftblib.lib.command.CommandUtils;
import com.feed_the_beast.ftblib.lib.math.BlockDimPos;
import com.feed_the_beast.ftbutilities.FTBUtilitiesConfig;
import com.feed_the_beast.ftbutilities.data.FTBUtilitiesPlayerData;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;

public class CmdSpawn extends CmdBase {
	public CmdSpawn() {
		super("spawn", Level.ALL);
	}

	@Override
	public void processCommand(ICommandSender sender, String[] args) throws CommandException {
		EntityPlayerMP player = getCommandSenderAsPlayer(sender);
		FTBUtilitiesPlayerData data = FTBUtilitiesPlayerData.get(CommandUtils.getForgePlayer(player));
		data.checkTeleportCooldown(sender, FTBUtilitiesPlayerData.Timer.SPAWN);
		FTBUtilitiesPlayerData.Timer.SPAWN.teleport(player, playerMP -> {
			World w = playerMP.mcServer.worldServerForDimension(FTBUtilitiesConfig.world.spawn_dimension);
			ChunkCoordinates spawnpoint = w.getSpawnPoint();

			while (w.getBlock(spawnpoint.posX, spawnpoint.posY, spawnpoint.posZ).isNormalCube()) {
				spawnpoint.posY += 2;
			}

			return new BlockDimPos(spawnpoint, FTBUtilitiesConfig.world.spawn_dimension).teleporter();
		}, null);
	}
}