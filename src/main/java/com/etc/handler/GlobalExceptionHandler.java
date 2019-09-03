package com.etc.handler;

import com.etc.exception.NotLoginException;
import com.etc.utils.Response;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = NotLoginException.class)
    @ResponseBody
    private Object notLoginHandler(HttpServletRequest request, Exception e){
        return new Response(-1,null,"请登录","");
    }
}
