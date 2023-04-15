package com.feed_the_beast.ftbutilities.command;

import com.feed_the_beast.ftblib.lib.command.CmdBase;
import com.feed_the_beast.ftblib.lib.command.CommandUtils;
import com.feed_the_beast.ftblib.lib.data.ForgePlayer;
import com.feed_the_beast.ftblib.lib.util.StringUtils;
import com.feed_the_beast.ftbutilities.FTBUtilities;
import com.feed_the_beast.ftbutilities.FTBUtilitiesConfig;
import com.feed_the_beast.ftbutilities.FTBUtilitiesPermissions;
import com.feed_the_beast.ftbutilities.data.FTBUtilitiesPlayerData;
import com.feed_the_beast.ftbutilities.net.MessageUpdateTabName;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.EnumChatFormatting;

public class CmdNick extends CmdBase {
	public CmdNick() {
		super("nick", Level.ALL);
	}

	@Override
	public void processCommand(ICommandSender sender, String[] args) throws CommandException {
		ForgePlayer player = CommandUtils.getForgePlayer(sender);

		if (!player.hasPermission(FTBUtilitiesPermissions.CHAT_NICKNAME_SET)) {
			throw new CommandException("commands.generic.permission");
		}

		FTBUtilitiesPlayerData data = FTBUtilitiesPlayerData.get(player);
		data.setNickname(StringUtils.joinSpaceUntilEnd(0, args));

		if (data.getNickname().isEmpty()) {
			player.getPlayer()
					.addChatMessage(FTBUtilities.lang(player.getPlayer(), "ftbutilities.lang.nickname_reset"));
		} else {
			String name = StringUtils.addFormatting(data.getNickname());

			if (!player.hasPermission(FTBUtilitiesPermissions.CHAT_NICKNAME_COLORS)) {
				name = StringUtils.unformatted(name);
			} else if (name.indexOf(StringUtils.FORMATTING_CHAR) != -1) {
				name += EnumChatFormatting.RESET;
			}

			player.getPlayer().addChatMessage(
					FTBUtilities.lang(player.getPlayer(), "ftbutilities.lang.nickname_changed", name));
		}

		if (FTBUtilitiesConfig.chat.replace_tab_names) {
			new MessageUpdateTabName(player.getPlayer()).sendToAll();
		}
	}
}