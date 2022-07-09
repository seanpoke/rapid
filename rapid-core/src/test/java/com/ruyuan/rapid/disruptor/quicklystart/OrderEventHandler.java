package com.ruyuan.rapid.disruptor.quicklystart;

import org.apache.commons.lang3.RandomUtils;

import com.lmax.disruptor.EventHandler;

/**
 * <B>主类名称：</B>OrderEventHandler<BR>
 * <B>概要说明：</B>事件<BR>
 * @author JiFeng
 * @since 2021年12月6日 上午12:09:16
 */
public class OrderEventHandler implements EventHandler<OrderEvent> {

	@Override
	public void onEvent(OrderEvent event, long sequence, boolean endOfBatch) throws Exception {
		Thread.sleep(RandomUtils.nextInt(1, 100));
		System.err.println("消费者消费：" + event.getValue());
	}

}
