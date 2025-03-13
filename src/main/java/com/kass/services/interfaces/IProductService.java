package com.kass.services.interfaces;

import com.kass.models.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IProductService {
    
    public Page<Product> findAll(String name, Double minPrice, Double maxPrice, Pageable pageable);
    
    public Product findById(Integer id);
    
    public Product save(Product product);
    
    public void deleteById(Integer id);
    
    public Product update(Product product);
    
}