package com.feed_the_beast.ftbutilities.events;

import java.util.function.Consumer;

import com.feed_the_beast.ftbutilities.data.Leaderboard;

/**
 * @author LatvianModder
 */
public class LeaderboardRegistryEvent extends FTBUtilitiesEvent {

    private final Consumer<Leaderboard> callback;

    public LeaderboardRegistryEvent(Consumer<Leaderboard> c) {
        callback = c;
    }

    public void register(Leaderboard entry) {
        callback.accept(entry);
    }
}
