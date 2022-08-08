package com.yuan.mall.member.exception;

/**
 * @author Yuan Diao
 * @date 2022/3/1
 */
public class UserNameExistException extends RuntimeException {
    public UserNameExistException() {
        super("用户名存在");
    }
}

