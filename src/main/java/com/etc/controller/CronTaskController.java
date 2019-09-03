package com.etc.controller;

import com.etc.crontask.TaskManager;
import com.etc.entity.CronTask;
import com.etc.entity.CronTaskLog;
import com.etc.mapper.CateMapper;
import com.etc.mapper.CronTaskLogMapper;
import com.etc.mapper.CronTaskMapper;
import com.etc.utils.HttpClientUtil;
import com.etc.utils.Response;
import com.etc.utils.TableSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/CronTask")
public class CronTaskController {

    private static Logger logger = LoggerFactory.getLogger(CronTaskController.class);

    @Autowired(required = false)
    private CronTaskMapper cronTaskMapper;

    @Autowired(required = false)
    private CateMapper cateMapper;

    @Autowired(required = false)
    private CronTaskLogMapper cronTaskLogMapper;

    @GetMapping("/index")
    public String index(){
        return "cron_task/index";
    }


    @PostMapping("/doUpdate")
    @ResponseBody
    public Object doUpdate(@Valid CronTask task, BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            return Response.error(bindingResult.getFieldError().getDefaultMessage());
        }
        Integer id = task.getId();
        try{
            new CronTrigger(task.getCronExpression());
        }catch (Exception e){
            return Response.error("cron表达式有误：<br>"+e.getMessage());
        }
        int num;
        if(id != null && id > 0){
            task.setUpdateTime(new Date());
            num = cronTaskMapper.updateById(task);
        }else{
            task.setCreateTime(new Date());
            num = cronTaskMapper.insert(task);
        }
        if(num > 0){
            return Response.successAndJump("/CronTask/index");
        }else{
            return Response.error("操作失败");
        }

    }

    @PostMapping("/changeStatus")
    @ResponseBody
    public Object changeStatus(CronTask task){
        Integer id = task.getId();
        int num = 0;
        if(id != null && id > 0){
            task.setUpdateTime(new Date());
            num = cronTaskMapper.updateById(task);
        }
        if(num > 0){
            return Response.success();
        }else{
            return Response.error();
        }

    }

    @GetMapping(value = {"/edit","/add"})
    public String edit(Integer id, ModelMap map){
        if(null != id && id > 0){
            map.addAttribute("vo",cronTaskMapper.selectById(id));
        }
        map.addAttribute("cateList",cateMapper.selectList(null));
        return "cron_task/edit";
    }


    @GetMapping(value = {"/handle"})
    @ResponseBody
    public Object handle(Integer id, ModelMap map){
        if(null != id && id > 0){
            CronTask task = cronTaskMapper.selectById(id);
            Date createTime = new Date();
            long begin = System.currentTimeMillis();
            CronTaskLog log = new CronTaskLog();
            try {

                log.setCreateTime(createTime);
                log.setServerUrl(task.getServerUrl());
                log.setCtId(task.getId());
                log.setServerMethod(task.getServerMethod());
                log.setServerPath(task.getServerPath());
                log.setServerParameter(task.getServerParameter());
                String response = HttpClientUtil.send(task.getServerMethod(), task.getServerUrl(), task.getServerPath(), task.getServerParameter());
                logger.info("handle response = " +response);
                Float spendTime = new Float((System.currentTimeMillis() - begin) / 1000.0);
                log.setSpendTime(spendTime);
                log.setServerStatus("success");
                cronTaskLogMapper.insert(log);
            } catch (Exception e) {
                e.printStackTrace();
                logger.info("loadTask e = " +e.getMessage(),e);
                Float spendTime = new Float((System.currentTimeMillis() - begin) / 1000.0);
                log.setSpendTime(spendTime);
                log.setServerStatus("fail");
                cronTaskLogMapper.insert(log);
                return Response.error();
            }
            return Response.success();
        } else{
            return Response.error();
        }
    }

    @GetMapping("/help")
    public String help(){
        return "cron_task/help";
    }

    @GetMapping("/lists")
    @ResponseBody
    public Object lists(Integer page, Integer limit){
        TableSet<CronTask> datasets = new TableSet<>(page,limit);
        List<CronTask> list = cronTaskMapper.selectTaskPage(datasets);
        return datasets.setRecords(list).response();
    }

    @GetMapping("doDel")
    @ResponseBody
    public Object doDel(Integer id){
        int num = 0;
        if(null != id && id > 0){
            num = cronTaskMapper.deleteById(id);
        }
        if(num > 0){
            return Response.success();
        }else{
            return Response.error();
        }
    }

}
