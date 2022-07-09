package com.ruyuan.rapid.core.netty.processor.filter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.ruyuan.rapid.common.util.ServiceLoader;
import com.ruyuan.rapid.core.context.Context;

import lombok.extern.slf4j.Slf4j;

/**
 * <B>主类名称：</B>DefaultProcessorFilterFactory<BR>
 * <B>概要说明：</B>默认过滤器工厂实现类<BR>
 * @author JiFeng
 * @since 2021年12月16日 上午1:14:06
 */
@Slf4j
public class DefaultProcessorFilterFactory extends AbstractProcessorFilterFactory {

	private static class SingletonHolder {
		private static final DefaultProcessorFilterFactory INSTANCE = new DefaultProcessorFilterFactory();
	}
	
	public static DefaultProcessorFilterFactory getInstance() {
		return SingletonHolder.INSTANCE;
	}
	
	//	构造方法：加载所有的ProcessorFilter子类的实现
	@SuppressWarnings("unchecked")
	private DefaultProcessorFilterFactory(){
		
		//	SPI方式加载filter的集合：
		Map<String , List<ProcessorFilter<Context>>> filterMap = new LinkedHashMap<String, List<ProcessorFilter<Context>>>();
		
		//	通过ServiceLoader加载
		@SuppressWarnings("rawtypes")
		ServiceLoader<ProcessorFilter> serviceLoader = ServiceLoader.load(ProcessorFilter.class);
		
		for(ProcessorFilter<Context> filter : serviceLoader) {
			Filter annotation = filter.getClass().getAnnotation(Filter.class);
			if(annotation != null) {
				String filterType = annotation.value().getCode();
				List<ProcessorFilter<Context>> filterList = filterMap.get(filterType);
				if(filterList == null) {
					filterList = new ArrayList<ProcessorFilter<Context>>();
				}
				filterList.add(filter);
				filterMap.put(filterType, filterList);
			}
		}
		
		//	java基础：枚举类循环也是有顺序的
		for(ProcessorFilterType filterType : ProcessorFilterType.values()) {
			List<ProcessorFilter<Context>> filterList = filterMap.get(filterType.getCode());
			if(filterList == null || filterList.isEmpty()) {
				continue;
			}
			
			Collections.sort(filterList, new Comparator<ProcessorFilter<Context>>() {
				@Override
				public int compare(ProcessorFilter<Context> o1, ProcessorFilter<Context> o2) {
					return o1.getClass().getAnnotation(Filter.class).order() - 
							o2.getClass().getAnnotation(Filter.class).order();
				}
			});
			
			try {
				super.buildFilterChain(filterType, filterList);
			} catch (Exception e) {
				//	ignor 
				log.error("#DefaultProcessorFilterFactory.buildFilterChain# 网关过滤器加载异常, 异常信息为：{}!",e.getMessage(), e);
			}
		}
		
	}
	
	/**
	 * <B>方法名称：</B>doFilterChain<BR>
	 * <B>概要说明：</B>正常过滤器链条执行：pre + route + post<BR>
	 * @author  JiFeng
	 * @since 2021年12月16日 上午1:48:34
	 * @see com.ruyuan.rapid.core.netty.processor.filter.ProcessorFilterFactory#doFilterChain(com.ruyuan.rapid.core.context.Context)
	 */
	@Override
	public void doFilterChain(Context ctx) throws Exception {
		try {
			defaultProcessorFilterChain.entry(ctx);
		} catch (Throwable e) {
			log.error("#DefaultProcessorFilterFactory.doFilterChain# ERROR MESSAGE: {}" , e.getMessage(), e);
			
			//	设置异常
			ctx.setThrowable(e);
			
			//	执行doFilterChain显示抛出异常时，Context上下文的生命周期为：Context.TERMINATED
			if(ctx.isTerminated()) {
				ctx.runned();
			}
			//	执行异常处理的过滤器链条
			doErrorFilterChain(ctx);
		}
	}

	/**
	 * <B>方法名称：</B>doErrorFilterChain<BR>
	 * <B>概要说明：</B>异常过滤器链条执行：error + post<BR>
	 * @author  JiFeng
	 * @since 2021年12月16日 上午1:48:12
	 * @see com.ruyuan.rapid.core.netty.processor.filter.ProcessorFilterFactory#doErrorFilterChain(com.ruyuan.rapid.core.context.Context)
	 */
	@Override
	public void doErrorFilterChain(Context ctx) throws Exception {
		try {
			errorProcessorFilterChain.entry(ctx);
		} catch (Throwable e) {
			log.error("#DefaultProcessorFilterFactory.doErrorFilterChain# ERROR MESSAGE: {}" , e.getMessage(), e);
		}
	}
	
}
