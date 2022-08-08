package com.yuan.mall.member.exception;

/**
 * @author Yuan Diao
 * @date 2022/3/1
 */
public class PhoneExistException extends RuntimeException {
    public PhoneExistException() {
        super("手机号存在");
    }
}
