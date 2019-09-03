package com.etc.crontask;

import com.baomidou.mybatisplus.mapper.Condition;
import com.etc.entity.CronTask;
import com.etc.entity.CronTaskLog;
import com.etc.entity.Setting;
import com.etc.mapper.CronTaskLogMapper;
import com.etc.mapper.CronTaskMapper;
import com.etc.mapper.SettingMapper;
import com.etc.utils.CommonUtil;
import com.etc.utils.HttpClientUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.Future;

@Component
@EnableScheduling
public class TaskManager {

    private static Logger logger = LoggerFactory.getLogger(TaskManager.class);

    private static Map<Integer,CronTask> taskMap = new HashMap<>();

    private static Map<Integer,Future<?>> futureMap = new HashMap<>();

    @Autowired
    private ThreadPoolTaskScheduler threadPoolTaskScheduler;

    @Autowired(required = false)
    private CronTaskMapper cronTaskMapper;

    @Autowired(required = false)
    private CronTaskLogMapper cronTaskLogMapper;

    @Autowired(required = false)
    private SettingMapper settingMapper;

    @Bean
    public ThreadPoolTaskScheduler threadPoolTaskScheduler(){
        return new ThreadPoolTaskScheduler();
    }

    @Scheduled(fixedRate = 84600000)
    private void deleteLogs(){
        Setting setting = new Setting();
        setting = settingMapper.selectOne(setting);
        if(setting != null){
            Integer day = Integer.valueOf(setting.getValue());
            if(day > 0){
                Date date = new Date();
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(date);
                calendar.add(Calendar.DAY_OF_MONTH, -day);
                cronTaskLogMapper.delete(Condition.create().
                        lt("create_time",CommonUtil.formatDate(calendar.getTime())));
            }
        }
    }

    @Scheduled(fixedRate = 5000)
    private void loadTask(){
        List<CronTask> list = cronTaskMapper.selectList(null);
        Set<Integer> idSet = new HashSet<>();
        for (CronTask task : list){
            Integer id = task.getId();
            idSet.add(id);
            boolean needStart = false;
            if(taskMap.containsKey(id)){
                //比较任务是否有改变
                if(!task.equals(taskMap.get(id))){
                    needStart = true;
                    stop(id);
                }
            }else{
                needStart = true;
            }
            if(needStart && task.getStatus().equals(1)){
                start(task);
            }
        }
        //是否有任务删除
        Iterator<Map.Entry<Integer, CronTask>> entries = taskMap.entrySet().iterator();
        while (entries.hasNext()){
            Integer key = entries.next().getKey();
            if(!idSet.contains(key)){
                //该任务被删除
                stop(key);
            }
        }
    }
    private void start(CronTask task){

        Runnable runner = new TaskThread(task);
        CronTrigger trigger = new CronTrigger(task.getCronExpression());

        Future<?> future = threadPoolTaskScheduler.schedule(runner,trigger);

        futureMap.put(task.getId(),future);
        taskMap.put(task.getId(),task);
    }

    private void stop(Integer id){

        taskMap.remove(id);
        Future f = futureMap.get(id);
        if(f != null){
            f.cancel(false);
            futureMap.remove(id);
        }

    }

    public class TaskThread implements Runnable {

        private CronTask task;

        public TaskThread(CronTask task) {
            this.task = task;
        }

        @Override
        public void run() {

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
                logger.info("loadTask response = " +response);
                Float spendTime = new Float((System.currentTimeMillis() - begin) / 1000.0);
                log.setSpendTime(spendTime);
                log.setServerStatus("success");
                cronTaskLogMapper.insert(log);
                //new TaskExecutor(log,begin).start();
            } catch (Exception e) {
                Float spendTime = new Float((System.currentTimeMillis() - begin) / 1000.0);
                log.setSpendTime(spendTime);
                log.setServerStatus("fail");
                cronTaskLogMapper.insert(log);
                e.printStackTrace();
            }
        }

    }

    private class TaskExecutor extends Thread{

        private CronTaskLog log;
        private long begin;

        public TaskExecutor(CronTaskLog log, long begin){
            this.log = log;
            this.begin = begin;
        }
        @Override
        public void run(){
            String[] cmds = new String[]{"/bin/sh","-c",log.getServerUrl()};
            logger.info("loadTask cmds = " + Arrays.toString(cmds));
            try {
                Process process = Runtime.getRuntime().exec(cmds);
                process.waitFor();
                Float spendTime = new Float((System.currentTimeMillis() - begin) / 1000.0);
                log.setSpendTime(spendTime);
                cronTaskLogMapper.insert(log);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


}


