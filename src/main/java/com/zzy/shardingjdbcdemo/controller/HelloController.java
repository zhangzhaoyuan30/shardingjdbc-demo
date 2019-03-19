package com.zzy.shardingjdbcdemo.controller;

import com.zzy.shardingjdbcdemo.dao.TMapper;
import com.zzy.shardingjdbcdemo.po.T;
import com.zzy.shardingjdbcdemo.service.HelloService;
import com.zzy.shardingjdbcdemo.util.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;


/**
 * @auther: zhangzhaoyuan
 * @date: 2019/01/29
 * @description:
 */
@RestController
public class HelloController {

    private final Logger logger = LoggerFactory.getLogger(HelloController.class);

    @Autowired
    private HelloService helloService;
    @Autowired
    private TMapper tMapper;


    @RequestMapping("insert")
    public Result hello1() {
        T t = new T();
        t.setA("aaa");
        t.setB("bbb");
        t.setC("ccc");
        List<T> tList = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            tList.add(t);
        }
        Instant time1 = Instant.now();
        helloService.batchInsert(tList);
        Instant time2 = Instant.now();
        Duration between = Duration.between(time1, time2);
        return Result.success(between.toMillis());
    }

    @RequestMapping("query")
    public Result hello2() {
        return Result.success(tMapper.selectAll().size());
    }

}
