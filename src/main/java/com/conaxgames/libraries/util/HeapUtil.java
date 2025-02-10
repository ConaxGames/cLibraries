package com.conaxgames.libraries.util;

import javax.management.MBeanServer;
import java.lang.management.ManagementFactory;
import java.lang.reflect.Method;

public class HeapUtil {

    private static volatile Object hotspotMBean;

    public static void dumpHeap(String fileName, boolean live) {
        initHotspotMBean();
        try {
            Class<?> clazz = Class.forName("com.sun.management.HotSpotDiagnosticMXBean");
            Method m = clazz.getMethod("dumpHeap", String.class, boolean.class);
            m.invoke(hotspotMBean, fileName, live);
        } catch (RuntimeException re) {
            throw re;
        } catch (Exception exp) {
            throw new RuntimeException(exp);
        }
    }

    private static void initHotspotMBean() {
        if (hotspotMBean == null)
            synchronized (HeapUtil.class) {
                if (hotspotMBean == null)
                    hotspotMBean = getHotspotMBean();
            }
    }

    private static Object getHotspotMBean() {
        try {
            Class<?> clazz = Class.forName("com.sun.management.HotSpotDiagnosticMXBean");
            MBeanServer server = ManagementFactory.getPlatformMBeanServer();
            Object bean = ManagementFactory.newPlatformMXBeanProxy(server, "com.sun.management:type=HotSpotDiagnostic", clazz);
            return bean;
        } catch (RuntimeException re) {
            throw re;
        } catch (Exception exp) {
            throw new RuntimeException(exp);
        }
    }
}
