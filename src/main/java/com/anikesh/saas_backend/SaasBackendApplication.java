package com.anikesh.saas_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableAspectJAutoProxy(proxyTargetClass = true)
@EnableTransactionManagement(order = 100)
public class SaasBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(SaasBackendApplication.class, args);
	}

}
