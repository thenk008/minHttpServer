package org.shareData.acceptor;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.shareData.acceptor.agreement.AccAgreement;
import org.shareData.acceptor.agreement.AccState;
import org.shareData.acceptor.one.PaxosBodys;

import com.alibaba.fastjson.JSONObject;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class PlayServer extends SimpleChannelInboundHandler<Object> {
	static final String NAME = "$_";
	static final Logger logger = LogManager.getLogger(PlayServer.class);

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {

	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		// 链接激活将机器号发送过来
		// 准备开始发送心跳
		PaxosBodys.get().add(ctx);
	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
		// IO读发生
		String ms = (String) msg;
			logger.info("收到的请求,json:{}", ms);
			AccAgreement agreement = JSONObject.parseObject(ms, AccAgreement.class);
			AccState stat = PaxosBodys.get().paxosGo(agreement);
			if (stat != null) {
				if (stat.getIsEnd() != 1) {
					String st = JSONObject.toJSONString(stat) + NAME;
					ByteBuf bu = Unpooled.copiedBuffer(st.getBytes());
					ctx.writeAndFlush(bu);
				} else {
					// 移除。。
					logger.info("移除案例");
					long bussinessId = stat.getBussinessId();
					PaxosBodys.get().removePaxos(bussinessId);
				}
			}
		
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		logger.warn("warn:{}", cause.getMessage());
	}

	@Override
	public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
		logger.warn("远程链接被强制关闭");
	}

}
