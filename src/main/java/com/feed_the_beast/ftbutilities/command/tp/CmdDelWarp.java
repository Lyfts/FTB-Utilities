package com.feed_the_beast.ftbutilities.command.tp;

import java.util.List;

import com.feed_the_beast.ftblib.lib.command.CmdBase;
import com.feed_the_beast.ftblib.lib.data.Universe;
import com.feed_the_beast.ftbutilities.FTBUtilities;
import com.feed_the_beast.ftbutilities.data.FTBUtilitiesUniverseData;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;

public class CmdDelWarp extends CmdBase {
	public CmdDelWarp() {
		super("delwarp", Level.OP);
	}

	@Override
	public List<String> addTabCompletionOptions(ICommandSender sender, String[] args) {
		if (args.length == 1) {
			return getListOfStringsFromIterableMatchingLastWord(args, FTBUtilitiesUniverseData.WARPS.list());
		}

		return super.addTabCompletionOptions(sender, args);
	}

	@Override
	public void processCommand(ICommandSender sender, String[] args) throws CommandException {
		checkArgs(sender, args, 1);

		args[0] = args[0].toLowerCase();

		if (FTBUtilitiesUniverseData.WARPS.set(args[0], null)) {
			sender.addChatMessage(FTBUtilities.lang(sender, "ftbutilities.lang.warps.del", args[0]));
			Universe.get().markDirty();
		} else {
			throw FTBUtilities.error(sender, "ftbutilities.lang.warps.not_set", args[0]);
		}
	}
}