package com.feed_the_beast.ftbutilities.handlers;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.feed_the_beast.ftblib.events.client.CustomClickEvent;
import com.feed_the_beast.ftblib.lib.client.ClientUtils;
import com.feed_the_beast.ftblib.lib.icon.Icon;
import com.feed_the_beast.ftblib.lib.math.Ticks;
import com.feed_the_beast.ftblib.lib.util.StringUtils;
import com.feed_the_beast.ftbutilities.FTBUtilities;
import com.feed_the_beast.ftbutilities.FTBUtilitiesConfig;
import com.feed_the_beast.ftbutilities.client.FTBUtilitiesClient;
import com.feed_the_beast.ftbutilities.client.FTBUtilitiesClientConfig;
import com.feed_the_beast.ftbutilities.gui.GuiClaimedChunks;
import com.feed_the_beast.ftbutilities.net.MessageEditNBTRequest;
import com.feed_the_beast.ftbutilities.net.MessageLeaderboardList;
import com.feed_the_beast.ftbutilities.net.MessageRequestBadge;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.network.FMLNetworkEvent;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.stats.StatList;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.client.event.RenderGameOverlayEvent;

/**
 * @author LatvianModder
 */
public class FTBUtilitiesClientEventHandler {
	public static final FTBUtilitiesClientEventHandler INST = new FTBUtilitiesClientEventHandler();
	private static final Map<UUID, Icon> BADGE_CACHE = new HashMap<>();
	public static long shutdownTime = 0L;
	public static int currentPlaytime = 0;

	public static void readSyncData(NBTTagCompound nbt) {
		shutdownTime = System.currentTimeMillis() + nbt.getLong("ShutdownTime");
	}

	public static Icon getBadge(UUID id) {
		Icon tex = BADGE_CACHE.get(id);

		if (tex == null) {
			tex = Icon.EMPTY;
			BADGE_CACHE.put(id, tex);
			new MessageRequestBadge(id).sendToServer();
		}

		return tex;
	}

	public static void setBadge(UUID id, String url) {
		BADGE_CACHE.put(id, Icon.getIcon(url));
	}

	@SubscribeEvent
	public void onClientDisconnected(FMLNetworkEvent.ClientDisconnectionFromServerEvent event) {
		BADGE_CACHE.clear();
		shutdownTime = 0L;
	}

	@SubscribeEvent
	public void onDebugInfoEvent(RenderGameOverlayEvent.Text event) {
		if (Minecraft.getMinecraft().gameSettings.showDebugInfo) {
			return;
		}

		if (shutdownTime > 0L && FTBUtilitiesClientConfig.general.show_shutdown_timer) {
			long timeLeft = Math.max(0L, shutdownTime - System.currentTimeMillis());

			if (timeLeft > 0L && timeLeft <= FTBUtilitiesClientConfig.general.getShowShutdownTimer()) {
				event.left.add(EnumChatFormatting.DARK_RED
						+ I18n.format("ftbutilities.lang.timer.shutdown", StringUtils.getTimeString(timeLeft)));
			}
		}

		if (FTBUtilitiesConfig.world.show_playtime) {
			event.left
					.add(StatList.minutesPlayedStat.func_150951_e().getUnformattedText() + ": " + Ticks
							.get(Minecraft.getMinecraft().thePlayer.getStatFileWriter().writeStat(StatList.minutesPlayedStat))
							.toTimeString());
		}
	}

	@SubscribeEvent
	public void onKeyEvent(InputEvent.KeyInputEvent event) {
		if (FTBUtilitiesClient.KEY_NBT.isPressed()) {
			MessageEditNBTRequest.editNBT();
		}

		if (FTBUtilitiesClient.KEY_TRASH.isPressed()) {
			ClientUtils.execClientCommand("/trash_can");
		}
	}

	@SubscribeEvent
	public void onCustomClick(CustomClickEvent event) {
		if (event.getID().getResourceDomain().equals(FTBUtilities.MOD_ID)) {
			switch (event.getID().getResourcePath()) {
				case "toggle_gamemode":
					ClientUtils.execClientCommand("/gamemode "
							+ (Minecraft.getMinecraft().thePlayer.capabilities.isCreativeMode ? "survival" : "creative"));
					break;
				case "daytime":
					long addDay = (24000L - (Minecraft.getMinecraft().theWorld.getWorldTime() % 24000L)
							+ FTBUtilitiesClientConfig.general.button_daytime) % 24000L;

					if (addDay != 0L) {
						ClientUtils.execClientCommand("/time add " + addDay);
					}

					break;
				case "nighttime":
					long addNight = (24000L - (Minecraft.getMinecraft().theWorld.getWorldTime() % 24000L)
							+ FTBUtilitiesClientConfig.general.button_nighttime) % 24000L;

					if (addNight != 0L) {
						ClientUtils.execClientCommand("/time add " + addNight);
					}

					break;
				case "claims_gui":
					GuiClaimedChunks.instance = new GuiClaimedChunks();
					GuiClaimedChunks.instance.openGui();
					break;
				case "leaderboards_gui":
					new MessageLeaderboardList().sendToServer();
					break;
			}

			event.setCanceled(true);
		}
	}

	@SubscribeEvent
	public void onClientWorldTick(TickEvent.ClientTickEvent event) {
		Minecraft mc = Minecraft.getMinecraft();

		if (event.phase == TickEvent.Phase.START && mc.theWorld != null
				&& mc.theWorld.provider.dimensionId == FTBUtilitiesConfig.world.spawn_dimension) {
			if (FTBUtilitiesConfig.world.forced_spawn_dimension_time != -1) {
				mc.theWorld.setWorldTime(FTBUtilitiesConfig.world.forced_spawn_dimension_time);
			}

			if (FTBUtilitiesConfig.world.forced_spawn_dimension_weather != -1) {
				mc.theWorld.getWorldInfo().setRaining(FTBUtilitiesConfig.world.forced_spawn_dimension_weather >= 1);
				mc.theWorld.getWorldInfo().setThundering(FTBUtilitiesConfig.world.forced_spawn_dimension_weather >= 2);
			}
		}
	}
}
