package com.feed_the_beast.ftbutilities.command.tp;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.event.HoverEvent;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;

import com.feed_the_beast.ftblib.lib.command.CmdBase;
import com.feed_the_beast.ftblib.lib.command.CommandUtils;
import com.feed_the_beast.ftblib.lib.math.TeleporterDimPos;
import com.feed_the_beast.ftblib.lib.util.StringUtils;
import com.feed_the_beast.ftbutilities.FTBUtilities;
import com.feed_the_beast.ftbutilities.FTBUtilitiesPermissions;
import com.feed_the_beast.ftbutilities.data.FTBUtilitiesPlayerData;

/**
 * @author LatvianModder
 */
public class CmdTPAccept extends CmdBase {

    public CmdTPAccept() {
        super("tpaccept", Level.ALL);
    }

    @Override
    public boolean isUsernameIndex(String[] args, int index) {
        return index == 0;
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) throws CommandException {
        checkArgs(sender, args, 1);
        EntityPlayerMP selfPlayer = getCommandSenderAsPlayer(sender);
        FTBUtilitiesPlayerData self = FTBUtilitiesPlayerData.get(CommandUtils.getForgePlayer(selfPlayer));
        FTBUtilitiesPlayerData other = FTBUtilitiesPlayerData.get(CommandUtils.getForgePlayer(sender, args[0]));

        IChatComponent selfName = StringUtils
                .color(new ChatComponentText(self.player.getPlayer().getDisplayName()), EnumChatFormatting.BLUE);
        IChatComponent otherName = StringUtils
                .color(new ChatComponentText(other.player.getPlayer().getDisplayName()), EnumChatFormatting.BLUE);

        if (self.player.equalsPlayer(other.player) || !other.player.isOnline()
                || !self.tpaRequestsFrom.contains(other.player)) {
            throw FTBUtilities.error(sender, "ftbutilities.lang.tpa.no_request", otherName);
        }

        if (selfPlayer.dimension != other.player.getPlayer().dimension
                && !other.player.hasPermission(FTBUtilitiesPermissions.TPA_CROSS_DIM)) {
            other.player.getPlayer().addChatMessage(
                    StringUtils.color(
                            FTBUtilities.lang(other.player.getPlayer(), "ftbutilities.lang.homes.cross_dim"),
                            EnumChatFormatting.RED));
            throw FTBUtilities.error(sender, "ftbutilities.lang.homes.cross_dim", otherName);
        }

        self.tpaRequestsFrom.remove(other.player);

        IChatComponent component = FTBUtilities.lang(sender, "ftbutilities.lang.tpa.request_accepted");
        component.getChatStyle().setChatHoverEvent(
                new HoverEvent(
                        HoverEvent.Action.SHOW_TEXT,
                        FTBUtilities.lang(sender, "ftbutilities.lang.tpa.from_to", otherName, selfName)));
        sender.addChatMessage(component);

        component = FTBUtilities.lang(other.player.getPlayer(), "ftbutilities.lang.tpa.request_accepted");
        component.getChatStyle().setChatHoverEvent(
                new HoverEvent(
                        HoverEvent.Action.SHOW_TEXT,
                        FTBUtilities
                                .lang(other.player.getPlayer(), "ftbutilities.lang.tpa.from_to", otherName, selfName)));
        other.player.getPlayer().addChatMessage(component);

        FTBUtilitiesPlayerData.Timer.TPA
                .teleport(other.player.getPlayer(), playerMP -> TeleporterDimPos.of(selfPlayer), null);
    }
}
