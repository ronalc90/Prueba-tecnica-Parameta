package com.parameta.empleados.rest;

import com.parameta.empleados.service.EmpleadoService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/empleados")
public class EmpleadoController {

    private final EmpleadoService service;

    public EmpleadoController(EmpleadoService service) {
        this.service = service;
    }

    @GetMapping
    public EmpleadoResponse registrar(@Valid @ModelAttribute EmpleadoRequest req) {
        return service.registrar(req);
    }

    @GetMapping("/listar")
    public List<EmpleadoResponse> listar() {
        return service.listar();
    }
}
