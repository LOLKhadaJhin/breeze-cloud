package com.nhwb.breeze.config;

import com.nhwb.breeze.domain.BaseConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalModelConfig {
    @Autowired
    private BaseConfig baseConfig;

    @ModelAttribute
    public void addAttributes(Model model) {
        model.addAttribute("background", baseConfig.getBackground());
    }
}
