/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.table.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.thinkgem.jeesite.common.config.Global;
import com.thinkgem.jeesite.common.persistence.Page;
import com.thinkgem.jeesite.common.web.BaseController;
import com.thinkgem.jeesite.common.utils.StringUtils;
import com.thinkgem.jeesite.modules.table.entity.OaPersonDefineTable;
import com.thinkgem.jeesite.modules.table.service.OaPersonDefineTableService;

/**
 * 自定义数据源Controller
 * @author chenxy
 * @version 2016-11-17
 */
@Controller
@RequestMapping(value = "${adminPath}/table/oaPersonDefineTable")
public class OaPersonDefineTableController extends BaseController {

	@Autowired
	private OaPersonDefineTableService oaPersonDefineTableService;
	
	@ModelAttribute
	public OaPersonDefineTable get(@RequestParam(required=false) String id) {
		OaPersonDefineTable entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = oaPersonDefineTableService.get(id);
		}
		if (entity == null){
			entity = new OaPersonDefineTable();
		}
		return entity;
	}
	
	@RequiresPermissions("table:oaPersonDefineTable:view")
	@RequestMapping(value = {"list", ""})
	public String list(OaPersonDefineTable oaPersonDefineTable, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<OaPersonDefineTable> page = oaPersonDefineTableService.findPage(new Page<OaPersonDefineTable>(request, response), oaPersonDefineTable); 
		model.addAttribute("page", page);
		return "modules/table/oaPersonDefineTableList";
	}

	@RequiresPermissions("table:oaPersonDefineTable:view")
	@RequestMapping(value = "form")
	public String form(OaPersonDefineTable oaPersonDefineTable, Model model) {
		model.addAttribute("oaPersonDefineTable", oaPersonDefineTable);
		return "modules/table/oaPersonDefineTableForm";
	}

	@RequiresPermissions("table:oaPersonDefineTable:edit")
	@RequestMapping(value = "save")
	public String save(OaPersonDefineTable oaPersonDefineTable, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, oaPersonDefineTable)){
			return form(oaPersonDefineTable, model);
		}
		oaPersonDefineTableService.save(oaPersonDefineTable);
        //todo 同步修改数据库表的方法
		addMessage(redirectAttributes, "保存自定义数据源成功");

		return "redirect:"+Global.getAdminPath()+"/table/oaPersonDefineTable/?repage";
	}
	
	@RequiresPermissions("table:oaPersonDefineTable:edit")
	@RequestMapping(value = "delete")
	public String delete(OaPersonDefineTable oaPersonDefineTable, RedirectAttributes redirectAttributes) {
		oaPersonDefineTableService.delete(oaPersonDefineTable);
        //todo 删除同步到数据库的表
		addMessage(redirectAttributes, "删除自定义数据源成功");
		return "redirect:"+Global.getAdminPath()+"/table/oaPersonDefineTable/?repage";
	}

}