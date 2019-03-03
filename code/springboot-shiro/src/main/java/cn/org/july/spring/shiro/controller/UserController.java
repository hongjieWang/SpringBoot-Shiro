package cn.org.july.spring.shiro.controller;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class UserController {

    @RequestMapping(value = "/hello")
    @ResponseBody
    public String hello() {
        return "ok";
    }

    @RequestMapping(value = "/testThymeleaf")
    public String testThymeleaf(Model model) {
        //把数据存入model
        model.addAttribute("test", "测试Thymeleaf");
        //返回test.html
        return "test";
    }


    @RequestMapping(value = "/add")
    public String add(Model model) {
        //把数据存入model
        model.addAttribute("test", "添加用户页面");
        //返回test.html
        return "/user/add";
    }

    @RequestMapping(value = "/update")
    public String update(Model model) {
        //把数据存入model
        model.addAttribute("test", "修改用户页面");
        //返回test.html
        return "/user/update";
    }


    @RequestMapping(value = "/toLogin")
    public String toLogin() {
        return "login";
    }

    @RequestMapping(value = "/noAuth")
    public String noAuth() {
        return "noAuth";
    }

    @RequestMapping(value = "/login")
    public String login(String username, String password, Model model) {
        System.out.printf("username :" + username);
        //1、获取 Subject
        Subject subject = SecurityUtils.getSubject();
        //2、封装用户数据
        UsernamePasswordToken usernamePasswordToken = new UsernamePasswordToken(username, password);
        //3、执行登录方法
        try {
            subject.login(usernamePasswordToken);
            return "redirect:/testThymeleaf";
        } catch (UnknownAccountException e) {
            //登录失败，用户名称不存在
            model.addAttribute("msg", "用户名称不存在");
            return "login";
        } catch (IncorrectCredentialsException e) {
            //登录失败，密码错误
            model.addAttribute("msg", "密码错误");
            return "login";
        }
    }

}
