package com.diagnocons.microservicio_Imagenes;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.diagnocons.microservicio_Imagenes")
public class MicroservicioImagenesApplication {

	public static void main(String[] args) {
		SpringApplication.run(MicroservicioImagenesApplication.class, args);
	}
}
