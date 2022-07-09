package com.ruyuan.rapid.console.web;

import java.util.List;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ruyuan.rapid.common.config.ServiceInstance;
import com.ruyuan.rapid.console.dto.ServiceInstanceDTO;
import com.ruyuan.rapid.console.service.ServiceInstanceService;

/**
 * <B>主类名称：</B>ServiceInstanceController<BR>
 * <B>概要说明：</B>服务实例控制层<BR>
 * @author JiFeng
 * @since 2021年11月29日 上午10:41:31
 */
@RestController
public class ServiceInstanceController {

	private ServiceInstanceService serviceInstanceService;
	
	/**
	 * <B>方法名称：</B>getList<BR>
	 * <B>概要说明：</B>根据服务唯一ID获取服务实例列表<BR>
	 * @author JiFeng
	 * @since 2021年11月29日 下午12:54:17
	 * @param prefixPath
	 * @param uniqueId
	 * @return List<ServiceInstance>
	 * @throws Exception 
	 */
	@RequestMapping("/serviceInstance/getList")
	public List<ServiceInstance> getList(@RequestParam("prefixPath") String prefixPath,
			@RequestParam("uniqueId")String uniqueId) throws Exception{
		List<ServiceInstance> list = serviceInstanceService.getServiceInstanceList(prefixPath, uniqueId);
		return list;
	}
	
	/**
	 * <B>方法名称：</B>updateEnable<BR>
	 * <B>概要说明：</B>启用禁用某个服务实例<BR>
	 * @author JiFeng
	 * @since 2021年11月29日 下午1:04:22
	 * @param serviceInstanceDTO
	 * @throws Exception
	 */
	@RequestMapping("/serviceInstance/updateEnable")
	public void updateEnable(@RequestBody ServiceInstanceDTO serviceInstanceDTO) throws Exception {
		if(serviceInstanceDTO != null) {
			serviceInstanceService.updateEnable(
					serviceInstanceDTO.getPrefixPath(),
					serviceInstanceDTO.getUniqueId(),
					serviceInstanceDTO.getServiceInstanceId(),
					serviceInstanceDTO.isEnable());
		}
	}
	
	/**
	 * <B>方法名称：</B>updateTags<BR>
	 * <B>概要说明：</B>对某个服务实例进行打标签<BR>
	 * @author JiFeng
	 * @since 2021年11月29日 下午1:14:56
	 * @param serviceInstanceDTO
	 * @throws Exception
	 */
	@RequestMapping("/serviceInstance/updateTags")
	public void updateTags(@RequestBody ServiceInstanceDTO serviceInstanceDTO) throws Exception {
		if(serviceInstanceDTO != null) {
			serviceInstanceService.updateTags(
					serviceInstanceDTO.getPrefixPath(),
					serviceInstanceDTO.getUniqueId(),
					serviceInstanceDTO.getServiceInstanceId(),
					serviceInstanceDTO.getTags());
		}
	}
	
	/**
	 * <B>方法名称：</B>updateWeight<BR>
	 * <B>概要说明：</B>更新某个服务实例的权重<BR>
	 * @author JiFeng
	 * @since 2021年11月29日 下午1:15:21
	 * @param serviceInstanceDTO
	 * @throws Exception
	 */
	@RequestMapping("/serviceInstance/updateWeight")
	public void updateWeight(@RequestBody ServiceInstanceDTO serviceInstanceDTO) throws Exception {
		if(serviceInstanceDTO != null) {
			serviceInstanceService.updateWeight(
					serviceInstanceDTO.getPrefixPath(),
					serviceInstanceDTO.getUniqueId(),
					serviceInstanceDTO.getServiceInstanceId(),
					serviceInstanceDTO.getWeight());
		}
	}

}
