package com.product.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.product.entity.Product;

public interface ProductRepo extends JpaRepository<Product, String> {

	List<Product> findByProductNameAndProductType(String productName, String productType);
	 List<Product> findByProductName(String productName);

	    List<Product> findByProductType(String productType);
}
