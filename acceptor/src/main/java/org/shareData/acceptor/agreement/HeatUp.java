package org.shareData.acceptor.agreement;

import io.netty.channel.ChannelHandlerContext;

public class HeatUp {
private ChannelHandlerContext ctx;//链接上下文
private long requestTimes;//接收次数
private long responseTimes;//相应次数
private int id;//链接ID
public int getId() {
	return id;
}
public void setId(int id) {
	this.id = id;
}
public ChannelHandlerContext getCtx() {
	return ctx;
}
public void setCtx(ChannelHandlerContext ctx) {
	this.ctx = ctx;
}
public long getRequestTimes() {
	return requestTimes;
}
public void setRequestTimes(long requestTimes) {
	this.requestTimes = requestTimes;
}
public long getResponseTimes() {
	return responseTimes;
}
public void setResponseTimes(long responseTimes) {
	this.responseTimes = responseTimes;
}

}
