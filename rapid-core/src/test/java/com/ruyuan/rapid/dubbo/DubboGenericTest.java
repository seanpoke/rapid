package com.ruyuan.rapid.dubbo;


import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;

import org.junit.Test;

import com.ruyuan.rapid.common.config.DubboServiceInvoker;
import com.ruyuan.rapid.common.util.FastJsonConvertUtil;
import com.ruyuan.rapid.core.context.DubboRequest;
import com.ruyuan.rapid.core.helper.DubboReferenceHelper;

public class DubboGenericTest {

	//	telnet localhost 32131
	//	invoke com.rapid.test.dubbo.service.HelloService.sayHelloUser("aaa")
	//	invoke com.rapid.test.dubbo.service.HelloService.sayHelloUser({"id":"100","name":"z3"},"aaa")
	
	@Test
	public void testSayHelloC() throws InterruptedException {
		CountDownLatch countDownLatch = new CountDownLatch(1);
		DubboServiceInvoker dubboServiceInvoker = new DubboServiceInvoker();
		dubboServiceInvoker.setInvokerPath("/sayHelloUser/c");
		dubboServiceInvoker.setRegisterAddress("zookeeper://192.168.11.111:2181?backup=192.168.11.112:2181,192.168.11.113:2181");
        dubboServiceInvoker.setInterfaceClass("com.ruyuan.test.dubbo.service.HelloService");
        dubboServiceInvoker.setMethodName("sayHelloUser");
        dubboServiceInvoker.setParameterTypes(new String[] {"java.lang.String"});
        dubboServiceInvoker.setTimeout(5000);
        dubboServiceInvoker.setVersion(null);
        String paramStr = "[\"1234\"]";
        List<Object> list = FastJsonConvertUtil.convertJSONToArray(paramStr, Object.class);
        
        
//		Object[] parameters = new Object[1];
//		parameters[0] = "1234";
//		System.err.println("parameters: " + FastJsonConvertUtil.convertObjectToJSON(parameters));

        
        DubboRequest dubboRequest = DubboReferenceHelper.buildDubboRequest(dubboServiceInvoker, list.toArray());
        CompletableFuture<Object> completableFuture = DubboReferenceHelper.getInstance()
                .$invokeAsync(null, dubboRequest);
        
    	completableFuture.whenCompleteAsync((retValue, throwable) -> {
        	System.err.println(retValue);
        	System.err.println(throwable);
        	countDownLatch.countDown();
        });
		
    	countDownLatch.await();
	}
	
	@Test
	public void testSayHelloB() throws InterruptedException {
		CountDownLatch countDownLatch = new CountDownLatch(1);
		DubboServiceInvoker dubboServiceInvoker = new DubboServiceInvoker();
		dubboServiceInvoker.setInvokerPath("/sayHelloUser/b");
		dubboServiceInvoker.setRegisterAddress("zookeeper://192.168.11.111:2181?backup=192.168.11.112:2181,192.168.11.113:2181");
        dubboServiceInvoker.setInterfaceClass("com.rapid.test.dubbo.service.HelloService");
        dubboServiceInvoker.setMethodName("sayHelloUser");
        dubboServiceInvoker.setParameterTypes(new String[] {"com.rapid.test.dubbo.service.User", "java.lang.String"});
        dubboServiceInvoker.setTimeout(5000);
        dubboServiceInvoker.setVersion(null);
        
        
//		Object[] parameters = new Object[2];
//		Object obj = FastJsonConvertUtil.convertJSONToObject("{\"id\":\"001\", \"name\" : \"张三\"}", Object.class);
//		parameters[0] = obj;
//		parameters[1] = "1234";
//		System.err.println("parameters: " + FastJsonConvertUtil.convertObjectToJSON(parameters));
        
        String paramStr = "[{\"id\":\"001\", \"name\" : \"张三\"}, \"xiaoxiao\"]";
        List<Object> list = FastJsonConvertUtil.convertJSONToArray(paramStr, Object.class);
        
        
        DubboRequest dubboRequest = DubboReferenceHelper.buildDubboRequest(dubboServiceInvoker, list.toArray());
        CompletableFuture<Object> completableFuture = DubboReferenceHelper.getInstance()
                .$invokeAsync(null, dubboRequest);
        
    	completableFuture.whenCompleteAsync((retValue, throwable) -> {
        	System.err.println("retValue:" + retValue);
        	System.err.println("throwable:" + throwable);
        	countDownLatch.countDown();
        });
		
    	countDownLatch.await();
	}
	
	@Test
	public void testBalance() {
		//	rlb=com.bfxy.rapid.core.balance.DubboLoadBalance
		
	}
}
