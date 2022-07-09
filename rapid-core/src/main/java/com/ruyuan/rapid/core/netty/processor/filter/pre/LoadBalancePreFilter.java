package com.ruyuan.rapid.core.netty.processor.filter.pre;

import java.util.Set;

import com.ruyuan.rapid.common.config.DynamicConfigManager;
import com.ruyuan.rapid.common.config.ServiceInstance;
import com.ruyuan.rapid.common.constants.ProcessorFilterConstants;
import com.ruyuan.rapid.common.constants.RapidProtocol;
import com.ruyuan.rapid.common.enums.LoadBalanceStrategy;
import com.ruyuan.rapid.common.enums.ResponseCode;
import com.ruyuan.rapid.common.exception.RapidResponseException;
import com.ruyuan.rapid.core.balance.LoadBalance;
import com.ruyuan.rapid.core.balance.LoadBalanceFactory;
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
 * <B>主类名称：</B>LoadBalancePreFilter<BR>
 * <B>概要说明：</B>负载均衡前置过滤器<BR>
 * @author JiFeng
 * @since 2021年12月20日 下午4:18:17
 */
@Filter(
		id = ProcessorFilterConstants.LOADBALANCE_PRE_FILTER_ID,
		name = ProcessorFilterConstants.LOADBALANCE_PRE_FILTER_NAME,
		value = ProcessorFilterType.PRE,
		order = ProcessorFilterConstants.LOADBALANCE_PRE_FILTER_ORDER
		)
public class LoadBalancePreFilter extends AbstractEntryProcessorFilter<LoadBalancePreFilter.Config> {

	public LoadBalancePreFilter() {
		super(LoadBalancePreFilter.Config.class);
	}

	@Override
	public void entry(Context ctx, Object... args) throws Throwable {
		try {
			RapidContext rapidContext = (RapidContext)ctx;
			LoadBalancePreFilter.Config config = (LoadBalancePreFilter.Config)args[0];
			LoadBalanceStrategy loadBalanceStrategy = config.getBalanceStrategy();
			String protocol = rapidContext.getProtocol();
			switch (protocol) {
				case RapidProtocol.HTTP:
					doHttpLoadBalance(rapidContext, loadBalanceStrategy);
					break;
				case RapidProtocol.DUBBO:
					doDubboLoadBalance(rapidContext, loadBalanceStrategy);
					break;	
				default:
					break;
			}
		} finally {
			super.fireNext(ctx, args);;
		}
	}
	
	private void doHttpLoadBalance(RapidContext rapidContext, LoadBalanceStrategy loadBalanceStrategy) {
		RapidRequest rapidRequest = rapidContext.getRequest();
		String uniqueId = rapidRequest.getUniqueId();
		Set<ServiceInstance> serviceInstances = DynamicConfigManager.getInstance()
				.getServiceInstanceByUniqueId(uniqueId);
		
		rapidContext.putAttribute(AttributeKey.MATCH_INSTANCES, serviceInstances);
		
		//	通过负载均衡枚举值获取负载均衡实例对象
		LoadBalance loadBalance = LoadBalanceFactory.getLoadBalance(loadBalanceStrategy);
		//	调用负载均衡实现，选择一个实例进行返回
		ServiceInstance serviceInstance = loadBalance.select(rapidContext);
		
		if(serviceInstance == null) {
			//	如果服务实例没有找到：终止请求继续执行，显示抛出异常
			rapidContext.terminated();
			throw new RapidResponseException(ResponseCode.SERVICE_INSTANCE_NOT_FOUND);
		}
		
		//	这一步非常关键：设置可修改的服务host，为当前选择的实例对象的address
		rapidContext.getRequestMutale().setModifyHost(serviceInstance.getAddress());
	}
	
	/**
	 * <B>方法名称：</B>doDubboLoadBalance<BR>
	 * <B>概要说明：</B><BR>
	 * @author JiFeng
	 * @since 2021年12月20日 下午4:44:45
	 * @param rapidContext
	 * @param loadBalanceStrategy
	 */
	private void doDubboLoadBalance(RapidContext rapidContext, LoadBalanceStrategy loadBalanceStrategy) {
		//	将负载均衡策略设置到上下文中即可，由 dubbo LoadBalance去进行使用：SPI USED
		rapidContext.putAttribute(AttributeKey.DUBBO_LOADBALANCE_STRATEGY, loadBalanceStrategy);
	}

	/**
	 * <B>主类名称：</B>Config<BR>
	 * <B>概要说明：</B>负载均衡前置过滤器配置<BR>
	 * @author JiFeng
	 * @since 2021年12月20日 下午4:21:54
	 */
	@Getter
	@Setter
	public static class Config extends FilterConfig {
		
		private LoadBalanceStrategy balanceStrategy = LoadBalanceStrategy.ROUND_ROBIN;
		
	}
	

}

