package com.ecommerce.platform.model;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "product_images")
public class ProductImage {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "variant_id", nullable = false)
    private ProductVariant variant;

    @Column(name = "image_url", nullable = false)
    private String imageUrl;

    @Column(name = "display_order")
    private Integer displayOrder = 0;

    public ProductImage() {}

    public ProductImage(UUID id, ProductVariant variant, String imageUrl, Integer displayOrder) {
        this.id = id;
        this.variant = variant;
        this.imageUrl = imageUrl;
        this.displayOrder = displayOrder != null ? displayOrder : 0;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public ProductVariant getVariant() { return variant; }
    public void setVariant(ProductVariant variant) { this.variant = variant; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public Integer getDisplayOrder() { return displayOrder; }
    public void setDisplayOrder(Integer displayOrder) { this.displayOrder = displayOrder; }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private UUID id;
        private ProductVariant variant;
        private String imageUrl;
        private Integer displayOrder;

        public Builder id(UUID id) { this.id = id; return this; }
        public Builder variant(ProductVariant variant) { this.variant = variant; return this; }
        public Builder imageUrl(String imageUrl) { this.imageUrl = imageUrl; return this; }
        public Builder displayOrder(Integer displayOrder) { this.displayOrder = displayOrder; return this; }

        public ProductImage build() {
            return new ProductImage(id, variant, imageUrl, displayOrder);
        }
    }
}
