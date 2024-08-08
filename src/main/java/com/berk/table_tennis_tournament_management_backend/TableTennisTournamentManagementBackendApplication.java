package com.berk.table_tennis_tournament_management_backend;

import com.berk.table_tennis_tournament_management_backend.log.LogFilter;
import com.berk.table_tennis_tournament_management_backend.log.LogRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class TableTennisTournamentManagementBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(TableTennisTournamentManagementBackendApplication.class, args);
	}

	@Bean
	public FilterRegistrationBean<LogFilter> loggingFilter(LogRepository logRepository) {
		FilterRegistrationBean<LogFilter> registrationBean = new FilterRegistrationBean<>();
		registrationBean.setFilter(new LogFilter(logRepository));
		registrationBean.addUrlPatterns("/*");
		return registrationBean;
	}
}
