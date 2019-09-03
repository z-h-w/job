package com.etc.controller;

import com.baomidou.mybatisplus.mapper.Condition;
import com.etc.entity.Cate;
import com.etc.mapper.CateMapper;
import com.etc.mapper.CronTaskMapper;
import com.etc.utils.Response;
import com.etc.utils.TableSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Controller
@RequestMapping("/Cate")
public class CateController {

    @Autowired(required = false)
    private CronTaskMapper cronTaskMapper;

    @Autowired(required = false)
    private CateMapper cateMapper;

    @GetMapping("/index")
    public String index(){
        return "cate/index";
    }

    @PostMapping("/doUpdate")
    @ResponseBody
    public Object doUpdate(@Valid Cate cate, BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            return Response.error(bindingResult.getFieldError().getDefaultMessage());
        }
        Integer id = cate.getId();
        int num;
        if(id != null && id > 0){
            num = cateMapper.updateById(cate);
        }else{
            num = cateMapper.insert(cate);
        }
        if(num > 0){
            return Response.successAndJump("/Cate/index");
        }else{
            return Response.error("操作失败");
        }

    }

    @GetMapping(value = {"/edit","/add"})
    public String edit(Integer id, ModelMap map){
        if(null != id && id > 0){
            map.addAttribute("vo",cateMapper.selectById(id));
        }
        return "cate/edit";
    }


    @GetMapping("/lists")
    @ResponseBody
    public Object lists(Integer page, Integer limit){
        TableSet<Cate> datasets = new TableSet<>(page,limit);
        List<Cate> list = cateMapper.selectList(null);
        for (Cate cate: list) {
            Integer id = cate.getId();
            cate.setTaskCount(cronTaskMapper.selectCount(Condition.create().eq("cate_id",id)));
        }
        return datasets.setRecords(list).response();
    }

    @GetMapping("doDel")
    @ResponseBody
    public Object doDel(Integer id){
        int num = 0;
        if(null != id && id > 0){
            num = cateMapper.deleteById(id);
        }
        if(num > 0){
            return Response.success();
        }else{
            return Response.error();
        }
    }
}
