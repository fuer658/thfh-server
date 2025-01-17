package com.thfh.common;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.HashMap;
import java.util.Map;

@Data
@Accessors(chain = true)
public class R {
    private Integer code;
    private String message;
    private Map<String, Object> data = new HashMap<>();

    private R() {}

    public static R ok() {
        R r = new R();
        r.setCode(200);
        r.setMessage("success");
        return r;
    }

    public static R error() {
        R r = new R();
        r.setCode(500);
        r.setMessage("error");
        return r;
    }

    public static R error(String message) {
        R r = new R();
        r.setCode(500);
        r.setMessage(message);
        return r;
    }

    public static R error(Integer code, String message) {
        R r = new R();
        r.setCode(code);
        r.setMessage(message);
        return r;
    }

    public R data(Object value) {
        this.data.put("data", value);
        return this;
    }

    public R data(String key, Object value) {
        this.data.put(key, value);
        return this;
    }

    public R data(Map<String, Object> map) {
        this.setData(map);
        return this;
    }
}