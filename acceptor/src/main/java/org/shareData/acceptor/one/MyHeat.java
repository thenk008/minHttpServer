package org.shareData.acceptor.one;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.shareData.acceptor.agreement.HeatUp;

public class MyHeat implements Runnable {
	static final Logger logger = LogManager.getLogger(MyHeat.class);

	@Override
	public void run() {
		try {
			Thread.sleep(5000);
			List<HeatUp> list = PaxosBodys.get().getHeat();
			for (int i = 0; i < list.size(); i++) {
				long requestNub = list.get(i).getRequestTimes();
				long responseNub = list.get(i).getResponseTimes();
				responseNub++;
				if ((responseNub - requestNub) > 30) {// 断线
					logger.warn("有Porpare掉线了");
					PaxosBodys.get().porpareDown();
					list.remove(i);
					i--;
				}

			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
