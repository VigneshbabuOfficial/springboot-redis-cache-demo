# Springboot Redis Cache Demo

## what is cache mechanism ? why it's using ?
> The cache mechanism stores frequently accessed data in a temporary storage layer between the server and the database. When a client requests data, the server first checks the cache for a matching entry. If found, the data is returned directly from the cache, reducing the need for repeated queries to the database and improving system performance


> JPA itself providing the 2 levels of Cache. <br/>
> JPA First level Cache - will cache the particular table / entity query execution within the same Transaction and that cache wont be avail in next Transaction or client Request. @Transactional annotation is used to mark the method to use same Transaction. Otherwise it'll create new Transaction for all the query execution. <br/>
> JPA Second level Cache - will be used to cache the particular entity so @Cacheable annotation is used at Entity level. So whenever a client requesting the same resource query execution will be cached after the initial one and this will be stored in Persistent Context memory. <br/>
> But Redis Cache will store / cache the entire response for a Client request. <br/>
> REF : https://www.youtube.com/watch?v=AvW94hRknmA&list=PLaLGeHpx4nQmhr6TtLtcSk_rQ4j0q6Dvi&index=43    ||||||    https://www.youtube.com/watch?v=oHVs4gK0MtU

## why Redis Cache ?


## Limitations of Redis Cache


## Redis Interview Questions


-------------------------------------

## Redis Cache configration

```
open CMD and execute the below commands one by one.

curl -fsSL https://packages.redis.io/gpg | sudo gpg --dearmor -o /usr/share/keyrings/redis-archive-keyring.gpg

echo "deb [signed-by=/usr/share/keyrings/redis-archive-keyring.gpg] https://packages.redis.io/deb $(lsb_release -cs) main" | sudo tee /etc/apt/sources.list.d/redis.list

sudo apt-get update
sudo apt-get install redis

sudo service redis-server start

To verify
redis-cli <press enter>

127.0.0.1:6379> ping
PONG

> REF :
https://redis.io/docs/install/install-redis/install-redis-on-linux/#install-on-ubuntu-debian
https://redis.io/docs/install/install-redis/install-redis-on-windows/
``` 

## Springboot app with Redis Cache Implementation
> STEP-1:-
>
Created a Springboot maven JAR project with Web, JPA, Postgres , Redis and Lombok dependencies.

![image](https://github.com/VigneshbabuOfficial/springboot-redis-cache-demo/assets/70185865/878ae31a-aa0c-42fc-af00-617bcac8b5ed)

> step-2:-
>
Create an application.yaml file and apply these content.
```
spring:
  cache:
    type: redis
    host: localhost
    port: 6379
    redis:
      time-to-live: 60000
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

> step-3:-
> 
Create all these classes

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
	@Cacheable(value = "product", key = "#id",condition = "#result != null")
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

> step-4:-
>

```JS
Open Postman tool and hit the endpoint  GET  http://localhost:8080/product.
Copy the ID and hit the endpoint GET http://localhost:8080/product/152 twice. We can see the log printed for one time. that means the second request got the response from the Cache memory.
Likewise try PUT and DELETE requests also.
```

