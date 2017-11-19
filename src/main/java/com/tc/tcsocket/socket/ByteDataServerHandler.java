package com.tc.tcsocket.socket;

import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.util.Date;

import org.apache.log4j.Logger;

import com.tc.tcsocket.model.Member;
import com.tc.tcsocket.utils.DataChangeUtils;
import com.tc.tcsocket.utils.JedisUtil;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.ChannelMatcher;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.timeout.IdleStateEvent;
import redis.clients.jedis.Jedis;

/*
 * netty 事件执行顺序：
 * 
当客户端连接时
1、initChannel（）在ChannelInitializer里执行
2、channelRegistered
3、channelActive
当客户端发送数据时
4、channelRead
5、channelReadComplete
当客户端段开时
1、channelReadComplete
2、channelInactive
3、channelUnregistered
 * 
 */
public class ByteDataServerHandler extends ChannelInboundHandlerAdapter implements IMyChannelHandler {

	private Logger log = Logger.getLogger(this.getClass());
	/*
	 * void channelRegistered(ChannelHandlerContext ctx) throws Exception; void
	 * channelUnregistered(ChannelHandlerContext ctx) throws Exception; void
	 * channelActive(ChannelHandlerContext ctx) throws Exception; void
	 * channelInactive(ChannelHandlerContext ctx) throws Exception; void
	 * inboundBufferUpdated(ChannelHandlerContext ctx) throws Exception;
	 */

	private String channelName = "";
	private String channelAddressName = "";
	SocketChannel channel = null;

	@Override
	public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
		ctx.fireChannelRegistered();
		// super.channelRegistered(ctx);
		this.channel = (SocketChannel) ctx.channel();
		System.out.println("channelRegistered");
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		super.channelActive(ctx);
		log.info("new clienet:" + ctx.channel().remoteAddress());
		System.out.println("channelActive:remote address:" + ctx.channel().remoteAddress());
		// register channelMap
		
		

	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		super.channelInactive(ctx);
		System.out.println("channelInactive:"+ ctx.channel().remoteAddress());
		// SocketServer.channelMap.remove(this.channelAddressName);

	}

	/**
	 * 当完成消息帧解析等对消息的过滤操作时发生!!!!!!!!!!,
	 * 所以，并不是客户端每次发送数据都会调用，channelReadComplete有调用，本事件不一定调用
	 */
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		UNCONNECT_NUM_S=0;
		System.out.println("channelRead");
		// 进入数据解析
		Analysis analysis = new Analysis(this);
		analysis.protocolCheck(ctx, msg);
		System.out.println("protocolChecking");
		// super.channelRead(ctx, msg);//打开这个，将导致 refCnt: 0错误!!!!!!!
		// System.out.println("channelRead");
		try {

			/*
			 * ByteBuf result = (ByteBuf) msg; byte[] result1 = new
			 * byte[result.readableBytes()]; System.out.println(
			 * "channelRead byte length " + result1.length); //
			 * msg中存储的是ByteBuf类型的数据，把数据读取到byte[]中 result.readBytes(result1);
			 * 
			 * System.out.println(DataChangeUtils.getHexStringFromByteArray(
			 * result1, " "));
			 * 
			 * 
			 * String resultStr = new String(result1);// 16进制数组变成字符串
			 * 
			 * 
			 * 
			 * System.out.println("Client said:" + resultStr);
			 * 
			 * for (java.util.Map.Entry<String, Channel> entry :
			 * SocketServer.channelMap.entrySet()) {
			 * 
			 * String brocaststr = "client:" + entry.getValue().remoteAddress()
			 * + "said:" + resultStr;
			 * 
			 * ByteBuf brocaststr_encoded = ctx.alloc().buffer(4 *
			 * brocaststr.length());
			 * brocaststr_encoded.writeBytes(brocaststr.getBytes());
			 * entry.getValue().writeAndFlush(brocaststr_encoded);
			 * 
			 * System.out.println(entry.getValue().remoteAddress() +
			 * "channelMap' channels all be writed msg!"); }
			 * 
			 * // 释放资源，这行很关键 result.release(); String response = "I am ok!"; //
			 * 在当前场景下，发送的数据必须转换成ByteBuf数组 ByteBuf encoded = ctx.alloc().buffer(4
			 * * response.length()); encoded.writeBytes(response.getBytes());
			 * ctx.write(encoded); ctx.flush();
			 */
		} catch (Exception e) {
			System.out.println("channel read err:" + e.getMessage());
		}
	}

	/**
	 * 当客户端一次性发送数据结束
	 */
	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
		// TODO Auto-generated method stub

		super.channelReadComplete(ctx);
		System.out.println("channelReadComplete");
		//ctx.flush();
	}

	@Override
	public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {

		/*
		 * ctx.fireChannelUnregistered(); SocketChannel ch = (SocketChannel)
		 * ctx.channel(); String channelname =
		 * ch.remoteAddress().getHostString() + ch.remoteAddress().getPort();
		 */

		if (this.channelName != null && ChannelsManager.channelRegistMap.containsKey(this.channelName))

			ChannelsManager.removeChannelForAll(this.channelName, this.channelAddressName);
		System.out.println("Regist channel size:" + ChannelsManager.channelRegistMap.size());
	}

	/**
	 * 保存登录连接信息
	 * 
	 * @param name
	 */
	public void saveChannelToMap(String name) {
		// TODO Auto-generated method stub
		this.channelName = name;

		ChannelsManager.addChannelToAll(this.channel, this.channelName);

	}

	private int UNCONNECT_NUM_S = 0;

	@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
		if (evt instanceof IdleStateEvent) {
			if (UNCONNECT_NUM_S >= 4) {
				System.err.println("client connect status is disconnect.");
				ChannelsManager.removeChannelForAll(this.channelName, this.channelAddressName);
                //关闭连接
				ctx.close();
				// 此处当重启次数达到4次之后，关闭此链接后，清除服务端相关的记录信息
				return;
			}

			IdleStateEvent e = (IdleStateEvent) evt;
			switch (e.state()) {
			case WRITER_IDLE: // 客户端发送超时
				System.out.println("send ping to client---date=" + new Date());
				// 这个时候，服务端发送一个探视连接请求，与期待客户端回应
				if (ChannelsManager.checkChannelMapHasChannel((SocketChannel) ctx.channel())) {
					Analysis.hartReturn(ctx, (byte) 0x00);

				} else {
					//关闭未登录的连接
					ctx.close();
				}
				UNCONNECT_NUM_S++;
				System.err.println("writer_idle over. and UNCONNECT_NUM_S=" + UNCONNECT_NUM_S);
				break;
			case READER_IDLE:
				System.err.println("reader_idle over.");
				UNCONNECT_NUM_S++;
				// 读取服务端消息超时时，直接断开该链接，并重新登录请求，建立通道
			case ALL_IDLE:
				System.err.println("all_idle over.");
				UNCONNECT_NUM_S++;
				// 读取服务端消息超时时，直接断开该链接，并重新登录请求，建立通道
			default:
				break;
			}
		}
	}

}
