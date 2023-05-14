package com.feed_the_beast.ftbutilities.command;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;

import com.feed_the_beast.ftblib.lib.command.CmdBase;
import com.feed_the_beast.ftblib.lib.util.NBTUtils;
import com.feed_the_beast.ftbutilities.data.FTBUtilitiesPlayerData;

public class CmdUnmute extends CmdBase {

    public CmdUnmute() {
        super("unmute", Level.OP);
    }

    @Override
    public boolean isUsernameIndex(String[] args, int index) {
        return index == 0;
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) throws CommandException {
        NBTUtils.getPersistedData(getPlayer(sender, args[0]), false).removeTag(FTBUtilitiesPlayerData.TAG_MUTED);
    }
}
