package com.ruyuan.rapid.rolling;

import org.apache.commons.lang3.RandomUtils;

import com.ruyuan.rapid.core.rolling.RollingNumber;
import com.ruyuan.rapid.core.rolling.RollingNumberEvent;

public class RollingTest {
	
	public static final Integer windowSize = 10000;
	
	public static final Integer bucketsSize = 10;

	public static void main(String[] args) throws InterruptedException {
		
		//	默认是10个桶，所以每个桶是1秒
		RollingNumber rollingNumber = new RollingNumber(windowSize, bucketsSize, "hello:1.0.0", null);

		while (true) {
			rollingNumber.increment(RollingNumberEvent.SUCCESS);// 事件 + 增量
			Thread.sleep(RandomUtils.nextInt(1, 10));
		}
	}

}
