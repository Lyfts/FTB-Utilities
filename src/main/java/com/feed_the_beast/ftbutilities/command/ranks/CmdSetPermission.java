package com.feed_the_beast.ftbutilities.command.ranks;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.feed_the_beast.ftblib.FTBLib;
import com.feed_the_beast.ftblib.lib.command.CmdBase;
import com.feed_the_beast.ftblib.lib.config.RankConfigAPI;
import com.feed_the_beast.ftblib.lib.config.RankConfigValueInfo;
import com.feed_the_beast.ftblib.lib.util.StringUtils;
import com.feed_the_beast.ftbutilities.FTBUtilities;
import com.feed_the_beast.ftbutilities.ranks.FTBUtilitiesPermissionHandler;
import com.feed_the_beast.ftbutilities.ranks.Rank;
import com.feed_the_beast.ftbutilities.ranks.Ranks;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;

/**
 * @author LatvianModder
 */
public class CmdSetPermission extends CmdBase {
	public static final List<String> PERM_VARIANTS = Arrays.asList("true", "false", "none");

	public CmdSetPermission() {
		super("set_permission", Level.OP);
	}

	@Override
	public List<String> getCommandAliases() {
		return Collections.singletonList("setp");
	}

	@Override
	public List<String> addTabCompletionOptions(ICommandSender sender, String[] args) {
		if (args.length == 1) {
			return Ranks.isActive()
					? getListOfStringsFromIterableMatchingLastWord(args, Ranks.INSTANCE.getRankNames(false))
					: Collections.emptyList();
		} else if (args.length == 2) {
			return getListOfStringsFromIterableMatchingLastWord(args,
					Ranks.isActive() ? Ranks.INSTANCE.getPermissionNodes()
							: FTBUtilitiesPermissionHandler.INSTANCE.getRegisteredNodes());
		} else if (args.length == 3) {
			RankConfigValueInfo info = RankConfigAPI.getHandler().getInfo(args[1]);

			if (info != null && !info.defaultValue.isNull()) {
				List<String> list = new ArrayList<>(info.defaultValue.getVariants());
				list.add("none");
				return getListOfStringsFromIterableMatchingLastWord(args, list);
			}

			return getListOfStringsFromIterableMatchingLastWord(args, PERM_VARIANTS);
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

		checkArgs(sender, args, 3);
		Rank rank = Ranks.INSTANCE.getRank(sender, args[0]);

		String node = args[1];
		String value0 = StringUtils.joinSpaceUntilEnd(2, args);
		String value = value0.equals("none") ? "" : value0;

		if (value.length() > 2 && value.startsWith("\"") && value.endsWith("\"")) {
			value = value.substring(1, value.length() - 1);
		}

		if (rank.setPermission(node, value) == null) {
			sender.addChatMessage(FTBLib.lang(sender, "nothing_changed"));
		} else {
			rank.ranks.save();
			IChatComponent nodeText = new ChatComponentText(node);
			nodeText.getChatStyle().setColor(EnumChatFormatting.GOLD);

			IChatComponent setText;

			if (value.isEmpty()) {
				setText = FTBUtilities.lang(sender, "commands.ranks.none");
				setText.getChatStyle().setColor(EnumChatFormatting.DARK_GRAY);
			} else {
				setText = new ChatComponentText(value);

				switch (value) {
					case "true":
						setText.getChatStyle().setColor(EnumChatFormatting.GREEN);
						break;
					case "false":
						setText.getChatStyle().setColor(EnumChatFormatting.RED);
						break;
					default:
						setText.getChatStyle().setColor(EnumChatFormatting.BLUE);
						break;
				}
			}

			sender.addChatMessage(FTBUtilities.lang(sender, "commands.ranks.set_permission.set", nodeText,
					rank.getDisplayName(), setText));
		}
	}
}