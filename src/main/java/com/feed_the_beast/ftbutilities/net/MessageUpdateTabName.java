package com.feed_the_beast.ftbutilities.net;

import com.feed_the_beast.ftblib.lib.io.DataIn;
import com.feed_the_beast.ftblib.lib.io.DataOut;
import com.feed_the_beast.ftblib.lib.net.MessageToClient;
import com.feed_the_beast.ftblib.lib.net.NetworkWrapper;
import com.feed_the_beast.ftblib.lib.util.NBTUtils;
import com.feed_the_beast.ftbutilities.FTBUtilitiesConfig;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;


import java.util.UUID;

public class MessageUpdateTabName extends MessageToClient {
	private UUID playerId;
	private IChatComponent displayName;
	private boolean afk, rec;

	public MessageUpdateTabName() {
	}

	public MessageUpdateTabName(EntityPlayerMP player) {
		playerId = player.getUniqueID();
		displayName = new ChatComponentText(player.getDisplayName());
		afk = (System.currentTimeMillis() - player.func_154331_x()) >= FTBUtilitiesConfig.afk
				.getNotificationTimer();
		rec = NBTUtils.getPersistedData(player, false).getBoolean("recording");
	}

	@Override
	public NetworkWrapper getWrapper() {
		return FTBUtilitiesNetHandler.GENERAL;
	}

	@Override
	public void writeData(DataOut data) {
		data.writeUUID(playerId);
		data.writeTextComponent(displayName);
		data.writeBoolean(afk);
		data.writeBoolean(rec);
	}

	@Override
	public void readData(DataIn data) {
		playerId = data.readUUID();
		displayName = data.readTextComponent();
		afk = data.readBoolean();
		rec = data.readBoolean();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void onMessage() {
		return;
//		NetworkPlayerInfo info = Minecraft.getMinecraft().thePlayer.connection.getPlayerInfo(playerId);

//		if (info == null) {
//			return;
//		}

//		IChatComponent component = new ChatComponentText("");
//
//		if (rec) {
//			IChatComponent component1 = new ChatComponentText("[REC]");
//			component1.getChatStyle().setColor(EnumChatFormatting.RED);
//			component1.getChatStyle().setBold(true);
//			component.appendSibling(component1);
//		}
//
//		if (afk) {
//			IChatComponent component1 = new ChatComponentText("[AFK]");
//			component1.getChatStyle().setColor(EnumChatFormatting.GRAY);
//			component.appendSibling(component1);
//		}
//
//		if (afk || rec) {
//			component.appendText(" ");
//		}
//
//		component.appendSibling(displayName);
//		info.setDisplayName(component);
	}
}
