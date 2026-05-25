package com.natwest.tc.dto.response;

import java.io.Serializable;

public class ErrorResponse implements Serializable {
    private int code;
    private String message;
    private Object body;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getBody() {
        return body;
    }

    public void setBody(Object body) {
        this.body = body;
    }
}
