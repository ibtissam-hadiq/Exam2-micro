package com.example.productservice.service;

import com.example.productservice.Repo.ProductRepo;
import com.example.productservice.entite.Product;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {
    @Autowired
    private ProductRepo productRepository;

    @Transactional
    public Product createProduct(Product product) {

        return productRepository.save(product);
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Product getProductById(Long id) {

        return productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));
    }

    @Transactional
    public Product updateProduct(Long id, Product productDetails) {
        Product product = getProductById(id);
        product.setName(productDetails.getName());
        product.setDescription(productDetails.getDescription());
        product.setPrice(productDetails.getPrice());
        product.setStock(productDetails.getStock());

        return productRepository.save(product);
    }

    @Transactional
    public void deleteProduct(Long id) {
        Product product = getProductById(id);
        productRepository.delete(product);
    }

    @Transactional
    public Product updateStock(Long id, int quantity) {
        Product product = getProductById(id);

        int newStock = product.getStock() + quantity;
        if (newStock < 0) {
            throw new RuntimeException("Insufficient stock for product: " + id);
        }

        product.setStock(newStock);
        return productRepository.save(product);
    }



    public boolean isProductInStock(Long id) {

        Product product = getProductById(id);
        return product.getStock() > 0;
    }
}
