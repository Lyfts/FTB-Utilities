package ftb.utils.mod.cmd.admin;

import com.mojang.authlib.GameProfile;
import ftb.lib.*;
import ftb.lib.api.cmd.*;
import ftb.lib.api.item.StringIDInvLoader;
import ftb.lib.api.players.*;
import ftb.lib.mod.FTBLibFinals;
import latmod.lib.LMFileUtils;
import latmod.lib.json.UUIDTypeAdapterLM;
import net.minecraft.command.*;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.*;

import java.io.File;
import java.util.UUID;

public class CmdPlayerLM extends CommandSubLM
{
	public CmdPlayerLM()
	{
		super("player_lm", CommandLevel.OP);
		
		if(FTBLibFinals.DEV) add(new CmdAddFake("add_fake"));
		add(new CmdDelete("delete"));
		add(new CmdLoadInv("load_inv"));
		add(new CmdSaveInv("save_inv"));
	}
	
	public static class CmdAddFake extends CommandLM
	{
		public CmdAddFake(String s)
		{ super(s, CommandLevel.OP); }
		
		public String getCommandUsage(ICommandSender ics)
		{ return '/' + commandName + " <player>"; }
		
		public Boolean getUsername(String[] args, int i)
		{ return (i == 0) ? Boolean.FALSE : null; }
		
		public IChatComponent onCommand(ICommandSender ics, String[] args) throws CommandException
		{
			checkArgs(args, 2);
			
			UUID id = UUIDTypeAdapterLM.getUUID(args[0]);
			if(id == null) return error(new ChatComponentText("Invalid UUID!"));
			
			if(LMWorldMP.inst.getPlayer(id) != null || LMWorldMP.inst.getPlayer(args[1]) != null)
				return error(new ChatComponentText("Player already exists!"));
			
			LMPlayerMP p = new LMPlayerMP(new GameProfile(id, args[1]));
			LMWorldMP.inst.playerMap.put(p.getProfile().getId(), p);
			p.refreshStats();
			
			return new ChatComponentText("Fake player " + args[1] + " added!");
		}
	}
	
	public static class CmdDelete extends CommandLM
	{
		public CmdDelete(String s)
		{ super(s, CommandLevel.OP); }
		
		public String getCommandUsage(ICommandSender ics)
		{ return '/' + commandName + " <player>"; }
		
		public Boolean getUsername(String[] args, int i)
		{ return (i == 0) ? Boolean.FALSE : null; }
		
		public IChatComponent onCommand(ICommandSender ics, String[] args) throws CommandException
		{
			checkArgs(args, 1);
			LMPlayerMP p = LMPlayerMP.get(args[0]);
			if(p.isOnline()) return error(new ChatComponentText("The player must be offline!"));
			LMWorldMP.inst.playerMap.remove(p.getProfile().getId());
			return new ChatComponentText("Player removed!");
		}
	}
	
	public static class CmdLoadInv extends CommandLM
	{
		public CmdLoadInv(String s)
		{ super(s, CommandLevel.OP); }
		
		public String getCommandUsage(ICommandSender ics)
		{ return '/' + commandName + " <player>"; }
		
		public Boolean getUsername(String[] args, int i)
		{ return (i == 0) ? Boolean.FALSE : null; }
		
		public IChatComponent onCommand(ICommandSender ics, String[] args) throws CommandException
		{
			checkArgs(args, 1);
			LMPlayerMP p = LMPlayerMP.get(args[0]);
			if(!p.isOnline()) error(new ChatComponentText("The player must be online!"));
			
			try
			{
				EntityPlayerMP ep = p.getPlayer();
				String filename = ep.getName();
				if(args.length == 2) filename = "custom/" + args[1];
				NBTTagCompound tag = LMNBTUtils.readTag(new File(FTBLib.folderLocal, "ftbu/playerinvs/" + filename + ".dat"));
				
				StringIDInvLoader.readInvFromNBT(ep.inventory, tag, "Inventory");
				
				if(FTBLib.isModInstalled(OtherMods.BAUBLES))
					StringIDInvLoader.readInvFromNBT(BaublesHelper.getBaubles(ep), tag, "Baubles");
			}
			catch(Exception e)
			{
				if(FTBLibFinals.DEV) e.printStackTrace();
				return error(new ChatComponentText("Failed to load inventory!"));
			}
			
			return new ChatComponentText("Inventory loaded!");
		}
	}
	
	public static class CmdSaveInv extends CommandLM
	{
		public CmdSaveInv(String s)
		{ super(s, CommandLevel.OP); }
		
		public String getCommandUsage(ICommandSender ics)
		{ return '/' + commandName + " <player>"; }
		
		public Boolean getUsername(String[] args, int i)
		{ return (i == 0) ? Boolean.FALSE : null; }
		
		public IChatComponent onCommand(ICommandSender ics, String[] args) throws CommandException
		{
			checkArgs(args, 1);
			LMPlayerMP p = LMPlayerMP.get(args[0]);
			if(!p.isOnline()) error(new ChatComponentText("The player must be online!"));
			
			try
			{
				EntityPlayerMP ep = p.getPlayer();
				NBTTagCompound tag = new NBTTagCompound();
				StringIDInvLoader.writeInvToNBT(ep.inventory, tag, "Inventory");
				
				if(FTBLib.isModInstalled(OtherMods.BAUBLES))
					StringIDInvLoader.writeInvToNBT(BaublesHelper.getBaubles(ep), tag, "Baubles");
				
				String filename = ep.getName();
				if(args.length == 2) filename = "custom/" + args[1];
				LMNBTUtils.writeTag(LMFileUtils.newFile(new File(FTBLib.folderLocal, "ftbu/playerinvs/" + filename + ".dat")), tag);
			}
			catch(Exception e)
			{
				if(FTBLibFinals.DEV) e.printStackTrace();
				return error(new ChatComponentText("Failed to save inventory!"));
			}
			
			return new ChatComponentText("Inventory saved!");
		}
	}
}