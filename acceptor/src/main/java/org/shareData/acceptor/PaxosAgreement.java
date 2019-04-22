package org.shareData.acceptor;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.shareData.acceptor.agreement.AccAgreement;
import org.shareData.acceptor.agreement.AccState;
import org.shareData.acceptor.agreement.StateValue;
import org.shareData.acceptor.i.PaxosType;
import org.shareData.acceptor.one.PaxosBodys;

public class PaxosAgreement implements PaxosType {
	private AccAgreement ment = null;
	public int porpareNub = 0;
	private int requestNub = 0;// 结束进程
	private Lock lock1 = new ReentrantLock();
	private Lock lock2 = new ReentrantLock();

	public PaxosAgreement(int porpareNub) {
		this.porpareNub = porpareNub;
	}

	static final Logger logger = LogManager.getLogger(PaxosAgreement.class);

	public AccState paxosCall1(AccAgreement body) {// 处理paxos一致性协议 一阶段提交 Acceptor 处理
		if (porpareNub < 5) {
			logger.info("1当前服务数量异常:d{}", porpareNub);
		}
		AccState state = new AccState();
		try {
			lock1.lock();
			long acceptorId = body.getMaxKey();
			state.setRequest(StateValue.PREPARE_ONE);
			state.setBussinessId(body.getBusinessId());
			if (ment != null) {// 当前业务序列存在了
				if (ment.getMaxKey() > acceptorId) {// 当前业务序列内的ID值更大，返回失败
					logger.info("ID:{},新编号更小拒绝,oldid:{}", acceptorId, ment.getMaxKey());
					state.setState(StateValue.ERROR_NOTACCEPT);
					state.setAccerptKey(ment.getMaxKey());
					state.setAccerptVal(ment.getAccerptVal());
				} else {// 当前业务序列内的ID更小 返回肯定相应
					logger.info("ID:{},更新新的内容", acceptorId);
					state.setState(StateValue.POK);
					if (ment.getAccerptKey() > 0) {// 当前存在一个提案,回应原提案
						state.setAccerptKey(ment.getAccerptKey());
						state.setAccerptVal(ment.getAccerptVal());
						logger.info("ID:{},更新新的内容1", acceptorId);
					}
					ment.setMaxKey(acceptorId);
				}
			} else {// 当前业务序列并不存在已经接受的编号
				logger.info("ID:{},第一次获取当前业务序列编号,业务序列为：d{}", acceptorId, body.getBusinessId());
				ment = new AccAgreement();
				/*
				 * ment.setMaxKey(acceptorId); ment.setAccerptKey(acceptorId);
				 * ment.setAccerptVal(body.getAccerptVal());
				 */
				state.setState(StateValue.OK);
			}
		} catch (Exception e) {
			lock1.unlock();
			e.printStackTrace();
		} finally {
			lock1.unlock();
		}
		return state;
	}

	public AccState paxosCall2(AccAgreement body) {// 处理paxos一致性协议 二阶段提交
		AccState state = new AccState();
		if (porpareNub < 5) {
			logger.info("2当前服务数量异常:d{}", porpareNub);
		}
		try {
			lock2.lock();
			long acceptorId = body.getMaxKey();
			state.setRequest(StateValue.PREPARE_TWO);
			state.setBussinessId(body.getBusinessId());
			if (ment == null) {
				logger.warn("ment is null");
			}

			if (ment.getMaxKey() > body.getMaxKey()) {// 本次二段提交失败
				state.setState(StateValue.ERROR_NOTANSER);
				state.setAccerptKey(ment.getMaxKey());
				state.setAccerptVal(ment.getAccerptVal());
				logger.info("ID:{},二段提交内容失败,老的ID:{}", acceptorId, ment.getMaxKey());
			} else {// 二段提交成功
				state.setState(StateValue.AOK);
				state.setAccerptKey(body.getAccerptKey());
				state.setAccerptVal(body.getAccerptVal());
				ment.setMaxKey(body.getMaxKey());
				ment.setAccerptKey(body.getAccerptKey());
				ment.setAccerptVal(body.getAccerptVal());
				logger.info("ID:{},二段提交内容成功", acceptorId);
			}

		} catch (Exception e) {
			lock2.unlock();
			e.printStackTrace();
		} finally {
			lock2.unlock();
		}
		return state;
	}

	@Override
	public AccState paxosDo(AccAgreement body) {
		byte type = body.getRequest();
		AccState state = null;
		if (StateValue.PREPARE_ONE == type) {// 一阶段提交
			state = paxosCall1(body);
		} else if (StateValue.PREPARE_TWO == type) {// 二阶段提交
			state = paxosCall2(body);
		} else {// 修改接受请求链接数值
			requestNub++;
			logger.info("一位propare完成案例");
			if (requestNub == PaxosBodys.get().getPropareNub()) {// 结束
				state = new AccState();
				state.setIsEnd(1);
				state.setBussinessId(body.getBusinessId());
			}
		}
		return state;
	}
}
