/*
 *
 * Copyright (c) 2005-2017 All Rights Reserved.
 */
package com.z.rpc.common.filter;

import java.util.Comparator;

/**
 * @author <a href=mailto:someharder@gmail.com>yinyu</a> 2020/7/28
 */
public class FilterComparator implements Comparator<Filter> {

    @Override
    public int compare(Filter o1, Filter o2) {          
        //order值越小排在越前面
        return o1.order()<o2.order()?-1:0;
    }
}
