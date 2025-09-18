package com.forum; // 定义包名

import org.apache.catalina.LifecycleException;

import com.forum.config.TomcatConfig;

/**
 */
public class Main {

    public static void main(String[] args) {
        try {
            TomcatConfig.start();
        } catch (LifecycleException e) {
            System.exit(1);
        }
    }
}
