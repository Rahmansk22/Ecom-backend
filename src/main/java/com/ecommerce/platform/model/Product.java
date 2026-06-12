package com.ecommerce.platform.model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "products", indexes = {
        @Index(name = "idx_products_slug", columnList = "slug", unique = true),
        @Index(name = "idx_products_brand", columnList = "brand")
})
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, unique = true)
    private String slug;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private String brand;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id", nullable = true)
    private User seller;

    @Column(nullable = false)
    private String status = "ACTIVE"; // DRAFT, ACTIVE, INACTIVE

    @Column(name = "country_of_origin")
    private String countryOfOrigin;

    @Column(name = "hsn_code")
    private String hsnCode;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Instant updatedAt;

    public Product() {}

    public Product(UUID id, String title, String slug, String description, String brand, Category category, 
                   User seller, String status, String countryOfOrigin, String hsnCode, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.title = title;
        this.slug = slug;
        this.description = description;
        this.brand = brand;
        this.category = category;
        this.seller = seller;
        this.status = status != null ? status : "ACTIVE";
        this.countryOfOrigin = countryOfOrigin;
        this.hsnCode = hsnCode;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getSlug() { return slug; }
    public void setSlug(String slug) { this.slug = slug; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }

    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }

    public User getSeller() { return seller; }
    public void setSeller(User seller) { this.seller = seller; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getCountryOfOrigin() { return countryOfOrigin; }
    public void setCountryOfOrigin(String countryOfOrigin) { this.countryOfOrigin = countryOfOrigin; }

    public String getHsnCode() { return hsnCode; }
    public void setHsnCode(String hsnCode) { this.hsnCode = hsnCode; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private UUID id;
        private String title;
        private String slug;
        private String description;
        private String brand;
        private Category category;
        private User seller;
        private String status;
        private String countryOfOrigin;
        private String hsnCode;
        private Instant createdAt;
        private Instant updatedAt;

        public Builder id(UUID id) { this.id = id; return this; }
        public Builder title(String title) { this.title = title; return this; }
        public Builder slug(String slug) { this.slug = slug; return this; }
        public Builder description(String description) { this.description = description; return this; }
        public Builder brand(String brand) { this.brand = brand; return this; }
        public Builder category(Category category) { this.category = category; return this; }
        public Builder seller(User seller) { this.seller = seller; return this; }
        public Builder status(String status) { this.status = status; return this; }
        public Builder countryOfOrigin(String countryOfOrigin) { this.countryOfOrigin = countryOfOrigin; return this; }
        public Builder hsnCode(String hsnCode) { this.hsnCode = hsnCode; return this; }
        public Builder createdAt(Instant createdAt) { this.createdAt = createdAt; return this; }
        public Builder updatedAt(Instant updatedAt) { this.updatedAt = updatedAt; return this; }

        public Product build() {
            return new Product(id, title, slug, description, brand, category, seller, status, countryOfOrigin, hsnCode, createdAt, updatedAt);
        }
    }
}
