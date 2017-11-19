package com.tc.tcsocket.socket;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.log4j.Logger;

import com.tc.tcsocket.model.Member;
import com.tc.tcsocket.utils.DataChangeUtils;
import com.tc.tcsocket.utils.JedisUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.SocketChannel;

/**
 * 数据解析
 * 
 * @author yeluxing
 *
 */
public class Analysis {

	public static long countreceivedata = 0L;

	private static SqlSessionFactory sqlSessionFactory;

	private static byte COMMAND_FLAG = (byte) 0x7e;
	IMyChannelHandler imChannelHandler;
	JedisUtil jedisUtil = JedisUtil.getInstance();
	private static Logger log = Logger.getLogger(Analysis.class);

	static {
		sqlSessionFactory = MySqlSessionFactory.getInstance().getSqlSessionFactory();
		System.out.print(" Analysis static completed!");
	}

	public Analysis(IMyChannelHandler imChannelHandler) {
		this.imChannelHandler = imChannelHandler;
	}

	/**
	 * 协议判断，分解释
	 * 
	 * @param ctx
	 * @param msg
	 */
	public void protocolCheck(ChannelHandlerContext ctx, Object msg) {
		System.out.println("protocolCheck......");
		ByteBuf result = (ByteBuf) msg;
		byte[] byteMsg = new byte[result.readableBytes()];
		result.readBytes(byteMsg);
		System.out.println("收到：" + DataChangeUtils.getHexStringFromByteArray(byteMsg, " "));
		System.out.println("收到数：" + countreceivedata);
		countreceivedata++;
		if (countreceivedata > java.lang.Math.pow(2, 50)) {
			countreceivedata = 0;
		}

		if (byteMsg[0] == (byte) 0x7e) {
			if (byteMsg.length >= 2) {
				byte protocolId = byteMsg[1];
				switch (protocolId) {
				default:
					logincheck(ctx, getMemberId(byteMsg), true);
					break;
				case (byte) 0xdd: // 登录
					login(ctx, byteMsg);
					break;
				case (byte) 0xe1:// 命令控制
					if (logincheck(ctx, getMemberId(byteMsg), true))
						controlCommand(ctx, byteMsg);
					break;
				case (byte) 0xe0:// 心跳包
					if (logincheck(ctx, getMemberId(byteMsg), true))
						hartReturn(ctx, (byte) 0x01);
					break;
				case (byte) 0xe3:// 回报终端类型
					if (logincheck(ctx, getMemberId(byteMsg), true))
						terminalatTypeCommand(ctx, byteMsg);
					break;

				}

			} else {
				log.error("收到的数据长度不符合要求:" + byteMsg.length);

			}

		}

	}

	/**
	 * 从消息获取用户id
	 * 
	 * @return
	 */
	private static String getMemberId(byte[] byteMsg) {
		if (byteMsg.length < 5) {
			log.error("数据不包含用户信息");
			return "";

		}
		byte[] send_memberid_byte = new byte[5];
		int j = 0;
		for (int i = 2; i <= 6; i++) {
			send_memberid_byte[j] = byteMsg[i];
			j++;

		}
		String send_memberid = String.valueOf(DataChangeUtils.getLongFromBytes(send_memberid_byte, false, 5));

		return send_memberid;
	}

	/**
	 * 发送控制命令
	 * 
	 * @param ctx
	 * @param byteMsg
	 */
	private void controlCommand(ChannelHandlerContext ctx, byte[] byteMsg) {
		// 获取发送方id
		byte[] send_memberid_byte = new byte[5];
		int j = 0;
		for (int i = 2; i <= 6; i++) {
			send_memberid_byte[j] = byteMsg[i];
			j++;

		}
		long send_memberid = DataChangeUtils.getLongFromBytes(send_memberid_byte, false, 5);

		j = 0;
		byte[] receive_memberid_byte = new byte[5];
		for (int i = 7; i <= 11; i++) {
			receive_memberid_byte[j] = byteMsg[i];
			j++;

		}
		long receive_memberid = DataChangeUtils.getLongFromBytes(receive_memberid_byte, false, 5);

		// 检验关系
		// 读取被控方的好友绑定信息
		JedisUtil jedisUtil = JedisUtil.getInstance();
		System.out.print("receive_memberid:" + receive_memberid);
		String cacheRequestMemebrId = ((List<String>) jedisUtil.HASH.hmget(String.valueOf(receive_memberid),
				"request_memberid")).get(0);
		// cacheRequestMemebrId != null &&
		// cacheRequestMemebrId.equals(String.valueOf(send_memberid))
		if (true) {
			// 关系验证通过
			String channelregistMapKey = String.valueOf(receive_memberid);
			SocketChannel receiveChannel = ChannelsManager.getRegistChannel(channelregistMapKey);
			if (receiveChannel != null) {
				byteMsg[1] = (byte) 0xe2;
				byte[] byteMsgToCommand = new byte[byteMsg.length];
				for (int i = 0; i < byteMsg.length - 2; i++) {
					byteMsgToCommand[i] = byteMsg[i];
				}
				byteMsgToCommand[byteMsgToCommand.length - 2] = DataChangeUtils.aXOR(byteMsgToCommand);
				byteMsgToCommand[byteMsgToCommand.length - 1] = COMMAND_FLAG;
				// 发送控制
				directWriteByte(receiveChannel, byteMsgToCommand);
				// 回应客户端，发送成功
				directWriteByte((SocketChannel) ctx.channel(), controlCommandReturn((byte) 0x01));
			} else {
				// 对方不在线
				directWriteByte((SocketChannel) ctx.channel(), controlCommandReturn((byte) 0x03));

			}
		} else {
			// 关系验证失败
			directWriteByte((SocketChannel) ctx.channel(), controlCommandReturn((byte) 0x04));

		}

	}

	/**
	 * 发送终端类型给对方
	 * 
	 * @param ctx
	 * @param byteMsg
	 */
	private void terminalatTypeCommand(ChannelHandlerContext ctx, byte[] byteMsg) {
		// 获取发送方id
		byte[] send_memberid_byte = new byte[5];
		int j = 0;
		for (int i = 2; i <= 6; i++) {
			send_memberid_byte[j] = byteMsg[i];
			j++;

		}
		
		long send_memberid = DataChangeUtils.getLongFromBytes(send_memberid_byte, false, 5);
        System.out.print("send_memberid:" + send_memberid+"\n");
		long receive_memberid = 0;
		// query member_pairs for the other party's member_id;

		SqlSession sqlSession = sqlSessionFactory.openSession();
		receive_memberid = sqlSession.selectOne("com.tc.tcsocket.dao.IMemberDao.queryOtherpartyMemberId",
				send_memberid);
		System.out.print("~~~~receive_memberid:~~~~~~~~~" + receive_memberid+"\n");
		
		if (receive_memberid>0) {
			//System.out.print("~~~~receive_memberid:~~~~~~~~~" + receive_memberid+"\n");
			String channelregistMapKey = String.valueOf(receive_memberid);
			SocketChannel receiveChannel = ChannelsManager.getRegistChannel(channelregistMapKey);
			if (receiveChannel != null) {
				byteMsg[1] = (byte) 0xe4;//服务端转发命令
				byte[] byteMsgToCommand = new byte[byteMsg.length];
				for (int i = 0; i < byteMsg.length - 2; i++) {
					byteMsgToCommand[i] = byteMsg[i];
				}
				byteMsgToCommand[byteMsgToCommand.length - 2] = DataChangeUtils.aXOR(byteMsgToCommand);
				byteMsgToCommand[byteMsgToCommand.length - 1] = COMMAND_FLAG;
				// 发送类型汇报
				directWriteByte(receiveChannel, byteMsgToCommand);
				// 回应客户端，发送成功
				directWriteByte((SocketChannel) ctx.channel(), termianlTypeReturn((byte) 0x01));
			} else {
				// 对方不在线
				directWriteByte((SocketChannel) ctx.channel(), termianlTypeReturn((byte) 0x03));

			}
		} else {
			// 关系验证失败
			directWriteByte((SocketChannel) ctx.channel(), termianlTypeReturn((byte) 0x04));

		}

	}

	/**
	 * 控制命令服务端回应
	 * 
	 * @param returnFlag
	 * @return
	 */
	private byte[] controlCommandReturn(byte returnFlag) {
		byte[] ru = new byte[5];
		ru[0] = COMMAND_FLAG;
		ru[1] = (byte) 0xe1;
		ru[2] = returnFlag;
		ru[3] = DataChangeUtils.aXOR(ru);
		ru[4] = COMMAND_FLAG;
		return ru;

	}
	
	/**
	 * 终端类型命令服务端回应
	 * 
	 * @param returnFlag
	 * @return
	 */
	private byte[] termianlTypeReturn(byte returnFlag) {
		byte[] ru = new byte[5];
		ru[0] = COMMAND_FLAG;
		ru[1] = (byte) 0xe3;
		ru[2] = returnFlag;
		ru[3] = DataChangeUtils.aXOR(ru);
		ru[4] = COMMAND_FLAG;
		return ru;

	}
	
	

	/**
	 * 是否已经登录验证
	 * 
	 * @param ctx
	 * @param byteMsg
	 */
	private boolean logincheck(ChannelHandlerContext ctx, String memberid, boolean whenNoLoginedClose) {
		// ？？?

		boolean logined = false;

		String cache_memberid = (jedisUtil.HASH.hmget(memberid, "id")).get(0);
		if (cache_memberid != null) {
			logined = true;
		} else {
			if (whenNoLoginedClose) {
				ctx.close();
				log.error("非法用户" + memberid + "  " + ctx.channel().remoteAddress());
			}
		}
		return logined;
	}

	/**
	 * 用户登录
	 * 
	 * @param ctx
	 * @param msg
	 */
	private void login(ChannelHandlerContext ctx, byte[] byteMsg) {

		// 获取用户ID和手机号
		System.out.println("login......");

		byte[] mobileBytes = new byte[6];
		int j = 0;
		for (int i = 2; i <= 7; i++) {
			mobileBytes[j] = byteMsg[i];
			j++;

		}
		String mobile = DataChangeUtils.getHexStringFromByteArray(mobileBytes, "");
		System.out.println("mobile is:" + mobile);

		// 获取用户id

		byte[] memberid_byte = new byte[5];
		j = 0;
		for (int i = 8; i <= 12; i++) {
			memberid_byte[j] = byteMsg[i];
			j++;

		}
		long memberid = DataChangeUtils.getLongFromBytes(memberid_byte, false, 5);
		System.out.println("memberid byte hex is:" + DataChangeUtils.getHexStringFromByteArray(memberid_byte, " "));
		System.out.println("memberid is:" + memberid);

		// 判断用户是否正确

		// 查找缓存
		String cacheMemberId = (jedisUtil.HASH.hmget(String.valueOf(memberid), "id")).get(0);
		/*
		 * if (cacheMemberId!=null && !cacheMemberId.equals("")) {
		 * 
		 * } else
		 */
		{
			// select语句，传入两个参数，返回Member对象
			// --------------------------------------------------------------------------

			Map<String, String> selectParams = new HashMap<String, String>();
			selectParams.put("id", String.valueOf(memberid));
			System.out.println(mobile.substring(0, 1));
			mobile = (mobile.substring(0, 1).equals("0") ? mobile.substring(1) : mobile);
			selectParams.put("mobile", mobile);
			System.out.println("memberid is:" + memberid + "   " + mobile);

			SqlSession sqlSession = sqlSessionFactory.openSession();
			Member member = sqlSession.selectOne("com.tc.tcsocket.dao.IMemberDao.queryMemberByIdAndMobile",
					selectParams);
			if (member != null) {
				System.out.println("***********sex is:" + member.getSex() + " photo is:" + member.getPhoto()
						+ " request_member is:" + member.getRequest_memberid());
				// 写入redis

				Map<String, String> mapMember = new HashMap<String, String>();

				mapMember.put("id", String.valueOf(memberid));
				mapMember.put("mobile", mobile);
				mapMember.put("sex", String.valueOf(member.getSex()));
				mapMember.put("request_memberid", String.valueOf(member.getRequest_memberid()));
				mapMember.put("pair_memberid", "");// 匹配的会员id
				mapMember.put("apply_status", String.valueOf(member.getApply_status()));
				// 保存cache
				jedisUtil.HASH.hmset(String.valueOf(memberid), mapMember);
				System.out.println("保存用户完成！");

				// 从缓存取出数据
				/*
				 * List<String> cache_request_memberid =
				 * jedisUtil.HASH.hmget(String.valueOf(memberid),
				 * "request_memberid"); String request_member_info =
				 * cache_request_memberid.get(0); System.out.println(
				 * "从redis提起的request_member_info： "+ request_member_info);
				 */

				// 测试写入
				/*
				 * Map<String, String> selectParamstest = new HashMap<String,
				 * String>(); selectParamstest.put("str", "str aaaccccccccnew "
				 * + (new Date()).getTime());
				 * sqlSession.insert("com.tc.tcsocket.dao.IMemberDao.addtest",
				 * selectParamstest); System.out.println("insert complete!" );
				 * sqlSession.commit();
				 */
				// 测试写入完

				sqlSession.close();

			} else {
				// 返回客户端失败
				directWriteByte((SocketChannel) ctx.channel(), loginReturn(false));
				// 断开
				ctx.close();
				return;
			}
		}
		// 保存连接
		imChannelHandler.saveChannelToMap(String.valueOf(memberid));
		// 返回客户端登录成功
		directWriteByte((SocketChannel) ctx.channel(), loginReturn(true));

	}

	/**
	 * channel发送字节流
	 * 
	 * @param ctx
	 * @param msg
	 */
	public static void directWriteByte(SocketChannel ctx, byte[] msg) {

		ByteBuf encoded = ctx.alloc().buffer(msg.length);
		encoded.writeBytes(msg);
		ctx.writeAndFlush(encoded);
		System.out.println("directWriteByte to " + ctx.remoteAddress() + " complete!");
	}

	/**
	 * 登录回应消息
	 * 
	 * @return
	 */
	private static byte[] loginReturn(boolean issucc) {
		byte[] ru = new byte[5];
		ru[0] = COMMAND_FLAG;
		ru[1] = (byte) 0xdd;
		if (issucc)
			ru[2] = (byte) 0x01;
		else
			ru[2] = (byte) 0x00;
		ru[3] = DataChangeUtils.aXOR(ru);
		ru[4] = COMMAND_FLAG;

		return ru;

	}

	/**
	 * 心跳包回应
	 * 
	 * @param ctx
	 * @param msg
	 */
	public static void hartReturn(ChannelHandlerContext ctx, byte msg) {
		// 获取用户id
		// ???
		// 更新更新缓存时间
		// ???
		byte[] sendmsg = new byte[5];
		sendmsg[0] = COMMAND_FLAG;
		sendmsg[1] = (byte) 0xe0;
		sendmsg[2] = msg;
		sendmsg[3] = DataChangeUtils.aXOR(sendmsg);
		sendmsg[4] = COMMAND_FLAG;
		directWriteByte((SocketChannel) ctx.channel(), sendmsg);

	}

}
