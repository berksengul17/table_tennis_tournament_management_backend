package com.berk.table_tennis_tournament_management_backend.age_category;

import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/get-categories")
    public List<String> getCategories() {
        return ageCategoryService.getCategories();
    }

    @GetMapping("/get-age-list")
    public List<String> getAgeListByCategory(@RequestParam int category) {
        return ageCategoryService.getAgeListByCategory(category);
    }
}
