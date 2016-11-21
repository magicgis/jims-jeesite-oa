/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.oa.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.thinkgem.jeesite.modules.oa.entity.OaSchedule;
import com.thinkgem.jeesite.modules.oa.entity.OaSummaryWeek;
import com.thinkgem.jeesite.modules.oa.entity.OaVo;
import com.thinkgem.jeesite.modules.oa.service.OaScheduleService;
import com.thinkgem.jeesite.modules.oa.service.OaSummaryWeekService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.thinkgem.jeesite.common.config.Global;
import com.thinkgem.jeesite.common.web.BaseController;
import com.thinkgem.jeesite.common.utils.StringUtils;
import com.thinkgem.jeesite.modules.oa.entity.OaSummaryDay;
import com.thinkgem.jeesite.modules.oa.service.OaSummaryDayService;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * 工作日志Controller
 *
 * @author yangruidong
 * @version 2016-11-16
 */
@Controller
@RequestMapping(value = "${adminPath}/oa/oaSummaryDay")
public class OaSummaryDayController extends BaseController {

    @Autowired
    private OaSummaryDayService oaSummaryDayService;

    @Autowired
    private OaScheduleService oaScheduleService;

    @Autowired
    private OaSummaryWeekService oaSummaryWeekService;

    @ModelAttribute
    public OaSummaryDay get(@RequestParam(required = false) String id) {
        OaSummaryDay entity = null;
        if (StringUtils.isNotBlank(id)) {
            entity = oaSummaryDayService.get(id);
        }
        if (entity == null) {
            entity = new OaSummaryDay();
        }
        return entity;
    }

    //@RequiresPermissions("oa:oaSummaryDay:view")
    @RequestMapping(value = {"list", ""})
    public String list(@ModelAttribute("oaSummaryDay") OaSummaryDay oaSummaryDay, HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {

        //当前日期
        DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String s = sdf.format(new Date());
        Date date = sdf.parse(s);

        //如果有日期，点击查询时的操作
        if (oaSummaryDay.getSumDate() != null) {
            Date before = oaSummaryDay.getSumDate();
            oaSummaryDay = oaSummaryDayService.findByDate(oaSummaryDay);
            OaSchedule oaSchedule = new OaSchedule();
            oaSchedule.setScheduleDate(before);
            List<OaSchedule> list = oaScheduleService.completeBy(oaSchedule);
            if (oaSummaryDay != null) {
                oaSummaryDay.setOaScheduleList(list);
                model.addAttribute("oaSummaryDay", oaSummaryDay);
            } else {
                model.addAttribute("oaSummaryDay", new OaSummaryDay(list, before));
            }
        } else {
            oaSummaryDay.setSumDate(date);
            oaSummaryDay = oaSummaryDayService.findByDate(oaSummaryDay);
            if (oaSummaryDay != null) {
                OaSchedule oaSchedule = new OaSchedule();
                oaSchedule.setScheduleDate(oaSummaryDay.getSumDate());
                List<OaSchedule> list = oaScheduleService.completeBy(oaSchedule);
                oaSummaryDay.setOaScheduleList(list);
                model.addAttribute("oaSummaryDay", oaSummaryDay);
            } else {
                OaSchedule oaSchedule = new OaSchedule();
                oaSchedule.setScheduleDate(date);
                List<OaSchedule> list = oaScheduleService.completeBy(oaSchedule);
                model.addAttribute("oaSummaryDay", new OaSummaryDay(list, date));
            }
        }
        return "modules/oa/oaSummaryDayList";
    }


    @RequestMapping(value = "complete")
    public String complete(OaSchedule oaSchedule, Model model) {
        List<OaSchedule> list = oaScheduleService.completeBy(oaSchedule);
        model.addAttribute("result", list);
        return "modules/oa/oaSummaryDayList";
    }


    @RequestMapping(value = "form")
    public String form(OaSummaryDay oaSummaryDay, Model model) {
        model.addAttribute("oaSummaryDay", oaSummaryDay);
        return "modules/oa/oaSummaryDayForm";
    }
    @RequestMapping(value = "form1")
    public String form1(OaSummaryWeek oaSummaryWeek, Model model) {
        model.addAttribute("OaSummaryWeek", oaSummaryWeek);
        return "modules/oa/oaSummaryDayForm";
    }

    Date currentFirstDate;
    List dd = new ArrayList();

    //格式化日期
    public String formatDate(Date date) {
        String year = Calendar.getInstance().get(Calendar.YEAR) + "-";
        String month = (date.getMonth() + 1) + "-";
        String day = date.getDate() + "";
        String[] weekDays = {"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if (w < 0)
            w = 0;
        return year + month + day + ' ' + weekDays[w];
    }

    public Date addDate(Date date, int n) {
        date.setDate(date.getDate() + n);
        return date;
    }
    public List setDate(Date date) {
        int week = date.getDay();
        date = addDate(date, week * -1);
        currentFirstDate = date;
        dd=new ArrayList();
        for (int i = 0; i < 7; i++) {

            dd.add(formatDate(i == 0 ? date : addDate(date, 1)));
        }
        return dd;
    }
    //查询周总结
    @RequestMapping(value = "formId")
    public String form(OaSummaryWeek oaSummaryWeek, Model model) throws Exception {

        //获取当前想要格式的日期
        DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String s = sdf.format(new Date());
        Date date = sdf.parse(s);
        //根据当前的日期来设置第一天从星期日开始
        Calendar calendar = Calendar.getInstance();
        calendar.setFirstDayOfWeek(Calendar.SUNDAY);
        calendar.setTime(date);
        List date1 = setDate(new Date());
        //calendar.get(Calendar.WEEK_OF_YEAR),获取当前日期是一年中的第几周
        //本周


      /*  if (oaSummaryWeek.getFlag()!=null) {
           //上一周
            date1=null;
            oaSummaryWeek.setWeekOfYear(calendar.get(Calendar.WEEK_OF_YEAR)-1);
            date1 = setDate(addDate(currentFirstDate, -7));
        }else{
            //下一周
            oaSummaryWeek.setWeekOfYear(calendar.get(Calendar.WEEK_OF_YEAR)+1);
            date1 = setDate(addDate(currentFirstDate, 7));
        }*/
        oaSummaryWeek.setWeekOfYear(calendar.get(Calendar.WEEK_OF_YEAR));
        oaSummaryWeek=oaSummaryWeekService.findByWeek(oaSummaryWeek);
        List<OaSchedule> list = null;
        List<OaVo> oa = new ArrayList<OaVo>();

        //根据日期拿到每个日期的任务完成和工作总结
        for (int j = 0; j < date1.size(); j++) {

            OaSchedule oaSchedule = new OaSchedule();
            String sum = date1.get(j).toString().substring(0, 10);

            String begin = sdf.format(DateFormat.getDateInstance().parse(sum));
            Date scheduleDate = sdf.parse(begin);
            OaVo oaVo = null;
            oaSchedule.setScheduleDate(scheduleDate);
            List<OaSchedule> list1 = oaScheduleService.completeBy(oaSchedule);
            String con = null;
            StringBuffer cons = new StringBuffer();
            for (int i = 0; i < list1.size(); i++) {
                con = cons.append(list1.get(i).getContent()).append("<br>").toString();
            }
            OaSummaryDay oaSummaryDay = new OaSummaryDay();
            oaSummaryDay.setSumDate(scheduleDate);
            oaSummaryDay = oaSummaryDayService.findByDate(oaSummaryDay);

            oaVo = new OaVo();
            oaVo.setContent(con);
            oaVo.setDate(date1.get(j).toString());
            if (oaSummaryDay != null) {
                oaVo.setStatus(oaSummaryDay.getContent());
            }
            oa.add(oaVo);
        }
        oaSummaryWeek.setOaVos(oa);


        model.addAttribute("oaSummaryWeek", oaSummaryWeek);
        return "modules/oa/oaSummaryDayForm";
    }


    @RequiresPermissions("oa:oaSummaryDay:edit")
    @RequestMapping(value = "save")
    public String save(OaSummaryDay oaSummaryDay, Model model, RedirectAttributes redirectAttributes) {
        if (!beanValidator(model, oaSummaryDay)) {
            return form(oaSummaryDay, model);
        }
        oaSummaryDayService.save(oaSummaryDay);
        String id = oaSummaryDay.getId();
        oaSummaryDay = oaSummaryDayService.get(id);
        OaSchedule oaSchedule = new OaSchedule();
        oaSchedule.setScheduleDate(oaSummaryDay.getSumDate());
        List<OaSchedule> list = oaScheduleService.completeBy(oaSchedule);
        oaSummaryDay.setOaScheduleList(list);
        model.addAttribute("oaSummaryDay", oaSummaryDay);
        addMessage(redirectAttributes, "保存工作日志成功");
        return "modules/oa/oaSummaryDayList";

    }

    /**
     * 保存周总结
     *
     * @param oaSummaryWeek
     * @param model
     * @param redirectAttributes
     * @return
     */
//    @RequiresPermissions("oa:oaSummaryDay:edit")
    @RequestMapping(value = "saveWeek")
    public String saveWeek(OaSummaryWeek oaSummaryWeek, Model model, RedirectAttributes redirectAttributes) throws Exception{
        if (!beanValidator(model, oaSummaryWeek)) {
            return form1(oaSummaryWeek, model);
        }
        //获取当前想要格式的日期
        DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String s = sdf.format(new Date());
        Date date = sdf.parse(s);
        //根据当前的日期来设置第一天从星期日开始
        Calendar calendar = Calendar.getInstance();
        calendar.setFirstDayOfWeek(Calendar.SUNDAY);
        calendar.setTime(date);
        List date1 = setDate(new Date());
        //calendar.get(Calendar.WEEK_OF_YEAR),获取当前日期是一年中的第几周
        //本周


      /*  if (oaSummaryWeek.getFlag()!=null) {
           //上一周
            date1=null;
            oaSummaryWeek.setWeekOfYear(calendar.get(Calendar.WEEK_OF_YEAR)-1);
            date1 = setDate(addDate(currentFirstDate, -7));
        }else{
            //下一周
            oaSummaryWeek.setWeekOfYear(calendar.get(Calendar.WEEK_OF_YEAR)+1);
            date1 = setDate(addDate(currentFirstDate, 7));
        }*/
        oaSummaryWeek.setWeekOfYear(calendar.get(Calendar.WEEK_OF_YEAR));
        oaSummaryWeek.setWeek(calendar.get(Calendar.WEEK_OF_YEAR) + "");
        oaSummaryWeek.setSumDate(date);

        List<OaSchedule> list = null;
        List<OaVo> oa = new ArrayList<OaVo>();

        //根据日期拿到每个日期的任务完成和工作总结
        for (int j = 0; j < date1.size(); j++) {

            OaSchedule oaSchedule = new OaSchedule();
            String sum = date1.get(j).toString().substring(0, 10);

            String begin = sdf.format(DateFormat.getDateInstance().parse(sum));
            Date scheduleDate = sdf.parse(begin);
            OaVo oaVo = null;
            oaSchedule.setScheduleDate(scheduleDate);
            List<OaSchedule> list1 = oaScheduleService.completeBy(oaSchedule);
            String con = null;
            StringBuffer cons = new StringBuffer();
            for (int i = 0; i < list1.size(); i++) {
                con = cons.append(list1.get(i).getContent()).append("<br>").toString();
            }
            OaSummaryDay oaSummaryDay = new OaSummaryDay();
            oaSummaryDay.setSumDate(scheduleDate);
            oaSummaryDay = oaSummaryDayService.findByDate(oaSummaryDay);

            oaVo = new OaVo();
            oaVo.setContent(con);
            oaVo.setDate(date1.get(j).toString());
            if (oaSummaryDay != null) {
                oaVo.setStatus(oaSummaryDay.getContent());
            }
            oa.add(oaVo);
        }
        oaSummaryWeek.setOaVos(oa);
        oaSummaryWeekService.save(oaSummaryWeek);
       /* String id = oaSummaryWeek.getId();
        oaSummaryWeek=oaSummaryWeekService.get(id);*/
        addMessage(redirectAttributes, "保存工作日志成功");
        model.addAttribute("oaSummaryWeek", oaSummaryWeek);
        return "modules/oa/oaSummaryDayForm";
    }

    @RequiresPermissions("oa:oaSummaryDay:edit")
    @RequestMapping(value = "delete")
    public String delete(OaSummaryDay oaSummaryDay, RedirectAttributes redirectAttributes) {
        oaSummaryDayService.delete(oaSummaryDay);
        addMessage(redirectAttributes, "删除工作日志成功");
        return "redirect:" + Global.getAdminPath() + "/oa/oaSummaryDay/?repage";
    }

}