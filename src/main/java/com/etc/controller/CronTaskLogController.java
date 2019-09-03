package com.etc.controller;

import com.baomidou.mybatisplus.mapper.Condition;
import com.etc.entity.Cate;
import com.etc.entity.CronTask;
import com.etc.entity.CronTaskLog;
import com.etc.mapper.CateMapper;
import com.etc.mapper.CronTaskLogMapper;
import com.etc.mapper.CronTaskMapper;
import com.etc.utils.TableSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/CronTaskLog")
public class CronTaskLogController {


    @Autowired(required = false)
    private CronTaskLogMapper cronTaskLogMapper;

    @Autowired(required = false)
    private CronTaskMapper cronTaskMapper;

    @Autowired(required = false)
    private CateMapper cateMapper;

    @GetMapping("/index")
    public String index(Integer ctId, ModelMap map){
        if(ctId != null && ctId > 0){
            map.addAttribute("ctId",ctId);
        }
        map.addAttribute("cateList",cateMapper.selectList(null));
        return "cron_task_log/index";
    }


    @GetMapping("/lists")
    @ResponseBody
    public Object lists(CronTaskLog taskLog,Integer page, Integer limit){
        TableSet<CronTaskLog> datasets = new TableSet<>(page,limit);
        List descs = new ArrayList<String>();
        descs.add("id");
        datasets.setDescs(descs);
        Condition condition = null;
        if(taskLog.getServerUrl() != null || taskLog.getCtId() !=null){
            condition = Condition.create();
            if(taskLog.getServerUrl() != null){
                condition = (Condition) condition.like("server_url",taskLog.getServerUrl());
            }
            if(taskLog.getCtId() != null){
                condition = (Condition) condition.eq("ct_id",taskLog.getCtId());
            }
        }

        List<CronTaskLog> list = cronTaskLogMapper.selectPage(datasets,condition);
        for (CronTaskLog log : list) {
            CronTask task = cronTaskMapper.selectById(log.getCtId());
            if(task != null){
                Cate cate = cateMapper.selectById(task.getCateId());
                if(cate != null){
                    log.setCateName(cate.getName());
                }
                log.setRemark(task.getRemark());
            }

        }
        return datasets.setRecords(list).response();
    }
}
