package com.nowcoder.community.controller;

import com.nowcoder.community.service.AlphaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

@Controller
@RequestMapping("/index")
public class DemoController {

    @RequestMapping("/hello")
    @ResponseBody
    public String sayHello(){
        return "Hello Spring Boot!";
    }

    @Autowired
    AlphaService alphaService;

    @RequestMapping("/alpha")
    @ResponseBody
    public String alphaController(){
        String resp = alphaService.service();
        return resp;
    }

    @RequestMapping(value = "/http0")
    public void httpController(HttpServletRequest request, HttpServletResponse response) throws IOException {
        System.out.println(request.getParameter("name"));
        System.out.println(request.getMethod());
        System.out.println(request.getServletPath());
        System.out.println(request.getContextPath());

        Enumeration<String> enumeration = request.getHeaderNames();
        while(enumeration.hasMoreElements()){
            String headerName = enumeration.nextElement();
            System.out.println(headerName + ": " + request.getHeader(headerName));
        }

        response.setContentType("text/html;charset=utf-8");
        response.getWriter().write("<h1> 成功啦 </h1>");
    }

    @RequestMapping(value = "/http1", method = RequestMethod.GET)
    @ResponseBody
    public String httpController1(
            @RequestParam(name = "name", required = false, defaultValue = "zhangsan") String name,
            @RequestParam(name = "num", required = false, defaultValue = "0") int num
            ){
        System.out.println(name);
        System.out.println(num);

        return "a member";
    }

    @RequestMapping(value = "/student/{id}", method = RequestMethod.GET)
    @ResponseBody
    public String httpController3(@PathVariable(name = "id") int id){
        System.out.println(id);
        return "success! Student Id = " + id;
    }

    @RequestMapping(value = "/student", method = RequestMethod.POST)
    @ResponseBody
    public String httpController2(String name, int age){
        System.out.println(name);
        System.out.println(age);
        return "a student";
    }

    @RequestMapping("/teacher")
    public ModelAndView getTeacher(){
        ModelAndView mav = new ModelAndView();
        mav.addObject("name", "张三");
        mav.addObject("age", 12);
        mav.setViewName("/view");
        return mav;
    }

    @RequestMapping(value = "/school", method = RequestMethod.GET)
    public String getSchool(Model model){
        model.addAttribute("name", "北京交通大学");
        model.addAttribute("age", 117);
        return "/school_demo";
    }

    @RequestMapping(value = "/emp", method = RequestMethod.GET)
    @ResponseBody
    public Map<String, Object> getEmp(){
        Map<String, Object> map = new HashMap<>();
        map.put("name", "zhangsan");
        map.put("age", 22);
        map.put("salary", 8000.0);
        return map;
    }

    @RequestMapping(value = "emps", method = RequestMethod.GET)
    @ResponseBody
    public List<Map<String, Object>> getEmps(){
        List<Map<String, Object>> list = new ArrayList<>();
        Map<String, Object> map = new HashMap<>();
        map.put("name", "zhangsan");
        map.put("age", 22);
        map.put("salary", 5000.0);
        list.add(map);

        map = new HashMap<>();
        map.put("name", "lisi");
        map.put("age", 24);
        map.put("salary", 10000.0);
        list.add(map);

        map = new HashMap<>();
        map.put("name", "wangwu");
        map.put("age", 19);
        map.put("salary", 6000.0);
        list.add(map);

        return list;
    }
}
