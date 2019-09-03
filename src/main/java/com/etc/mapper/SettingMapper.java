package com.etc.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.etc.entity.Setting;

public interface SettingMapper extends BaseMapper<Setting> {

    Integer updateValueById(Setting t);
}
