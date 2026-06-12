package com.ecommerce.platform.service;

import com.ecommerce.platform.dto.CategoryResponse;
import java.util.List;

public interface CategoryService {
    List<CategoryResponse> getCategoryTree();
}
