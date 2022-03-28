package com.js.encrypt.enums;

/**
 *
 */
public enum ParamEnum {

    SIGN("sign"),
    ;
    private String paramName;

    ParamEnum(String paramName) {
        this.paramName = paramName;
    }

    public String getParamName() {
        return paramName;
    }
}
