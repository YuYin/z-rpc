package com.z.rpc.samples.server.spring;

import com.z.rpc.samples.utils.BeanFactoryUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.concurrent.TimeUnit;

public class SpringServerStarter {
    private static final Logger logger = LoggerFactory.getLogger(SpringServerStarter.class);

    public static void main(String[] args) throws Exception {
        AnnotationConfigApplicationContext annotationConfigApplicationContext=new AnnotationConfigApplicationContext();
        annotationConfigApplicationContext.register(RPCServerConfig.class);
    //    annotationConfigApplicationContext.getBeanFactory().addBeanPostProcessor(new RPCServerLoadServiceBPP(annotationConfigApplicationContext));
        BeanFactoryUtils.setBeanFactory(annotationConfigApplicationContext.getBeanFactory());
        annotationConfigApplicationContext.refresh();
        while ((!Thread.currentThread().isInterrupted())){
            try {
                TimeUnit.HOURS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
