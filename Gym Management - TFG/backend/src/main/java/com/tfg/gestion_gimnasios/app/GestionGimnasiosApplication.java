package com.tfg.gestion_gimnasios.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication(scanBasePackages={
		"com.tfg.gestion_gimnasios.rest",
		"com.tfg.gestion_gimnasios.servicios",
		"com.tfg.gestion_gimnasios.repositorios",
		"com.tfg.gestion_gimnasios.seguridad",

})
@EntityScan(basePackages ="com.tfg.gestion_gimnasios.entidades")
@ComponentScan(basePackages = {
		"com.tfg.gestion_gimnasios.configuracion",
		"com.tfg.gestion_gimnasios.rest",
		"com.tfg.gestion_gimnasios.servicios",
		"com.tfg.gestion_gimnasios.repositorios",
		"com.tfg.gestion_gimnasios.seguridad"
})
public class GestionGimnasiosApplication {

    public static void main(String[] args) {
        SpringApplication.run(GestionGimnasiosApplication.class, args);
    }
}
