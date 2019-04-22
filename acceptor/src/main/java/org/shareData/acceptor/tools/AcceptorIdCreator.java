package org.shareData.acceptor.tools;

public class AcceptorIdCreator {
	// 生成分布式全局唯一ID
	public static final long sequenceBits = 10L;// 序列掩码位数
	public static final long workBits = 4L;// 分布式机器号共四位
	public static final long businessBits = 8L;// 业务号位数
	public static long workId = 0L;
	public static boolean isOk = false;
	public static final long workAndBusBites = workBits + businessBits;
	public static final long allBites = workAndBusBites + sequenceBits;
	public static final long businessMask = -1L ^ (-1L << businessBits);// 业务号极值
	private static final long workMask = -1L ^ (-1L << workBits);// 机器号极值
	private static final long sequenceMask = -1L ^ (-1L << sequenceBits);// 序列掩码极值
	private static final AcceptorIdCreator AcceptorId = new AcceptorIdCreator();
	private static long sequence = 0L;
	private static long lastTimestamp = -1L;

	private AcceptorIdCreator() {

	}

	public long init(long workIds) {
		if (workIds <= workMask) {// 非法
			workId = workIds;
			isOk = true;
		} else {
			workIds = 0;
		}
		return workIds;
	}

	public synchronized long nextId(int businessId) {
		if (!isOk) {
			throw new RuntimeException("AcceptorServer is not init");
		}
		long timestamp = timeGen();
		if (timestamp < lastTimestamp) {
			throw new RuntimeException(String.format(
					"Clock moved backwards.  Refusing to generate id for %d milliseconds", lastTimestamp - timestamp));
		}
		if (businessId > businessMask) {
			throw new RuntimeException("businessId is too large==" + businessId);
		}
		if (lastTimestamp == timestamp) {
			sequence = (sequence + 1) & sequenceMask;
			if (sequence == 0) {
				timestamp = tilNextMillis(lastTimestamp);
			}
		} else {
			sequence = 0L;
		}
		lastTimestamp = timestamp;
		return (timestamp << allBites) | (sequence << workAndBusBites) | (businessId << workBits) | workId;

	}

	protected long tilNextMillis(long lastTimestamp) {
		long timestamp = timeGen();
		while (timestamp <= lastTimestamp) {
			timestamp = timeGen();
		}
		return timestamp;
	}

	protected long timeGen() {
		return System.currentTimeMillis();
	}

	public static AcceptorIdCreator get() {
		return AcceptorId;
	}
}
