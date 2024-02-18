package com.redis.demo;

import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CacheConfig(cacheNames = "product")
@RestController
@RequestMapping("/product")
public class ProductController {

	private Logger log = Logger.getLogger("ProductController");

	@Autowired
	private ProductRepository repository;

	@GetMapping
	public List<Product> getAllProducts() {
		return repository.findAll();
	}

	@GetMapping("/{id}")
	@Cacheable(value = "product", key = "#id")
	public Product getProductById(@PathVariable long id) {
		log.info("getProductById, ID = " + id);
		Optional<Product> productOptional = repository.findById(id);
		return productOptional.isEmpty() ? null : productOptional.get();
	}

	@PostMapping
	public Product addProduct(@RequestBody Product product) {
		return repository.save(product);
	}

	@PutMapping("/{id}")
	@CachePut(cacheNames = "product", key = "#id")
	public Product editProduct(@PathVariable long id, @RequestBody Product product) {
		log.info("editProduct, ID = " + id + ", product = " + product);
		Optional<Product> productOptional = repository.findById(id);
		if (productOptional.isPresent()) {
			product.setId(id);
			return repository.save(product);
		} else
			return null;
	}

	@DeleteMapping("/{id}")
	@CacheEvict(cacheNames = "product", allEntries = true)
	public String removeProductById(@PathVariable long id) {
		log.info("removeProductById, ID = " + id);
		Optional<Product> productOptional = repository.findById(id);
		if (productOptional.isPresent()) {
			repository.delete(productOptional.get());
			return "SUCCESS";
		} else
			return "ERROR";
	}

}
