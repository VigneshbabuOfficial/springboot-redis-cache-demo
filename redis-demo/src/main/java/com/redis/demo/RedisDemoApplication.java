package com.redis.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

import jakarta.annotation.PostConstruct;

@EnableCaching
@SpringBootApplication
public class RedisDemoApplication {

	@Autowired
	private ProductRepository repository;
	
	public static void main(String[] args) {
		SpringApplication.run(RedisDemoApplication.class, args);
	}

	@PostConstruct
	void createNewProducts() {

		Product product_1 = new Product();
		product_1.setName("product_1");
		product_1.setCode("product_1");
		product_1.setQuantity(5);
		product_1.setPrice(1000.50);

		if (repository.findProductByName(product_1.getName()).isEmpty()) {
			repository.save(product_1);
			System.out.println("product_1 saved into DB");
		}

	}

}
