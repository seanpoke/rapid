package com.ruyuan.rapid.console.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ruyuan.rapid.common.config.ServiceInstance;
import com.ruyuan.rapid.common.util.FastJsonConvertUtil;
import com.ruyuan.rapid.common.util.Pair;
import com.ruyuan.rapid.discovery.api.RegistryService;

/**
 * <B>主类名称：</B>ServiceInstanceService<BR>
 * <B>概要说明：</B>ServiceInstanceService<BR>
 * @author hezhuo.Bai-JiFeng
 * @since 2021年11月29日 上午10:43:38
 */
/**
 * <B>主类名称：</B>ServiceInstanceService<BR>
 * <B>概要说明：</B>ServiceInstanceService<BR>
 * @author JiFeng
 * @since 2021年12月20日 下午10:54:42
 */
@Service
public class ServiceInstanceService {

	@Autowired
	private RegistryService registryService;

	/**
	 * <B>方法名称：</B>getServiceInstanceList<BR>
	 * <B>概要说明：</B>根据服务唯一标识获取实例列表<BR>
	 * @author JiFeng
	 * @since 2021年12月20日 下午10:54:57
	 * @param prefixPath
	 * @param uniqueId
	 * @return
	 * @throws Exception 
	 */
	public List<ServiceInstance> getServiceInstanceList(String prefixPath, String uniqueId) throws Exception {
		/**
		 * 		/rapid-env
		 * 			/services
		 * 				/serviceA:1.0.0		==>	value: ServiceDefinition & AbstractServiceInvoker
		 * 				/serviceB:1.0.0
		 * 			/instances
		 * 			/instances/serviceA:1.0.0/192.168.11.100:port  	==>  value: ServiceInstance1
		 * 			/instances/serviceA:1.0.0/192.168.11.101:port		==>  value: ServiceInstance1
		 * 			/instances/serviceB:1.0.0	
		 * 				/192.168.11.103:port  	==>  value: ServiceInstance1	
		 * 			/routes
		 * 				/uuid01	==>	value: Rule1
		 * 				/uuid02 ==>	value: Rule2
		 */
		String path = RegistryService.PATH 
				+ prefixPath 
				+ RegistryService.INSTANCE_PREFIX
				+ RegistryService.PATH 
				+ uniqueId;
		List<Pair<String, String>> list = registryService.getListByPrefixKey(path);
		
		List<ServiceInstance> serviceInstances = new ArrayList<ServiceInstance>();

		for(Pair<String, String> pair : list) {
//			String p = pair.getObject1();
//			if (p.equals(path)) { 
//				continue;
//			}
			String json = pair.getObject2();
			ServiceInstance si = FastJsonConvertUtil.convertJSONToObject(json, ServiceInstance.class);
			serviceInstances.add(si);
		}
		
		return serviceInstances;
	}
	
	public void updateEnable(String prefixPath, String uniqueId, String serviceInstanceId, boolean enable) throws Exception {
		updateServiceInstance(prefixPath, uniqueId, serviceInstanceId, enable);
	}
	
	public void updateTags(String prefixPath, String uniqueId, String serviceInstanceId, String tags) throws Exception {
		updateServiceInstance(prefixPath, uniqueId, serviceInstanceId, tags);
	}
	
	public void updateWeight(String prefixPath, String uniqueId, String serviceInstanceId, int weight) throws Exception {
		updateServiceInstance(prefixPath, uniqueId, serviceInstanceId, weight);
	}

	/**
	 * <B>方法名称：</B>updateEnable<BR>
	 * <B>概要说明：</B>启用禁用某个服务实例<BR>
	 * @author JiFeng
	 * @since 2021年12月20日 下午10:55:09
	 * @param prefixPath
	 * @param uniqueId
	 * @param serviceInstanceId
	 * @param param
	 * @throws Exception
	 */
	private void updateServiceInstance(String prefixPath, String uniqueId, String serviceInstanceId, Object param) throws Exception {
		String path = RegistryService.PATH 
				+ prefixPath 
				+ RegistryService.INSTANCE_PREFIX
				+ RegistryService.PATH 
				+ uniqueId;
		List<Pair<String, String>> list = registryService.getListByPrefixKey(path);

		for(Pair<String, String> pair : list) {
			String p = pair.getObject1();
			if (p.equals(path)) { 
				continue;
			}
			String json = pair.getObject2();
			ServiceInstance si = FastJsonConvertUtil.convertJSONToObject(json, ServiceInstance.class);
			//	更新启用禁用
			if((si.getServiceInstanceId()).equals(serviceInstanceId)) {

				//	update:  tags & enable & weight
				if(param instanceof String) {
					String tags = (String)param;
					si.setTags(tags);
				}
				if(param instanceof Boolean) {
					boolean enable = (boolean)param;
					si.setEnable(enable);
				}
				if(param instanceof Integer) {
					int weight = (int)param;
					si.setWeight(weight);
				}
				
			}
			//	会写数据到ETCD
			String value = FastJsonConvertUtil.convertObjectToJSON(si);
			registryService.registerEphemeralNode(p, value);
		}
	}
	
}
