package com.ruyuan.rapid.disruptor.quicklystart;

/**
 * <B>主类名称：</B>OrderEvent<BR>
 * <B>概要说明：</B>这就是DS的RingBuffer处理的数据模型<BR>
 * @author JiFeng
 * @since 2021年12月6日 上午12:05:06
 */
public class OrderEvent {

	private long value;

	public long getValue() {
		return value;
	}

	public void setValue(long value) {
		this.value = value;
	}
	
}
