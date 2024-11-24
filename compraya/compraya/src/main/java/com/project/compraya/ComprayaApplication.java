package com.project.compraya;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories("com.project.compraya.repositories")  // Asegura el paquete correcto
@EntityScan("com.project.compraya.entities")    
public class ComprayaApplication {

	public static void main(String[] args) {
		SpringApplication.run(ComprayaApplication.class, args);
	}

}
