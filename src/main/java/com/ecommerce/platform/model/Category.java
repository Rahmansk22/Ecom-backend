package com.ecommerce.platform.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "categories", indexes = {
        @Index(name = "idx_categories_slug", columnList = "slug", unique = true)
})
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String slug;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    @JsonIgnore
    private Category parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Category> children = new ArrayList<>();

    @Column(name = "banner_url")
    private String bannerUrl;

    @Column(name = "icon_url")
    private String iconUrl;

    public Category() {}

    public Category(UUID id, String name, String slug, Category parent, List<Category> children, String bannerUrl, String iconUrl) {
        this.id = id;
        this.name = name;
        this.slug = slug;
        this.parent = parent;
        this.children = children != null ? children : new ArrayList<>();
        this.bannerUrl = bannerUrl;
        this.iconUrl = iconUrl;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getSlug() { return slug; }
    public void setSlug(String slug) { this.slug = slug; }

    public Category getParent() { return parent; }
    public void setParent(Category parent) { this.parent = parent; }

    public List<Category> getChildren() { return children; }
    public void setChildren(List<Category> children) { this.children = children; }

    public String getBannerUrl() { return bannerUrl; }
    public void setBannerUrl(String bannerUrl) { this.bannerUrl = bannerUrl; }

    public String getIconUrl() { return iconUrl; }
    public void setIconUrl(String iconUrl) { this.iconUrl = iconUrl; }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private UUID id;
        private String name;
        private String slug;
        private Category parent;
        private List<Category> children;
        private String bannerUrl;
        private String iconUrl;

        public Builder id(UUID id) { this.id = id; return this; }
        public Builder name(String name) { this.name = name; return this; }
        public Builder slug(String slug) { this.slug = slug; return this; }
        public Builder parent(Category parent) { this.parent = parent; return this; }
        public Builder children(List<Category> children) { this.children = children; return this; }
        public Builder bannerUrl(String bannerUrl) { this.bannerUrl = bannerUrl; return this; }
        public Builder iconUrl(String iconUrl) { this.iconUrl = iconUrl; return this; }

        public Category build() {
            return new Category(id, name, slug, parent, children, bannerUrl, iconUrl);
        }
    }
}
