package org.shareData.acceptor.one;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.shareData.acceptor.PaxosAgreement;
import org.shareData.acceptor.agreement.AccAgreement;
import org.shareData.acceptor.agreement.AccState;
import org.shareData.acceptor.agreement.HeatUp;
import org.shareData.acceptor.i.PaxosType;

import io.netty.channel.ChannelHandlerContext;

public class PaxosBodys {
	private static final PaxosBodys BODY = new PaxosBodys();
	private static Map<Long, PaxosType> paxosMap = new ConcurrentHashMap<>();
	private static List<HeatUp> heatUps = new ArrayList<>();
	private static int porpareNub = 0;
	private static Map<Long, Long> businessMap = new ConcurrentHashMap<>();
	static final Logger logger = LogManager.getLogger(PaxosBodys.class);
	private static final int BUSQ_MAX = 0x3FFFFFFF;// 序列最大值
	public synchronized AccState paxosGo(AccAgreement agreement) {
		long bussinessId = agreement.getBusinessId();
		AccState state = null;
		if (paxosMap.containsKey(bussinessId)) {// 内存中已存在此选举状态
			PaxosType paxos = paxosMap.get(bussinessId);
			state = paxos.paxosDo(agreement);
		} else {// 内存中不存在当前业务号的选举状态，创建一个新的选举类
			long oldBusinessId = bussinessId >> 30L;
			if (businessMap.containsKey(oldBusinessId)) {
				long bus = businessMap.get(oldBusinessId);
				if (bus == BUSQ_MAX) {
					bus = 1;
				}
				if (((bussinessId & BUSQ_MAX) - bus == 1) || (bus == 1 && (bussinessId & BUSQ_MAX) == 1)) {
					if (bus > 1) {
						bus++;
					}
					businessMap.put(oldBusinessId, bus);
					logger.info("新的业务ID:{}", bussinessId);
					PaxosType paxos = new PaxosAgreement(porpareNub);
					state = paxos.paxosDo(agreement);
					paxosMap.put(bussinessId, paxos);
				}
			} else {
				businessMap.put(oldBusinessId, 1L);
				logger.info("新的业务ID:{}", bussinessId);
				PaxosType paxos = new PaxosAgreement(porpareNub);
				state = paxos.paxosDo(agreement);
				paxosMap.put(bussinessId, paxos);
			}

		}

		return state;
	}

	public void compare(ChannelHandlerContext ctx) {
		for (HeatUp up : heatUps) {
			if (up.getCtx() == ctx) {
				long requestNub = up.getRequestTimes();
				long responseNub = up.getResponseTimes();
				requestNub = responseNub;
				up.setRequestTimes(requestNub);
				break;
			}
		}
	}

	public int getPropareNub() {
		return porpareNub;
	}

	public void porpareDown() {
		porpareNub--;
	}

	public synchronized void add(ChannelHandlerContext ctx) {
		porpareNub++;
		// HeatUp up = new HeatUp();
		// up.setCtx(ctx);
		// heatUps.add(up);
	}

	public List<HeatUp> getHeat() {
		return heatUps;
	}

	public PaxosType getPaxos(long bussinessId) {
		return paxosMap.get(bussinessId);
	}

	public void removePaxos(long bussinessId) {
		paxosMap.remove(bussinessId);
	}

	private PaxosBodys() {
	};

	public static PaxosBodys get() {
		return BODY;
	}
}
