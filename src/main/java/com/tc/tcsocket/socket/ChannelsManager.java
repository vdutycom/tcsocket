package com.tc.tcsocket.socket;

import java.util.concurrent.ConcurrentHashMap;

import io.netty.channel.Channel;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.unix.Socket;

public class ChannelsManager {
	
	public static ConcurrentHashMap<String, SocketChannel> channelRegistMap = new ConcurrentHashMap<String, SocketChannel>();
    public static ConcurrentHashMap<String, SocketChannel> channelMap =  new ConcurrentHashMap<String, SocketChannel>();
	
    public static void addChannelToAll(SocketChannel ch,String channelanme)
    {
    	channelRegistMap.put(channelanme,ch);
    	channelMap.put(ch.remoteAddress().toString(), ch);
    }
    
    public static void removeChannelForAll(String channelName, String channelAddressName)
    {
    	try
    	{
    	channelRegistMap.remove(channelName);
		channelMap.remove(channelAddressName);
    	}
    	catch(Exception e)
    	{
    		
    	}
    }
    
    public static boolean checkChannelMapHasChannel(SocketChannel ch)
    {
    	String key  = ch.remoteAddress().toString();
    	if (channelMap.get(key)!=null )
    	{
    		return true;
    	}
    	return false;
    }
    
    public static  SocketChannel getRegistChannel(String key)
    {
    	 return channelRegistMap.get(key);
    }
    
    

}
