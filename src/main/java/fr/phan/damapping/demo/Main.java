package fr.phan.damapping.demo;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * Main -
 */
public class Main {

    public static void main(String[] args) {
        ApplicationContext applicationContext = new AnnotationConfigApplicationContext(AppConfiguration.class);
        AnyService service = applicationContext.getBean(AnyService.class);

        System.out.println(service.method1());
        System.out.println(service.method2());
        System.out.println(service.method3());
    }
}
