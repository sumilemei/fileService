package com.example.fileupload.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ConditionService {

    public ConditionService(){
        log.warn("conditionService注入了FileServiceBean中，先创建这个依赖的bean,需要注意，这是第一步");
    }
}
