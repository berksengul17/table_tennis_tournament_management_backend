package com.berk.table_tennis_tournament_management_backend;

import com.berk.table_tennis_tournament_management_backend.age_category.AGE;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class TableTennisTournamentManagementBackendApplication {

	public static void main(String[] args) {
		System.out.println(AGE.getByAge("30-39"));
		SpringApplication.run(TableTennisTournamentManagementBackendApplication.class, args);
	}

}
