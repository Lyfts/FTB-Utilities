package com.feed_the_beast.ftbutilities;

import com.feed_the_beast.ftblib.lib.data.ForgePlayer;
import com.feed_the_beast.ftblib.lib.math.Ticks;
import com.feed_the_beast.ftbutilities.data.Leaderboard;
import com.feed_the_beast.ftbutilities.events.LeaderboardRegistryEvent;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.stats.StatList;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.ResourceLocation;

import java.util.Comparator;

/**
 * @author LatvianModder
 */
//@Mod.EventBusSubscriber(modid = FTBUtilities.MOD_ID)
public class FTBUtilitiesLeaderboards {
	@SubscribeEvent
	public static void registerLeaderboards(LeaderboardRegistryEvent event) {
		event.register(new Leaderboard.FromStat(new ResourceLocation(FTBUtilities.MOD_ID, "deaths"), StatList.deathsStat,
				false, Leaderboard.FromStat.DEFAULT));
		event.register(new Leaderboard.FromStat(new ResourceLocation(FTBUtilities.MOD_ID, "mob_kills"),
				StatList.mobKillsStat, false, Leaderboard.FromStat.DEFAULT));
		event.register(new Leaderboard.FromStat(new ResourceLocation(FTBUtilities.MOD_ID, "time_played"),
				StatList.minutesPlayedStat, false, Leaderboard.FromStat.TIME));
		event.register(new Leaderboard.FromStat(new ResourceLocation(FTBUtilities.MOD_ID, "jumps"), StatList.jumpStat,
				false, Leaderboard.FromStat.DEFAULT));

		event.register(new Leaderboard(
				new ResourceLocation(FTBUtilities.MOD_ID, "deaths_per_hour"),
				new ChatComponentTranslation("ftbutilities.stat.dph"),
				player -> {
					double d = getDPH(player);
					return new ChatComponentText(d < 0D ? "-" : String.format("%.2f", d));
				},
				Comparator.comparingDouble(FTBUtilitiesLeaderboards::getDPH).reversed(),
				player -> getDPH(player) >= 0D));

		event.register(new Leaderboard(
				new ResourceLocation(FTBUtilities.MOD_ID, "last_seen"),
				new ChatComponentTranslation("ftbutilities.stat.last_seen"),
				player -> {
					if (player.isOnline()) {
						IChatComponent component = new ChatComponentTranslation("gui.online");
						component.getChatStyle().setColor(EnumChatFormatting.GREEN);
						return component;
					} else {
						long worldTime = player.team.universe.world.getTotalWorldTime();
						int time = (int) (worldTime - player.getLastTimeSeen());
						return Leaderboard.FromStat.TIME.apply(time);
					}
				},
				Comparator.comparingLong(FTBUtilitiesLeaderboards::getRelativeLastSeen),
				player -> player.getLastTimeSeen() != 0L));
	}

	private static long getRelativeLastSeen(ForgePlayer player) {
		if (player.isOnline()) {
			return 0;
		}

		return player.team.universe.ticks.ticks() - player.getLastTimeSeen();
	}

	private static double getDPH(ForgePlayer player) {
		int playTime = player.stats().writeStat(StatList.minutesPlayedStat);

		if (playTime > 0) {
			double hours = Ticks.get(playTime).hoursd();

			if (hours >= 1D) {
				return (double) player.stats().writeStat(StatList.damageDealtStat) / hours;
			}
		}

		return -1D;
	}
}
