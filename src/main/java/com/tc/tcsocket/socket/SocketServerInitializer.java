package com.tc.tcsocket.socket;


import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.timeout.IdleStateHandler;

/**
 * 服务端 ChannelInitializer
 * 
 * ChannelInitializer，当一个链接建立时，我们需要知道怎么来接收或者发送数据，当然，
 * 我们有各种各样的Handler实现来处理它，那么ChannelInitializer便是用来配置这些Handler，
 * 它会提供一个ChannelPipeline，并把Handler加入到ChannelPipeline。
 * 

 */
public class SocketServerInitializer extends
		ChannelInitializer<SocketChannel> {

	@Override
    public void initChannel(SocketChannel ch) throws Exception {
System.out.println("initChannel");  
		ChannelPipeline pipeline = ch.pipeline();
           
		 /*ChannelPipeline，一个Netty应用基于ChannelPipeline机制，
		  * 这种机制需要依赖于EventLoop和EventLoopGroup，
		  * 因为它们三个都和事件或者事件处理相关。*/
		 
		 /*DelimiterBasedFrameDecoder
		  * 对接收到的消息进行帧判断，以第二个参数"MyDelimiters.lineDelimiter()"的分隔符确定
		  * 当遇到分隔符时，则返回结果，执行channelRead事件（在其他处理器handler中）
		  *  
		  * */
        pipeline.addLast("framer", new DelimiterBasedFrameDecoder(2048, MyDelimiters.lineDelimiter()));
       /*
        *加这两个，消息将会变成字符串
        pipeline.addLast("decoder", new StringDecoder());
        pipeline.addLast("encoder", new StringEncoder());
        
        */
		pipeline.addLast(new IdleStateHandler(0,20,0));//（读服务器超时时间，写服务器超时时间，读写超时时间） 
        pipeline.addLast("handler", new ByteDataServerHandler());
        
       // pipeline.addLast("handler", new SimpleChatServerHandler());
 
		/*System.out.println("SimpleChatClient:"+ch.remoteAddress() +"连接上"  );
		String response = "welcome!" + ch.remoteAddress();  
	        // 在当前场景下，发送的数据必须转换成ByteBuf数组  
	        ByteBuf encoded = ch.alloc().buffer(4 * response.length());  
	        encoded.writeBytes(response.getBytes());  
	        ch.write(encoded);  
	        ch.flush();
	        SocketServer.channelMap.put(ch.remoteAddress().getHostString()+ch.remoteAddress().getPort(), ch);
	        
	        System.out.println("当前连接数："+ SocketServer.channelMap.size());
	       
	        for (java.util.Map.Entry<String, Channel> entry : SocketServer.channelMap.entrySet()) {
	            
	        	response = "client:"+entry.getValue().remoteAddress() +"connected!";
	        	encoded = ch.alloc().buffer(4 * response.length());  
	 	        encoded.writeBytes(response.getBytes()); 	        	
	        	entry.getValue().writeAndFlush(encoded );
	        	
	        	System.out.println(entry.getValue().remoteAddress() + "channelMap writed!" );
	        }*/
	                
	        
	        /*ChannelGroup channelGroup = 
	        		  new DefaultChannelGroup ("mychannelgroup", GlobalEventExecutor.INSTANCE); 
	        channelGroup.add(ch);
	        */
	        
		
    }
}
