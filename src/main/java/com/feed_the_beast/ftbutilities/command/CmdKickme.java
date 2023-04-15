package com.feed_the_beast.ftbutilities.command;

import com.feed_the_beast.ftblib.lib.command.CmdBase;
import com.feed_the_beast.ftbutilities.FTBUtilities;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;

public class CmdKickme extends CmdBase {
	public CmdKickme() {
		super("kickme", Level.ALL);
	}

	@Override
	public void processCommand(ICommandSender sender, String[] args) throws CommandException {
		EntityPlayerMP player = getCommandSenderAsPlayer(sender);
		if (player.mcServer.isDedicatedServer()) {
			getCommandSenderAsPlayer(sender).playerNetServerHandler.kickPlayerFromServer(FTBUtilities.lang(sender, "ftbutilities.lang.kickme").getUnformattedText());
		} else {
			player.mcServer.initiateShutdown();
		}
	}
}