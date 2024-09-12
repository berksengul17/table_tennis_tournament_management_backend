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

//    @PostMapping("/create-categories")
//    public List<AgeCategory> createAgeCategories() {
//        return ageCategoryService.createAgeCategories();
//    }

    @GetMapping("/load-categories")
    public List<AgeCategory> loadAgeCategories() {
        return ageCategoryService.loadAgeCategories();
    }

    @GetMapping("/get-categories")
    public List<String> getCategories(@RequestParam boolean showDoubles) {
        return ageCategoryService.getCategories(showDoubles);
    }

    @GetMapping("/get-age-category/{category}/{age}")
    public String getAgeCategoryString(@PathVariable int category, @PathVariable int age) {
        return ageCategoryService.getAgeCategoryString(category, age);
    }

    @GetMapping("/get-age-list")
    public List<String> getAgeListByCategoryAndGender(@RequestParam(defaultValue = "-1") int category,
                                                      @RequestParam(defaultValue = "-1") int gender) {
        return ageCategoryService.getAgeListByCategoryAndGender(category, gender);
    }

    @GetMapping("/is-double")
    public boolean isDouble(@RequestParam String category) {
        return ageCategoryService.isDouble(category);
    }
}
