package com.js.encrypt.encryption.enums;

/**
 *
 */
public enum EncodingEnum {

    /**
     * 默认编码
     */
    DEFAULT_ENCODING("UTF-8"),
    ;

    private String encoding;

    EncodingEnum(String encoding) {
        this.encoding = encoding;
    }

    public String getEncoding() {
        return encoding;
    }
}
