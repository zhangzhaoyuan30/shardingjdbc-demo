package com.zzy.shardingjdbcdemo.controller;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
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
import java.util.Random;


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
    public Result insert() {
        List<T> tList = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            T t = new T();
            t.setA("aaa");
            t.setB("bbb");
            t.setC((int) (Math.random() * 10));
            tList.add(t);
        }
        Instant time1 = Instant.now();
        helloService.batchInsert(tList);
        Instant time2 = Instant.now();
        Duration between = Duration.between(time1, time2);
        return Result.success(between.toMillis());
    }

    @RequestMapping("queryAll")
    public Result queryAll() {
        return Result.success(tMapper.selectAll().size());
    }

    @RequestMapping("queryById")
    public Result query(Long id) {
        return Result.success(tMapper.selectByPrimaryKey(id));
    }

    @RequestMapping("queryByPage")
    public Result queryPage() {
        PageHelper.startPage(2, 10);
        List<T> ts = tMapper.selectAll();
        return Result.success(new PageInfo<>(ts));
    }

    @RequestMapping("queryByOrder")
    public Result queryByOrder() {
        List<T> ts = tMapper.queryByOrder();
        return Result.success(ts);
    }

    @RequestMapping("queryBetween")
    public Result queryBetween(Long id1, Long id2) {
        List<T> ts = tMapper.queryBetween(id1, id2);
        return Result.success(ts);
    }


}
