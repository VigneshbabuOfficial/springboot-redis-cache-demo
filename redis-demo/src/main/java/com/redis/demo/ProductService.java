package com.redis.demo;

import org.springframework.http.ResponseEntity;

public interface ProductService {

	public ResponseEntity<Product> getProductById(Long id);
	
	public Product addProduct(Product product);
	
	public Product editProduct(Long id, Product product);
	
	public String deleteProductById(Long id);
}
