package com.etc.controller;

import com.etc.crontask.TaskManager;
import com.etc.entity.User;
import com.etc.mapper.UserMapper;
import com.etc.utils.CommonUtil;
import com.etc.utils.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.security.NoSuchAlgorithmException;

@Controller
@RequestMapping("/public")
public class PublicController {

    private static Logger logger = LoggerFactory.getLogger(PublicController.class);

    @Autowired(required = false)
    private UserMapper userMapper;

    @GetMapping("logout")
    public String logout(HttpSession session){
        session.removeAttribute("admin_user");
        return "redirect:login";
    }

    @GetMapping("/login")
    public String login(HttpSession session){
        logger.info("PublicController,login start!!!");
        User user = (User)session.getAttribute("admin_user");
        if(user == null){
            logger.info("PublicController,login public/login start!!!");
            return "public/login";
        }else{
            logger.info("PublicController,login redirect:/ start!!!");
            return "redirect:/";
        }
    }

    @PostMapping("/login")
    @ResponseBody
    public Object login(@Valid User user, BindingResult bindResult, HttpSession session) throws NoSuchAlgorithmException {

        if(bindResult.hasErrors()){
            return Response.error(bindResult.getFieldError().getDefaultMessage());
        }
        if(user.getUsername() == "" || user.getPassword() == ""){
            return Response.error("用户或密码有误");
        }

        String password = user.getPassword();
        user.setPassword(null);

        User existUser = userMapper.selectOne(user);
        if(null == existUser){
            return Response.error("用户或密码有误");
        }else{
            String encryPwd = CommonUtil.encryPwd(password,existUser.getSalt());
            if(!existUser.getPassword().equals(encryPwd)){
                return Response.error("用户或密码有误");
            }
            session.setAttribute("admin_user",existUser);
            return Response.successAndJump("/");
        }
    }
}
