package com.tc.tcsocket.socket;

import java.io.File;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import com.tc.tcsocket.utils.JedisUtil;
import com.tc.tcsocket.utils.JedisUtil.Hash;
import com.tc.tcsocket.utils.JedisUtils;
import com.tc.tcsocket.utils.MyPath;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * 服务端 Editor:yeluxing
 * 
 */
public class SocketServer {

	private Logger log = Logger.getLogger(this.getClass());
	private int port;

	public SocketServer(int port) {
		this.port = port;
	}

	@SuppressWarnings("deprecation")
	public void run() throws Exception {

		/*
		 * EventLoops的目的是为Channel处理IO操作，一个EventLoop可以为多个Channel服务。
		 * EventLoopGroup会包含多个EventLoop。
		 */
		EventLoopGroup bossGroup = new NioEventLoopGroup(); // (1)
		EventLoopGroup workerGroup = new NioEventLoopGroup();
		try {
			/* Bootstrap，一个Netty应用通常由一个Bootstrap开始，它主要作用是配置整个Netty程序，串联起各个组件。 */
			/*
			 * Channel代表了一个Socket链接，或者其它和IO操作相关的组件，它和EventLoop一起用来参与IO处理。
			 * handler在初始化时就会执行，而childHandler会在客户端成功connect后才执行，这是两者的区别。
			 * 
			 */
			ServerBootstrap b = new ServerBootstrap(); // (2)
			b.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class) // (3)
					.childHandler(new SocketServerInitializer()) // (4)
					.option(ChannelOption.SO_BACKLOG, 65535) // (5)
					.childOption(ChannelOption.TCP_NODELAY, true).childOption(ChannelOption.SO_KEEPALIVE, true); // (6)

			try {
				String userdir = MyPath.getProjectPath();
				String jarfile = userdir + File.separatorChar + "tcsoket.jar";
				File jarfileobj = new File(jarfile);
				Calendar cal = Calendar.getInstance();
				long time = jarfileobj.lastModified();
				cal.setTimeInMillis(time);
				SimpleDateFormat formatter;
				formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				String ctime = formatter.format(time);
				// cal.getTime().toLocaleString()
				System.out.println("SocketServer 版本：" + ctime);
			} catch (Exception ex) {
				System.out.println("SocketServer 获取版本失败");
			}
			System.out.println("SocketServer 启动了" + this.port);
			log.info("TCServer 启动了" + this.port);
			// 绑定端口，开始接收进来的连接
			ChannelFuture f = b.bind(port).sync(); // (7)

			// 等待服务器 socket 关闭 。
			// 在这个例子中，这不会发生，但你可以优雅地关闭你的服务器。
			f.channel().closeFuture().sync();

		} finally {
			
			workerGroup.shutdownGracefully();
			bossGroup.shutdownGracefully();
			System.out.println("TCServer 关闭了");

		}
	}

	@SuppressWarnings("null")
	public static void main(String[] args) throws Exception {

		// path test
		System.out.println(MyPath.getAppPath(SocketServer.class));
		System.out.println(MyPath.getProjectPath());
		System.out.println(MyPath.getRealPath());

		// path test end
		/*
		 * // mybatis sessionfactory test for (int i = 0; i < 1000; i++) { final
		 * int j = i; final Thread t = new Thread(new Runnable() {
		 * 
		 * public void run() { // TODO Auto-generated method stub
		 * 
		 * MySqlSessionFactory mySqlSessionFactory =
		 * MySqlSessionFactory.getInstance(); SqlSessionFactory
		 * sqlSessionFactory = mySqlSessionFactory.getSqlSessionFactory(); if
		 * (sqlSessionFactory != null) {
		 * 
		 * // 判断用户是否正确 SqlSession sqlSession =
		 * sqlSessionFactory.openSession();// sqlSessionFactory.openSession();
		 * try { for (int h = 0;h<100; h++) { Map<String, String> selectParams =
		 * new HashMap<String, String>(); selectParams.put("str",
		 * "str aaaccccccccnew " + j);
		 * sqlSession.insert("com.tc.tcsocket.dao.IMemberDao.addtest",
		 * selectParams); System.out.println("insert complete!" + h);
		 * sqlSession.commit(); } } finally { sqlSession.close(); } } else {
		 * System.out.println("sqlSessionFactory is null"); }
		 * 
		 * } }); t.start();
		 * 
		 * }
		 */

		// end

		String userdir = MyPath.getProjectPath();
		String logconfigfile = userdir + File.separatorChar + "resources" + File.separatorChar + "log4j.properties";
		try {
			File file = new File("log4j.properties");
			if (!file.exists()) {
				file = new File(logconfigfile);
				if (file.exists())
					// 配置文件初始化
					PropertyConfigurator.configure(logconfigfile);
				else {
					logconfigfile = userdir + File.separatorChar + "src" + File.separator + "main" + File.separator
							+ "resources" + File.separatorChar + "log4j.properties";
					file = new File(logconfigfile);
					if (file.exists())
						// 配置文件初始化
						PropertyConfigurator.configure(logconfigfile);
				}
				System.out.println("configfile log4j complete:" + logconfigfile);
			}
		} catch (Exception e) {
			System.out.println("configfile log4j fail");
		}

		int port = 8080;
		/*
		 * if (args.length > 0) { port = Integer.parseInt(args[0]); } else
		 */ {
			String tcconfigfile = "tc.properties";
			InputStream in = com.tc.tcsocket.config.Config.getConfigStream(tcconfigfile);
			if (in != null) {
				Properties property = new Properties();
				property.load(in);
				String configPort = property.getProperty("socket.port");
				if (configPort != null && configPort != "") {
					System.out.println("socket.port:" + configPort);
					port = Integer.parseInt(configPort);
				} else

				{
					System.out.println("server port readfail!");
				}

			} else {
				port = 8080;
			}

			// redis test start

			/*
			 * 以下可用 JedisUtils.set("idfoyeluxing", "3506...", 3000); String
			 * idfoyeluxing = JedisUtils.get("idfoyeluxing");
			 * System.out.println("redis get key idfoyeluxing 's value ok! "
			 * +idfoyeluxing);
			 */
			/*
			 * System.out.println("111"); JedisUtil jedisUtil =
			 * JedisUtil.getInstance(); JedisUtil jedisUtil1 =
			 * JedisUtil.getInstance(); JedisUtil jedisUtil2 =
			 * JedisUtil.getInstance();
			 * 
			 * System.out.println("222"); if (jedisUtil == null) {
			 * 
			 * System.out.println("jedisUtil null");
			 * 
			 * } else {
			 * 
			 * if (jedisUtil.getJedis()==null) { System.out.println(
			 * "jedisUtil getJedis null"); }else { System.out.println(
			 * "jedisUtil getJedis not null"); }
			 * 
			 * Map<String, String> mapMember = new HashMap<String, String>();
			 * mapMember.put("name", "yeluxing"); mapMember.put("mobile",
			 * "13859988888");
			 * 
			 * jedisUtil.HASH.hmset("68", mapMember); List<String> memberinfo =
			 * jedisUtil.HASH.hmget("68", "mobile", "name"); for (String s :
			 * memberinfo) { System.out.println(s); } }
			 */
			// jedis test over

		}
		new SocketServer(port).run();

	}

}