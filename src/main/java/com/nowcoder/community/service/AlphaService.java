package com.nowcoder.community.service;

import com.nowcoder.community.dao.AlphaDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@Service
public class AlphaService {

    public AlphaService(){
        System.out.println("实例化Service");
    }

    @PostConstruct
    public void init(){
        System.out.println("初始化Service");
    }

    @PreDestroy
    public void destroy(){
        System.out.println("销毁Service");
    }

    @Autowired
    @Qualifier("alphaDaoImp2")
    AlphaDao alphaDao;
    public String service(){
        return alphaDao.select();
    }
}
