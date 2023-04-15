package com.feed_the_beast.ftbutilities.net;

import com.feed_the_beast.ftblib.lib.client.ClientUtils;
import com.feed_the_beast.ftblib.lib.net.MessageToClient;
import com.feed_the_beast.ftblib.lib.net.NetworkWrapper;
import com.feed_the_beast.ftblib.lib.util.StringJoiner;
import net.minecraft.client.Minecraft;
import net.minecraft.util.MovingObjectPosition;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * @author LatvianModder
 */
public class MessageEditNBTRequest extends MessageToClient {
	public MessageEditNBTRequest() {
	}

	@Override
	public NetworkWrapper getWrapper() {
		return FTBUtilitiesNetHandler.FILES;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void onMessage() {
		editNBT();
	}

	@SideOnly(Side.CLIENT)
	public static void editNBT() {
		MovingObjectPosition ray = Minecraft.getMinecraft().objectMouseOver;

		if (ray != null) {
			if (ray.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
				ClientUtils.execClientCommand(StringJoiner.with(' ').joinObjects("/nbtedit block",
						ray.blockX, ray.blockY, ray.blockZ));
			} else if (ray.typeOfHit == MovingObjectPosition.MovingObjectType.ENTITY && ray.entityHit != null) {
				ClientUtils.execClientCommand("/nbtedit entity " + ray.entityHit.getEntityId());
			}
		}
	}
}
