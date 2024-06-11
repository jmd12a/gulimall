package com.keda.common.Biz;

/**
 * @author Jmd
 * @create 2024-03-2024/3/25-23:11
 * @Description：
 */
public class WareConstant {

    public enum PurchaseStatusEnum{

        CREATE(0,"新建"),
        RECEIVE(1,"已领取"),
        ASSIGNED(2,"已分配");

        private Integer code;
        private String message;


        PurchaseStatusEnum(Integer code, String message) {
            this.code = code;
            this.message = message;
        }

        public Integer getCode() {
            return code;
        }

        public void setCode(Integer code) {
            this.code = code;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
}
