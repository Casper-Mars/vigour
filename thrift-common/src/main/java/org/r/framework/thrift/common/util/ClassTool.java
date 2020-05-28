package org.r.framework.thrift.common.util;

/**
 * date 20-5-9 上午11:32
 *
 * @author casper
 **/
public class ClassTool {


    public static Class<?> filterClass(Class<?>[] list, String nameIndicator) {
        if (list == null || list.length == 0) {
            return null;
        }
        for (Class<?> aClass : list) {
            /*后缀匹配*/
            if (aClass.getName().endsWith(nameIndicator)) {
                return aClass;
            }
        }
        return null;
    }




}
