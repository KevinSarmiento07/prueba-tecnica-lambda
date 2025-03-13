package com.kass.dao;

import com.kass.models.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ProductDao extends JpaRepository<Product, Integer>, JpaSpecificationExecutor<Product> {

}