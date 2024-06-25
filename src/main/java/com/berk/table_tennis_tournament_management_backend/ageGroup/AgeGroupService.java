package com.berk.table_tennis_tournament_management_backend.ageGroup;

import org.springframework.stereotype.Service;

@Service
public class AgeGroupService {

    private final AgeGroupRepository ageGroupRepository;

    public AgeGroupService(AgeGroupRepository ageGroupRepository) {
        this.ageGroupRepository = ageGroupRepository;
    }

}
