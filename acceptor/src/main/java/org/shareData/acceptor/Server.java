package org.shareData.acceptor;

import java.net.InetSocketAddress;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

public class Server {
	static final Logger logger = LogManager.getLogger(Server.class);
	public  void acceptorStart(long host) {
		EventLoopGroup group = new NioEventLoopGroup();
		EventLoopGroup boss = new NioEventLoopGroup();
		try {
			ServerBootstrap b = new ServerBootstrap();
			b.group(group, boss).channel(NioServerSocketChannel.class).childOption(ChannelOption.TCP_NODELAY, true)
			.handler(new LoggingHandler(LogLevel.INFO))
			.childHandler(new Et());
			logger.info("Acceptor Server Start");
			Channel nel = b.bind(new InetSocketAddress((int)host)).sync().channel();
			logger.info("Acceptor Port:{} Open ",host);
			nel.closeFuture().sync();
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			group.shutdownGracefully();
			boss.shutdownGracefully();
		}
	}

}
class Et extends ChannelInitializer<SocketChannel> {

	@Override
	protected void initChannel(SocketChannel ch) throws Exception {
		ByteBuf def = Unpooled.copiedBuffer("$_".getBytes());
		ch.pipeline().addLast(new DelimiterBasedFrameDecoder(1024,def));
		ch.pipeline().addLast(new StringDecoder());
		ch.pipeline().addLast(new PlayServer());
		
	}

}