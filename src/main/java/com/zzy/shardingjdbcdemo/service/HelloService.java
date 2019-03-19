package com.zzy.shardingjdbcdemo.service;

import com.zzy.shardingjdbcdemo.po.T;

import java.util.List;

/**
 * @auther: zhangzhaoyuan
 * @date: 2019/03/11
 * @description:
 */

public interface HelloService {
    void delayBatchInsert(List<T> t);

    void batchInsert(List<T> t);

}
