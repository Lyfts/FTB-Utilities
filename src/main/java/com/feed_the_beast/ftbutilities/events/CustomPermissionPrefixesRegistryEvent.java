package com.feed_the_beast.ftbutilities.events;

import java.util.function.Consumer;

import com.feed_the_beast.ftblib.lib.util.permission.DefaultPermissionLevel;
import com.feed_the_beast.ftbutilities.data.NodeEntry;

/**
 * @author LatvianModder
 */
public class CustomPermissionPrefixesRegistryEvent extends FTBUtilitiesEvent
{
	private final Consumer<NodeEntry> callback;

	public CustomPermissionPrefixesRegistryEvent(Consumer<NodeEntry> c)
	{
		callback = c;
	}

	public void register(NodeEntry entry)
	{
		callback.accept(entry);
	}

	public void register(String node, DefaultPermissionLevel level, String desc)
	{
		callback.accept(new NodeEntry(node, level, desc));
	}
}