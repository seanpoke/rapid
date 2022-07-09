package com.ruyuan.rapid.core.netty.processor.filter.pre;

import com.ruyuan.rapid.common.config.DubboServiceInvoker;
import com.ruyuan.rapid.common.constants.ProcessorFilterConstants;
import com.ruyuan.rapid.common.constants.RapidProtocol;
import com.ruyuan.rapid.core.context.AttributeKey;
import com.ruyuan.rapid.core.context.Context;
import com.ruyuan.rapid.core.context.RapidContext;
import com.ruyuan.rapid.core.context.RapidRequest;
import com.ruyuan.rapid.core.netty.processor.filter.AbstractEntryProcessorFilter;
import com.ruyuan.rapid.core.netty.processor.filter.Filter;
import com.ruyuan.rapid.core.netty.processor.filter.FilterConfig;
import com.ruyuan.rapid.core.netty.processor.filter.ProcessorFilterType;

import lombok.Getter;
import lombok.Setter;

/**
 * <B>主类名称：</B>TimeoutPreFilter<BR>
 * <B>概要说明：</B>超时的前置过滤器<BR>
 * @author JiFeng
 * @since 2021年12月17日 上午12:34:53
 */
@Filter(
		id = ProcessorFilterConstants.TIMEOUT_PRE_FILTER_ID,
		name = ProcessorFilterConstants.TIMEOUT_PRE_FILTER_NAME,
		value = ProcessorFilterType.PRE,
		order = ProcessorFilterConstants.TIMEOUT_PRE_FILTER_ORDER
		)
public class TimeoutPreFilter extends AbstractEntryProcessorFilter<TimeoutPreFilter.Config> {

	public TimeoutPreFilter() {
		super(TimeoutPreFilter.Config.class);
	}
	
	/**
	 * <B>方法名称：</B>entry<BR>
	 * <B>概要说明：</B>超时的过滤器核心方法实现<BR>
	 * @author  JiFeng
	 * @since 2021年12月17日 上午12:50:31
	 * @see com.ruyuan.rapid.core.netty.processor.filter.ProcessorFilter#entry(java.lang.Object, java.lang.Object[])
	 */
	@Override
	public void entry(Context ctx, Object... args) throws Throwable {
		try {
			RapidContext rapidContext = (RapidContext)ctx;
			String protocol = rapidContext.getProtocol();
			TimeoutPreFilter.Config config = (TimeoutPreFilter.Config) args[0];
			switch (protocol) {
				case RapidProtocol.HTTP:
					RapidRequest rapidRequest = rapidContext.getRequest();
					rapidRequest.setRequestTimeout(config.getTimeout());
					break;
				case RapidProtocol.DUBBO:
					DubboServiceInvoker dubboServiceInvoker = (DubboServiceInvoker)rapidContext.getRequiredAttribute(AttributeKey.DUBBO_INVOKER);
					dubboServiceInvoker.setTimeout(config.getTimeout());
					break;
				default:
					break;
			}			
		} finally {
			//	非常重要的，一定要记得：驱动我们的过滤器链表
			super.fireNext(ctx, args);
		}
	}
	
	@Getter
	@Setter
	public static class Config extends FilterConfig {
		private Integer timeout;
	}
	
}
