package com.nhwb.breeze.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/error")
public class ErrorController {
    @GetMapping("/limit")
    public String limit(HttpServletRequest request, Model model) {
        model.addAttribute("text", request.getAttribute("limit"))
                .addAttribute("help", "www.bilibili.com/video/BV1ZF411j7Ch/");
        return "a";
    }

    @GetMapping("/busy")
    public String register(Model model) {
        model.addAttribute("text", "服务器忙，稍后重试")
                .addAttribute("help", "www.bilibili.com/video/BV1ZF411j7Ch/");
        return "a";
    }
}
