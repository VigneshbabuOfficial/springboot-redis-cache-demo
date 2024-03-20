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
