package com.zzy.shardingjdbcdemo.service.impl;

import com.zzy.shardingjdbcdemo.dao.TMapper;
import com.zzy.shardingjdbcdemo.po.T;
import com.zzy.shardingjdbcdemo.service.HelloService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @auther: zhangzhaoyuan
 * @date: 2019/03/11
 * @description:
 */
@Service
public class HelloServiceImpl implements HelloService {

    @Autowired
    private TMapper tMapper;

    @Override
    public void delayBatchInsert(List<T> t) {
    }

    @Override
    public void batchInsert(List<T> t) {
        tMapper.batchInsert(t);
    }
}
