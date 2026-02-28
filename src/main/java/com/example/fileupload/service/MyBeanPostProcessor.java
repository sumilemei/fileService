package com.example.fileupload.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class MyBeanPostProcessor implements BeanPostProcessor {

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if(bean instanceof FileService){
            log.warn("第四步遍历所有注册的BeanPostProcessor，调用其postProcessBeforeInitialization方法");
            log.warn("开始初始化bean{}-{}", beanName, System.currentTimeMillis());
        }
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if(bean instanceof FileService){
            log.warn("第六步再次遍历所有注册的BeanPostProcessor，调用其postProcessAfterInitialization方法，{}bean初始化完成-{}", bean, System.currentTimeMillis());
        }
        return bean;
    }

}
