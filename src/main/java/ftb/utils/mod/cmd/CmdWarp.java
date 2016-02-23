package ftb.utils.mod.cmd;

import ftb.lib.*;
import ftb.lib.api.cmd.*;
import ftb.utils.mod.FTBU;
import ftb.utils.world.FTBUWorldDataMP;
import net.minecraft.command.*;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.*;

public class CmdWarp extends CommandLM
{
	public CmdWarp()
	{ super("warp", CommandLevel.ALL); }
	
	public String getCommandUsage(ICommandSender ics)
	{ return '/' + commandName + " <ID>"; }
	
	public String[] getTabStrings(ICommandSender ics, String[] args, int i) throws CommandException
	{
		if(i == 0) return FTBUWorldDataMP.inst.warps.list();
		return super.getTabStrings(ics, args, i);
	}
	
	public IChatComponent onCommand(ICommandSender ics, String[] args) throws CommandException
	{
		checkArgs(args, 1);
		if(args[0].equals("list"))
		{
			String[] list = FTBUWorldDataMP.inst.warps.list();
			if(list.length == 0) return new ChatComponentText("-");
			return new ChatComponentText(joinNiceString(list));
		}
		
		EntityPlayerMP ep = getCommandSenderAsPlayer(ics);
		BlockDimPos p = FTBUWorldDataMP.inst.warps.get(args[0]);
		if(p == null) return error(new ChatComponentTranslation(FTBU.mod.assets + "cmd.warp_not_set", args[0]));
		LMDimUtils.teleportPlayer(ep, p);
		return new ChatComponentTranslation(FTBU.mod.assets + "cmd.warp_tp", args[0]);
	}
}