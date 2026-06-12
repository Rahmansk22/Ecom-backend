package com.ecommerce.platform.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "product_variants", indexes = {
        @Index(name = "idx_variants_sku", columnList = "sku", unique = true)
})
public class ProductVariant {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false, unique = true)
    private String sku;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal price;

    @Column(name = "compare_at_price", precision = 12, scale = 2)
    private BigDecimal compareAtPrice;

    @Column(nullable = false)
    private Integer stock = 0;

    @Column(name = "attributes_json", columnDefinition = "TEXT")
    private String attributesJson; // e.g. '{"color":"Titanium Gray","storage":"256GB"}'

    @Column(precision = 8, scale = 2)
    private BigDecimal weight;

    @Column(precision = 8, scale = 2)
    private BigDecimal length;

    @Column(precision = 8, scale = 2)
    private BigDecimal width;

    @Column(precision = 8, scale = 2)
    private BigDecimal height;

    public ProductVariant() {}

    public ProductVariant(UUID id, Product product, String sku, BigDecimal price, BigDecimal compareAtPrice, 
                          Integer stock, String attributesJson, BigDecimal weight, BigDecimal length, BigDecimal width, BigDecimal height) {
        this.id = id;
        this.product = product;
        this.sku = sku;
        this.price = price;
        this.compareAtPrice = compareAtPrice;
        this.stock = stock != null ? stock : 0;
        this.attributesJson = attributesJson;
        this.weight = weight;
        this.length = length;
        this.width = width;
        this.height = height;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }

    public String getSku() { return sku; }
    public void setSku(String sku) { this.sku = sku; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public BigDecimal getCompareAtPrice() { return compareAtPrice; }
    public void setCompareAtPrice(BigDecimal compareAtPrice) { this.compareAtPrice = compareAtPrice; }

    public Integer getStock() { return stock; }
    public void setStock(Integer stock) { this.stock = stock; }

    public String getAttributesJson() { return attributesJson; }
    public void setAttributesJson(String attributesJson) { this.attributesJson = attributesJson; }

    public BigDecimal getWeight() { return weight; }
    public void setWeight(BigDecimal weight) { this.weight = weight; }

    public BigDecimal getLength() { return length; }
    public void setLength(BigDecimal length) { this.length = length; }

    public BigDecimal getWidth() { return width; }
    public void setWidth(BigDecimal width) { this.width = width; }

    public BigDecimal getHeight() { return height; }
    public void setHeight(BigDecimal height) { this.height = height; }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private UUID id;
        private Product product;
        private String sku;
        private BigDecimal price;
        private BigDecimal compareAtPrice;
        private Integer stock;
        private String attributesJson;
        private BigDecimal weight;
        private BigDecimal length;
        private BigDecimal width;
        private BigDecimal height;

        public Builder id(UUID id) { this.id = id; return this; }
        public Builder product(Product product) { this.product = product; return this; }
        public Builder sku(String sku) { this.sku = sku; return this; }
        public Builder price(BigDecimal price) { this.price = price; return this; }
        public Builder compareAtPrice(BigDecimal compareAtPrice) { this.compareAtPrice = compareAtPrice; return this; }
        public Builder stock(Integer stock) { this.stock = stock; return this; }
        public Builder attributesJson(String attributesJson) { this.attributesJson = attributesJson; return this; }
        public Builder weight(BigDecimal weight) { this.weight = weight; return this; }
        public Builder length(BigDecimal length) { this.length = length; return this; }
        public Builder width(BigDecimal width) { this.width = width; return this; }
        public Builder height(BigDecimal height) { this.height = height; return this; }

        public ProductVariant build() {
            return new ProductVariant(id, product, sku, price, compareAtPrice, stock, attributesJson, weight, length, width, height);
        }
    }
}
