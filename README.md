```JAVA
spring:
  cache:
    type: redis
    redis:
      time-to-live: 60000
      key-prefix: "redis-cache-demo:"
  datasource:
    url: jdbc:postgresql://localhost/practice_db
    username: postgres
    password: admin
    driver-class-name: org.postgresql.Driver
  jpa:
    show-sql: true
    hibernate:
      ddl-auto: update
    properties:
      hibernate:
        dialect: org.hibernate.dialect.PostgreSQLDialect

```

```JAVA
package com.redis.demo;

import java.io.Serializable;

import org.hibernate.annotations.DynamicUpdate;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table
@DynamicUpdate
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Product implements Serializable {

	private static final long serialVersionUID = 2212719282319205920L;
	
	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private String name;
    private String code;
    private int quantity;
    private double price;
}


```

```JAVA
package com.redis.demo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
	
	Optional<Product> findProductByName(String name);
}


```

```JAVA
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


```

```JAVA
package com.redis.demo;

import org.springframework.http.ResponseEntity;

public interface ProductService {

	public ResponseEntity<Product> getProductById(Long id);
	
	public Product addProduct(Product product);
	
	public Product editProduct(Long id, Product product);
	
	public String deleteProductById(Long id);
}


```

```JAVA
package com.redis.demo;

import java.util.Objects;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class ProductServiceImpl implements ProductService {

	private Logger log = Logger.getLogger("ProductServiceImpl");

	@Autowired
	private ProductDAO productDAO;

	@Override
	public Product addProduct(Product product) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Product editProduct(Long productId, Product product) {
		log.info("editProduct, productId = " + productId);
		Product dbProduct = productDAO.findById(productId);
		if (Objects.nonNull(dbProduct)) {
			product.setId(productId);
			return productDAO.editProduct(product);
		} else
			return null;
	}

	@Override
	public ResponseEntity<Product> getProductById(Long productId) {
		log.info("getProductById, productId = " + productId);
		Product product = productDAO.findById(productId);
		if (Objects.isNull(product)) {
			log.warning("getProductById, productId = " + productId + ", Product not found");
			return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<>(product, HttpStatus.OK);
	}

	@Override
	public String deleteProductById(Long productId) {
		Product product = productDAO.findById(productId);
		if (Objects.nonNull(product)) {
			productDAO.deleteProduct(product);
			return "SUCCESS";
		} else
			return "ERROR";
	}

}


```

```JAVA
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


```


```JAVA
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


```
