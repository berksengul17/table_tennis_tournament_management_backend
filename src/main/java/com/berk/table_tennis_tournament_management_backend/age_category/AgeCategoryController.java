package com.berk.table_tennis_tournament_management_backend.age_category;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/age-category")
public class AgeCategoryController {

    private final AgeCategoryService ageCategoryService;

    public AgeCategoryController(AgeCategoryService ageCategoryService) {
        this.ageCategoryService = ageCategoryService;
    }

    @PostMapping("/create-categories")
    public List<AgeCategory> createAgeCategories() {
        return ageCategoryService.createAgeCategories();
    }

    @GetMapping("/load-categories")
    public List<AgeCategory> loadAgeCategories() {
        return ageCategoryService.loadAgeCategories();
    }
}
