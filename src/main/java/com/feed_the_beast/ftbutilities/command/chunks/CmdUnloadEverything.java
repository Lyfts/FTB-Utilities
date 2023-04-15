package com.feed_the_beast.ftbutilities.command.chunks;

import java.util.List;
import java.util.OptionalInt;

import com.feed_the_beast.ftblib.FTBLib;
import com.feed_the_beast.ftblib.lib.command.CmdBase;
import com.feed_the_beast.ftblib.lib.command.CommandUtils;
import com.feed_the_beast.ftblib.lib.data.ForgeTeam;
import com.feed_the_beast.ftblib.lib.data.Universe;
import com.feed_the_beast.ftbutilities.data.ClaimedChunk;
import com.feed_the_beast.ftbutilities.data.ClaimedChunks;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;

/**
 * @author LatvianModder
 */
public class CmdUnloadEverything extends CmdBase {
	public CmdUnloadEverything() {
		super("unload_everything", Level.OP_OR_SP);
	}

	@Override
	public List<String> addTabCompletionOptions(ICommandSender sender, String[] args) {
		if (args.length == 1) {
			return getListOfStringsFromIterableMatchingLastWord(args, CommandUtils.getDimensionNames());
		}

		return super.addTabCompletionOptions(sender, args);
	}

	@Override
	public void processCommand(ICommandSender sender, String[] args) throws CommandException {
		if (!ClaimedChunks.isActive()) {
			throw FTBLib.error(sender, "feature_disabled_server");
		}

		OptionalInt dimension = CommandUtils.parseDimension(sender, args, 0);

		for (ForgeTeam team : Universe.get().getTeams()) {
			for (ClaimedChunk chunk : ClaimedChunks.instance.getTeamChunks(team, dimension)) {
				chunk.setLoaded(false);
			}
		}
	}
}