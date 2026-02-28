package com.example.fileupload.service;


import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;



/**
 * 以此来观察bean的生命周期
 */
@Service
@Slf4j
public class FileService implements BeanNameAware, BeanClassLoaderAware, BeanFactoryAware, ApplicationContextAware,
        ResourceLoaderAware, EnvironmentAware {

    @Resource
    private ConditionService conditionService;

    public FileService(){
        log.warn("FileServiceBean的构造方法,这是第二步");
    }

    @Override
    public void setBeanClassLoader(ClassLoader classLoader) {
        String name = classLoader.getName();
        log.warn("第三步BeanClassLoaderAware扩展，调用setBeanClassLoader方法，传入类加载器！");
        log.warn("类加载器名称{}",name);
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        boolean flag = beanFactory.containsBean("adc");
        log.warn("第三步BeanFactoryAware扩展，调用setBeanFactory方法，传入当前的beanFactory！");
        log.warn("当前的beanFactory存在adc这个bean？{}",flag);
    }

    @Override
    public void setBeanName(String s) {
        log.warn("第三步BeanNameAware扩展，调用setBeanName方法，传入bean在容器中的名称！");
        log.warn("bean在容器中的名称{}",s);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        Environment environment = applicationContext.getEnvironment();
        String property = environment.getProperty("server.port");
        log.warn("第三步ApplicationContextAware扩展，调用setApplicationContext方法，传入当前的ApplicationContext！");
        log.warn("通过ApplicationContext获取的environment属性{}",property);

    }

    @Override
    public void setEnvironment(Environment environment) {
        String property = environment.getProperty("server.port");
        log.warn("第三步EnvironmentAware扩展，调用setEnvironment方法,传入当前的environment！");
        log.warn("environment属性{}",property);
    }

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        ClassLoader classLoader = resourceLoader.getClassLoader();
        String name = classLoader.getName();
        log.warn("第三步ResourceLoaderAware扩展，调用setResourceLoader方法，传入当前的resourceLoader！");
        log.warn("当前通过resourceLoader获取的classLoader名称为{}",name);
    }

    @PostConstruct
    public void init(){
        log.warn("第五步，init初始化bean");
    }

    @PreDestroy
    public void myDestroy() {
        System.out.println("第七步，@PreDestroy / destroy-method，执行销毁方法！");
    }

}
