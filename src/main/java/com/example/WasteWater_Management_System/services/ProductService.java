package com.example.WasteWater_Management_System.services;

import com.example.WasteWater_Management_System.exception.ResourceNotFoundException;
import com.example.WasteWater_Management_System.models.Product;
import com.example.WasteWater_Management_System.models.enums.ProductType;
import com.example.WasteWater_Management_System.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public List<Product> getProductsByType(ProductType productType) {
        return productRepository.findByProductType(productType);
    }

    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + id));
    }

    @Transactional
    public Product createProduct(Product product) {
        return productRepository.save(product);
    }

    @Transactional
    public Product updateProduct(Long id, Product updated) {
        Product product = getProductById(id);
        product.setProductName(updated.getProductName());
        product.setProductType(updated.getProductType());
        product.setDescription(updated.getDescription());
        product.setUnit(updated.getUnit());
        product.setUnitPrice(updated.getUnitPrice());
        product.setActive(updated.getActive());
        return productRepository.save(product);
    }

    @Transactional
    public void deleteProduct(Long id) {
        Product product = getProductById(id);
        productRepository.delete(product);
    }
}
