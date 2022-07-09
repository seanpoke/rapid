package com.ruyuan.rapid.disruptor.quicklystart;

import com.lmax.disruptor.EventFactory;

/**
 * <B>主类名称：</B>OrderEventFactory<BR>
 * <B>概要说明：</B>ds的事件工厂类<BR>
 * @author JiFeng
 * @since 2021年12月6日 上午12:05:37
 */
public class OrderEventFactory implements EventFactory<OrderEvent>{

	@Override
	public OrderEvent newInstance() {
		return new OrderEvent();
	}

}
