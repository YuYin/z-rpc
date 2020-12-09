/*
 *
 * Copyright (c) 2005-2017 All Rights Reserved.
 */
package com.z.rpc.samples.server.spring;

import com.google.common.base.Splitter;
import com.z.rpc.server.RpcService;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.core.type.filter.AnnotationTypeFilter;

/**
 * @author <a href=mailto:someharder@gmail.com>yinyu</a> 2020/7/31
 */
public class ServiceAnnotationBeanPostProcessor implements BeanDefinitionRegistryPostProcessor {

    	private String scanPackage;
	public ServiceAnnotationBeanPostProcessor(String scanPackage) {
		this.scanPackage = scanPackage;
	}
    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
       	if (this.scanPackage != null) {
			ClassPathBeanDefinitionScanner scanner = new ClassPathBeanDefinitionScanner(registry, false);
			scanner.addIncludeFilter(new AnnotationTypeFilter(RpcService.class));
			scanner.scan(Splitter.on(",").splitToList(scanPackage).toArray(new String[0]));
		}
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {

    }
}
