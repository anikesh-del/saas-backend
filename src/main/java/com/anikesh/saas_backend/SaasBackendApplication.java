package com.anikesh.saas_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement 
public class SaasBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(SaasBackendApplication.class, args);
	}

}
