package com.keda.common.Biz;

public enum BizcodeEnum {
    VAILD_Exception(400,"参数验证错误"),
    UNKNOW_Exception(400,"未知错误"),
    Runtime_Exception(400,"运行时错误");

    private Integer code;
    private String msg;

    BizcodeEnum(Integer code,String msg){
        this.code = code;
        this.msg = msg;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
