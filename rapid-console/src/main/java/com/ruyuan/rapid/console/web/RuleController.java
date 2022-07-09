package com.ruyuan.rapid.console.web;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ruyuan.rapid.common.config.Rule;
import com.ruyuan.rapid.console.dto.RuleDTO;
import com.ruyuan.rapid.console.service.RuleService;

/**
 * <B>主类名称：</B>RuleController<BR>
 * <B>概要说明：</B>规则控制层<BR>
 * @author JiFeng
 * @since 2021年12月20日 下午10:55:41
 */
@RestController
public class RuleController {

	@Autowired
	private RuleService ruleService;
	
	@RequestMapping("rule/getList")
	public List<Rule> getList(@RequestParam("prefixPath") String prefixPath) throws Exception{
		List<Rule> list = ruleService.getRuleList(prefixPath);
		return list;
	}
	
	@RequestMapping("rule/add")
	public void addRule(@RequestBody RuleDTO ruleDTO) throws Exception {
		if(ruleDTO != null) {
			Rule rule = new Rule();
			rule.setId(ruleDTO.getId());
			rule.setName(ruleDTO.getName());
			rule.setProtocol(rule.getProtocol());
			rule.setOrder(ruleDTO.getOrder());
			rule.setFilterConfigs(ruleDTO.getFilterConfigs());
			ruleService.addRule(ruleDTO.getPrefixPath(), rule);			
		}
	}
	
	@RequestMapping("rule/update")
	public void updateRule(@RequestBody RuleDTO ruleDTO) throws Exception {
		if(ruleDTO != null) {
			Rule rule = new Rule();
			rule.setId(ruleDTO.getId());
			rule.setName(ruleDTO.getName());
			rule.setProtocol(rule.getProtocol());
			rule.setOrder(ruleDTO.getOrder());
			rule.setFilterConfigs(ruleDTO.getFilterConfigs());
			ruleService.updateRule(ruleDTO.getPrefixPath(), rule);			
		}
	}
	
	@RequestMapping("rule/delete")
	public void deleteRule(@RequestBody RuleDTO ruleDTO) {
		if(ruleDTO != null) {
			ruleService.deleteRule(ruleDTO.getPrefixPath(), ruleDTO.getId());	
		}
	}
	
}
