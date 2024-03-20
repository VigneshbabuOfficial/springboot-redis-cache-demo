package com.redis.demo;

import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

@Component
public class ProductDAO {

	private Logger log = Logger.getLogger("ProductDAO");

	@Autowired
	private ProductRepository repository;

	@Cacheable(value = "Product", key = "#productId")
	public Product findById(Long productId) {
		log.info("findById, productId = " + productId);
		return repository.findById(productId).orElse(null);
	}

	@CachePut(cacheNames = "Product", key = "#product.id")
	public Product editProduct(Product product) {
		log.info("editProduct, productId = " + product.getId());
		return repository.save(product);
	}

	@CacheEvict(cacheNames = "Product", key = "#product.id")
	public void deleteProduct(Product product) {
		repository.delete(product);
	}

}
