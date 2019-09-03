package com.etc.controller;

import com.etc.EtcManagerJobApplication;
import com.etc.entity.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.HtmlUtils;

import javax.servlet.http.HttpSession;
import javax.swing.text.html.HTML;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

@Controller
public class IndexController {

    @GetMapping("/")
    public String index(HttpSession session, ModelMap map){
        User user = (User)session.getAttribute("admin_user");
        if(user == null){
            return "redirect:public/login";
        }
        map.addAttribute("adminUserName",user.getUsername());
        return "index/index";
    }
    @GetMapping("/console")
    public String console(ModelMap map) throws Exception {
        String os = System.getProperty("os.name");
        Process process;
        if(os.toLowerCase().startsWith("win")){
            String cmd = "jps -l";
            process = Runtime.getRuntime().exec(cmd);
        }else{
            String[] cmds = new String[]{"sh", "-c", "ps aux | grep etc-manager-job"};
            process = Runtime.getRuntime().exec(cmds);
        }
        process.waitFor();
        InputStream in = process.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        String line ;
        StringBuilder sb = new StringBuilder();
        while ((line = reader.readLine()) != null) {
            if(os.toLowerCase().startsWith("win")){
                if(line.contains("EtcManagerJobApplication")){
                    sb.append(line + " ");
                }
            }else{
                sb.append(line + " ");
            }
        }
        map.addAttribute("status", sb);
        return "index/console";
    }
}
