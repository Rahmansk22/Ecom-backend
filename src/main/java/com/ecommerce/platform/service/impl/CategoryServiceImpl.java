package com.ecommerce.platform.service.impl;

import com.ecommerce.platform.dto.CategoryResponse;
import com.ecommerce.platform.model.Category;
import com.ecommerce.platform.repository.CategoryRepository;
import com.ecommerce.platform.service.CategoryService;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    @Cacheable(value = "categories")
    public List<CategoryResponse> getCategoryTree() {
        List<Category> roots = categoryRepository.findRootCategories();
        return roots.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private CategoryResponse mapToResponse(Category category) {
        List<CategoryResponse> childrenDto = category.getChildren().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());

        return new CategoryResponse(
                category.getId(),
                category.getName(),
                category.getSlug(),
                category.getBannerUrl(),
                category.getIconUrl(),
                childrenDto
        );
    }
}
