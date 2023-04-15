package com.feed_the_beast.ftbutilities.command.ranks;

import java.util.Collections;
import java.util.List;

import com.feed_the_beast.ftblib.FTBLib;
import com.feed_the_beast.ftblib.lib.command.CmdBase;
import com.feed_the_beast.ftbutilities.FTBUtilities;
import com.feed_the_beast.ftbutilities.ranks.Rank;
import com.feed_the_beast.ftbutilities.ranks.Ranks;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;

/**
 * @author LatvianModder
 */
public class CmdDelete extends CmdBase {
	public CmdDelete() {
		super("delete", Level.OP);
	}

	@Override
	public List<String> getCommandAliases() {
		return Collections.singletonList("del");
	}

	@Override
	public List<String> addTabCompletionOptions(ICommandSender sender, String[] args) {
		if (args.length == 1 && Ranks.isActive()) {
			return getListOfStringsFromIterableMatchingLastWord(args, Ranks.INSTANCE.getRankNames(false));
		}

		return super.addTabCompletionOptions(sender, args);
	}

	@Override
	public boolean isUsernameIndex(String[] args, int index) {
		return index == 0;
	}

	@Override
	public void processCommand(ICommandSender sender, String[] args) throws CommandException {
		if (!Ranks.isActive()) {
			throw FTBLib.error(sender, "feature_disabled_server");
		}

		checkArgs(sender, args, 1);

		Rank rank = Ranks.INSTANCE.getRank(sender, args[0]);

		if (rank.remove()) {
			rank.ranks.save();
			sender.addChatMessage(FTBUtilities.lang(sender, "commands.ranks.delete.deleted", rank.getDisplayName()));
		} else {
			sender.addChatMessage(FTBLib.lang(sender, "nothing_changed"));
		}
	}
}