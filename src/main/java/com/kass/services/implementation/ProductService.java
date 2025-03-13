package com.kass.services.implementation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.kass.dao.ProductDao;
import com.kass.models.Product;
import com.kass.services.interfaces.IProductService;
import com.kass.util.filters.ProductSpecification;

@Service
public class ProductService implements IProductService {
    
    @Autowired
    private ProductDao productDao;
    
    @Override
    public void deleteById(Integer id) {
        productDao.deleteById(id);
    }
    
    @Override
    public Page<Product> findAll(String name, Double minPrice, Double maxPrice, Pageable pageable) {
        Specification<Product> spec = Specification.where(ProductSpecification.nameContains(name)).and(ProductSpecification.priceBetween(minPrice, maxPrice));
        return productDao.findAll(spec, pageable);
    }
    
    @Override
    public Product findById(Integer id) {
        return productDao.findById(id).orElse(null);
    }
    
    @Override
    public Product save(Product product) {
        return productDao.save(product);
    }
    
    @Override
    public Product update(Product product) {
        return productDao.save(product);
    }
    
    
    
}

