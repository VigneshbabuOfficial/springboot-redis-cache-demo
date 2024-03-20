package com.redis.demo;

import java.util.List;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/product")
public class ProductController {

	private Logger log = Logger.getLogger("ProductController");

	@Autowired
	private ProductRepository repository;
	
	@Autowired
	private ProductService productService;

	@GetMapping
	public List<Product> getAllProducts() {
		log.info("getAllProducts");
		return repository.findAll();
	}

	@GetMapping("/{id}")
	public ResponseEntity<Product> getProductById(@PathVariable Long id) {
		log.info("getProductById, ID = " + id);
		return productService.getProductById(id);
	}
	
	@PostMapping
	public Product addProduct(@RequestBody Product product) {
		return repository.save(product);
	}

	@PutMapping("/{id}")
	public Product editProduct(@PathVariable Long id, @RequestBody Product product) {
		log.info("editProduct, ID = " + id + ", product = " + product);
		return productService.editProduct(id,product);
	}

	@DeleteMapping("/{id}")
	public String deleteProductById(@PathVariable Long id) {
		log.info("deleteProductById, ID = " + id);
		return productService.deleteProductById(id);
	}

}
