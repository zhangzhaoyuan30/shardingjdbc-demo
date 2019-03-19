package com.zzy.shardingjdbcdemo.util;

import com.zzy.shardingjdbcdemo.constant.CommonConstants;
import lombok.Data;

/**
 * @auther: zhangzhaoyuan
 * @date: 2018/11/28
 * @description:
 */
@Data
public class Result {

    private static final long serialVersionUID = 1L;

    private int code;

    private String msg;

    private Object data;

    public static Result buildResult(int resultCode, String resultDesc, Object resultData) {
        Result result = new Result();
        result.setCode(resultCode);
        result.setMsg(resultDesc);
        result.setData(resultData);

        return result;
    }

    /**
     * 返回成功信息
     *
     * @return
     */
    public static Result success() {
        return buildResult(CommonConstants.SUCCESS, "操作成功", null);
    }

    public static Result success(Object resultData) {
        return buildResult(CommonConstants.SUCCESS, "操作成功", resultData);
    }


    /**
     * 返回错误消息
     *
     * @return 错误消息
     */
    public static Result error() {
        return buildResult(CommonConstants.FAIL, "操作失败", null);
    }


    /**
     * 自定义错误消息
     *
     * @param msg
     * @return
     */
    public static Result error(String msg) {
        return buildResult(CommonConstants.FAIL, msg, null);
    }

    public static Result toResult(int rows) {
        return rows > 0 ? Result.success() : Result.error();
    }


}
