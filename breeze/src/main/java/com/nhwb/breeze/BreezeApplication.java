package com.nhwb.breeze;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.AnnotationBeanNameGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.validation.constraints.NotNull;
import java.util.Objects;

@SpringBootApplication
public class BreezeApplication {

    public static void main(String[] args) {
//        SpringApplication.run(BreezeApplication.class, args);
        new SpringApplicationBuilder(BreezeApplication.class)
                .beanNameGenerator(new UniqueNameGenerator())
                .run(args);
    }
    /**
     * 由于需要混淆代码,混淆后类都是A B C,spring 默认是把A B C当成BeanName,BeanName又不能重复导致报错
     * 所以需要重新定义BeanName生成策略
     */
    @Component("UniqueNameGenerator")
    public static class UniqueNameGenerator extends AnnotationBeanNameGenerator
    {
        /**
         * 重写buildDefaultBeanName
         * 其他情况(如自定义BeanName)还是按原来的生成策略,只修改默认(非其他情况)生成的BeanName带上包名
         */
        @Override
        public @NotNull String buildDefaultBeanName(BeanDefinition definition)
        {
            //全限定类名
            return Objects.requireNonNull(definition.getBeanClassName());
        }
    }
}
