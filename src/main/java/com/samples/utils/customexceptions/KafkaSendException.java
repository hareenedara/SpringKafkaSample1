package com.samples.utils.customexceptions;

/**
 * Created by I681336 on 8/28/2017.
 */
public class KafkaSendException extends Exception {
    private String key;
    private String val;
    private String errorMsg;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getVal() {
        return val;
    }

    public void setVal(String val) {
        this.val = val;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }
}
