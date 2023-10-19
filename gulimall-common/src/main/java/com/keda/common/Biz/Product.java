package com.keda.common.Biz;

/**
 * @author Jmd
 * @create 2023-10-2023/10/18-22:11
 * @Description：
 */
public enum Product {
    ;
    public enum AttrTypeEnum{
        ATTR_TYPE_BASE(1,"base"),
        ATTR_TYPE_SALE(0,"sale");

        private Integer code;

        private String type;

        AttrTypeEnum(Integer code, String type){
            this.code = code;
            this.type = type;
        }

        public Integer getCode() {
            return code;
        }

        public void setCode(Integer code) {
            this.code = code;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }
    }
}
