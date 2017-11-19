package com.tc.tcsocket.socket;

import java.util.Collection;
import java.util.Iterator;

import io.netty.channel.Channel;
import io.netty.channel.ChannelId;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.ChannelGroupException;
import io.netty.channel.group.ChannelGroupFuture;
import io.netty.channel.group.ChannelMatcher;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.EventExecutor;

public class MyChannelGroup extends DefaultChannelGroup {

	public MyChannelGroup(String name, EventExecutor executor, boolean stayClosed) {
		super(name, executor, stayClosed);
		// TODO Auto-generated constructor stub
	}

	
}
