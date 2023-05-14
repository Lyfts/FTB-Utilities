package com.feed_the_beast.ftbutilities.command.chunks;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;

import com.feed_the_beast.ftblib.FTBLib;
import com.feed_the_beast.ftblib.lib.command.CmdBase;
import com.feed_the_beast.ftblib.lib.command.CommandUtils;
import com.feed_the_beast.ftblib.lib.data.ForgePlayer;
import com.feed_the_beast.ftblib.lib.math.ChunkDimPos;
import com.feed_the_beast.ftblib.lib.util.text_components.Notification;
import com.feed_the_beast.ftbutilities.FTBUtilities;
import com.feed_the_beast.ftbutilities.FTBUtilitiesNotifications;
import com.feed_the_beast.ftbutilities.FTBUtilitiesPermissions;
import com.feed_the_beast.ftbutilities.data.ClaimedChunks;

/**
 * @author LatvianModder
 */
public class CmdLoad extends CmdBase {

    public CmdLoad() {
        super("load", Level.ALL);
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) throws CommandException {
        if (!ClaimedChunks.isActive()) {
            throw FTBLib.error(sender, "feature_disabled_server");
        }

        EntityPlayerMP player = getCommandSenderAsPlayer(sender);
        ForgePlayer p = CommandUtils.getForgePlayer(player);
        ChunkDimPos pos = new ChunkDimPos(player);

        if (p.hasTeam() && ClaimedChunks.instance.canPlayerModify(p, pos, FTBUtilitiesPermissions.CLAIMS_OTHER_LOAD)
                && ClaimedChunks.instance.loadChunk(p, p.team, pos)) {
            Notification
                    .of(
                            FTBUtilitiesNotifications.CHUNK_MODIFIED,
                            FTBUtilities.lang(player, "ftbutilities.lang.chunks.chunk_loaded"))
                    .send(player.mcServer, player);
            FTBUtilitiesNotifications.updateChunkMessage(player, pos);
        } else {
            FTBUtilitiesNotifications.sendCantModifyChunk(player.mcServer, player);
        }
    }
}
