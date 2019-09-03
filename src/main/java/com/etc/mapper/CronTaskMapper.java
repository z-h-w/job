package com.etc.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.etc.entity.CronTask;

import java.util.List;

public interface CronTaskMapper extends BaseMapper<CronTask> {

    public List<CronTask> selectTaskPage(Page<CronTask> page);
}
