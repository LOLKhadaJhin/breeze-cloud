package com.nhwb.breeze.config;

import com.nhwb.breeze.config.util.BeanRefresh;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class MyApplicationRunner implements ApplicationRunner {
    @Autowired
    private BeanRefresh refresh;
    @Override
    public void run(ApplicationArguments args) throws Exception {
        refresh.refreshAuthorityAll();
        refresh.refreshBaseConfig();
        System.out.println("                     //");
        System.out.println("         \\\\         //");
        System.out.println("          \\\\       //");
        System.out.println("    ##DDDDDDDDDDDDDDDDDDDDDD##");
        System.out.println("    ## DDDDDDDDDDDDDDDDDDDD ##   ________   ___   ___        ___   ________   ___   ___        ___");
        System.out.println("    ## hh                hh ##   |\\   __  \\ |\\  \\ |\\  \\      |\\  \\ |\\   __  \\ |\\  \\ |\\  \\      |\\  \\");
        System.out.println("    ## hh    //    \\\\    hh ##   \\ \\  \\|\\ /_\\ \\  \\\\ \\  \\     \\ \\  \\\\ \\  \\|\\ /_\\ \\  \\\\ \\  \\     \\ \\  \\");
        System.out.println("    ## hh   //      \\\\   hh ##    \\ \\   __  \\\\ \\  \\\\ \\  \\     \\ \\  \\\\ \\   __  \\\\ \\  \\\\ \\  \\     \\ \\  \\");
        System.out.println("    ## hh                hh ##     \\ \\  \\|\\  \\\\ \\  \\\\ \\  \\____ \\ \\  \\\\ \\  \\|\\  \\\\ \\  \\\\ \\  \\____ \\ \\  \\");
        System.out.println("    ## hh      wwww      hh ##      \\ \\_______\\\\ \\__\\\\ \\_______\\\\ \\__\\\\ \\_______\\\\ \\__\\\\ \\_______\\\\ \\__\\");
        System.out.println("    ## hh                hh ##       \\|_______| \\|__| \\|_______| \\|__| \\|_______| \\|__| \\|_______| \\|__|");
        System.out.println("    ## MMMMMMMMMMMMMMMMMMMM ##");
        System.out.println("    ##MMMMMMMMMMMMMMMMMMMMMM##                             https://www.bilibili.com/video/BV1ZF411j7Ch/");
        System.out.println("         \\/            \\/");
    }

}
