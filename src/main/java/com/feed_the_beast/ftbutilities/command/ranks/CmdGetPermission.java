package com.feed_the_beast.ftbutilities.command.ranks;

import java.util.Collections;
import java.util.List;

import com.feed_the_beast.ftblib.lib.command.CmdBase;
import com.feed_the_beast.ftblib.lib.config.ConfigBoolean;
import com.feed_the_beast.ftblib.lib.config.ConfigValue;
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
public class CmdGetPermission extends CmdBase {
	public CmdGetPermission() {
		super("get_permission", Level.OP);
	}

	@Override
	public List<String> getCommandAliases() {
		return Collections.singletonList("getp");
	}

	@Override
	public List<String> addTabCompletionOptions(ICommandSender sender, String[] args) {
		if (args.length == 2) {
			return Ranks.matchPossibleNodes(args[args.length - 1],
					Ranks.isActive() ? Ranks.INSTANCE.getPermissionNodes()
							: FTBUtilitiesPermissionHandler.INSTANCE.getRegisteredNodes());
		}

		return super.addTabCompletionOptions(sender, args);
	}

	@Override
	public boolean isUsernameIndex(String[] args, int index) {
		return index == 0;
	}

	@Override
	public void processCommand(ICommandSender sender, String[] args) throws CommandException {
		checkArgs(sender, args, 2);
		Rank rank = Ranks.INSTANCE.getRank(sender, args[0]);

		ConfigValue value = rank.getPermissionValue(args[1], args[1], true);

		IChatComponent valueText;

		if (value.isNull()) {
			valueText = FTBUtilities.lang(sender, "commands.ranks.none");
			valueText.getChatStyle().setColor(EnumChatFormatting.DARK_GRAY);
		} else if (value instanceof ConfigBoolean) {
			valueText = new ChatComponentText(value.getString());
			valueText.getChatStyle().setColor(value.getBoolean() ? EnumChatFormatting.GREEN : EnumChatFormatting.RED);
		} else {
			valueText = new ChatComponentText(value.getString());
			valueText.getChatStyle().setColor(EnumChatFormatting.BLUE);
		}

		IChatComponent nodeText = new ChatComponentText(args[1]);
		nodeText.getChatStyle().setColor(EnumChatFormatting.GOLD);

		IChatComponent nameText = rank.getDisplayName().createCopy();
		nameText.getChatStyle().setColor(EnumChatFormatting.DARK_GREEN);

		sender.addChatMessage(
				FTBUtilities.lang(sender, "commands.ranks.get_permission.text", nodeText, nameText, valueText));
	}
}
