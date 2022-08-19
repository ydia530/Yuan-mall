package com.yuan.common.constant;

import java.util.HashMap;
import java.util.Map;

public enum OrderStatusEnum {
    CREATE_NEW(0,"待付款"),
    PAYED(1,"已付款,待发货"),
    SENDED(2,"待收货"),
    RECIEVED(3,"已完成"),
    CANCLED(4,"已取消，支付超时"),
    SERVICING(5,"售后中"),
    SERVICED(6,"售后完成");
    private Integer code;
    private String msg;

    OrderStatusEnum(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public Integer getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }


    private static Map<Integer,OrderStatusEnum > zyMap = new HashMap<>();
    static {
        for (OrderStatusEnum value : OrderStatusEnum.values()) {
            zyMap.put(value.getCode(),value);
        }
    }
    public static OrderStatusEnum getByCode(Integer code) {
        return zyMap.get(code);
    }
}
