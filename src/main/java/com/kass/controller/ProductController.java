package com.kass.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kass.models.Product;
import com.kass.services.interfaces.IProductService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/products")
public class ProductController {
    
    @Autowired
    private IProductService productService;
    
    @GetMapping
    public ResponseEntity<?> listProducts(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection) {
        
        Sort sort = sortDirection.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        return ResponseEntity.ok(productService.findAll(name, minPrice, maxPrice, pageable));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<?> getProduct(@PathVariable Integer id) {
        try {
            Product product = productService.findById(id);
            if (product == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(productService.findById(id));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error");
        }
    }
    
    @PostMapping
    public ResponseEntity<?> createProduct(@RequestBody @Valid Product product, BindingResult result) {
        try {
            if (result.hasErrors()) {
                return validation(result);
            }
            return ResponseEntity.ok(productService.save(product));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @PutMapping
    public ResponseEntity<?> updateProduct(@RequestBody @Valid Product product, BindingResult result) {
        try {
            if (result.hasErrors()) {
                return validation(result);
            }
            return ResponseEntity.ok(productService.update(product));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable Integer id) {
        try {
            productService.deleteById(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    private ResponseEntity<?> validation(BindingResult result) {
        Map<String, Object> map = new HashMap<>();
        result.getFieldErrors().forEach(err -> {
            map.put(err.getField(), err.getDefaultMessage());
        });
        
        return new ResponseEntity<>(map, HttpStatus.BAD_REQUEST);
    }
}

