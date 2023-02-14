package com.reggie.takeout.common;

/**
 * @author shenlijia
 */
public class BasicContext {

    public static ThreadLocal<Long> threadLocal = new ThreadLocal<>();

    public static Long getCurrentId() {
        return threadLocal.get();
    }

    public static void setCurrentId(Long id) {
        threadLocal.set(id);
    }
}
