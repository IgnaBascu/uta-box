package com.utabox.catalogo_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class CatalogoServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(CatalogoServiceApplication.class, args);
	}

}
