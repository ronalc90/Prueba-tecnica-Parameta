package com.parameta.empleados;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.time.Clock;

@SpringBootApplication
public class EmpleadosApplication {

    public static void main(String[] args) {
        SpringApplication.run(EmpleadosApplication.class, args);
    }

    /** Inyectado en los servicios para poder fijar el tiempo en pruebas. */
    @Bean
    public Clock clock() {
        return Clock.systemDefaultZone();
    }
}
