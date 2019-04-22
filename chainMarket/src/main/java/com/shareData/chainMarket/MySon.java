package com.shareData.chainMarket;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

import io.netty.channel.ChannelHandlerContext;

public abstract class MySon {
	private static Class<?> myboss;

	public Class<?> getMyboss() {
		return myboss;
	}

	public static void setMyboss(Class<?> mybos) {
		myboss = mybos;
	}

	public static void body(String body, Map<Object, Object> map, Http web, ChannelHandlerContext ch) {
		try {
			Method me = myboss.getMethod("body", String.class, Map.class);
			Method you = myboss.getMethod("head", Http.class, ChannelHandlerContext.class);
			Object obj = myboss.newInstance();
			you.invoke(obj, web, ch);
			me.invoke(obj, body, map);
		} catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException
				| IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
		}
	};

}
