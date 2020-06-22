package org.r.framework.thrift.common.util;

import java.util.Collection;

/**
 * date 2020/6/22 上午9:47
 *
 * @author casper
 **/
public class CollectionUtils {


    /**
     * 判断集合是否为空
     *
     * @param target 集合
     * @param <T>    泛型参数
     * @return
     */
    public static <T> boolean isEmpty(Collection<T> target) {
        return target == null || target.isEmpty();
    }

    /**
     * 判断集合是否不为空
     *
     * @param target 集合
     * @param <T>    泛型参数
     * @return
     */
    public static <T> boolean isNotEmpty(Collection<T> target) {
        return !isEmpty(target);
    }

}
