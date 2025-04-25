package com.example.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.UnknownHostException;

public final class NetUtil {
    private static final Logger log = LoggerFactory.getLogger(NetUtil.class);
    public static final String LOCAL_HOST;

    private NetUtil() {
    }

    static {
        try {
            LOCAL_HOST = InetAddress.getLocalHost().getHostName();
            log.info("本机主机名: {}", LOCAL_HOST);
        } catch (UnknownHostException e) {
            log.error("获取本机主机名失败", e);
            throw new RuntimeException(e);
        }
    }
}
