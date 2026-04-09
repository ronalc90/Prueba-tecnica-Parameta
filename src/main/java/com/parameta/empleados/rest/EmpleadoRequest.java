package com.parameta.empleados.rest;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

/** Parámetros del GET. Validados con Bean Validation. */
public class EmpleadoRequest {

    @NotBlank(message = "nombres es obligatorio")
    private String nombres;

    @NotBlank(message = "apellidos es obligatorio")
    private String apellidos;

    @NotBlank(message = "tipoDocumento es obligatorio")
    private String tipoDocumento;

    @NotBlank(message = "numeroDocumento es obligatorio")
    private String numeroDocumento;

    @NotNull(message = "fechaNacimiento es obligatoria")
    @Past(message = "fechaNacimiento debe ser anterior a hoy")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate fechaNacimiento;

    @NotNull(message = "fechaVinculacion es obligatoria")
    @PastOrPresent(message = "fechaVinculacion no puede ser futura")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate fechaVinculacion;

    @NotBlank(message = "cargo es obligatorio")
    private String cargo;

    @NotNull(message = "salario es obligatorio")
    @Positive(message = "salario debe ser mayor a cero")
    private Double salario;

    public String getNombres() { return nombres; }
    public void setNombres(String nombres) { this.nombres = nombres; }

    public String getApellidos() { return apellidos; }
    public void setApellidos(String apellidos) { this.apellidos = apellidos; }

    public String getTipoDocumento() { return tipoDocumento; }
    public void setTipoDocumento(String tipoDocumento) { this.tipoDocumento = tipoDocumento; }

    public String getNumeroDocumento() { return numeroDocumento; }
    public void setNumeroDocumento(String numeroDocumento) { this.numeroDocumento = numeroDocumento; }

    public LocalDate getFechaNacimiento() { return fechaNacimiento; }
    public void setFechaNacimiento(LocalDate fechaNacimiento) { this.fechaNacimiento = fechaNacimiento; }

    public LocalDate getFechaVinculacion() { return fechaVinculacion; }
    public void setFechaVinculacion(LocalDate fechaVinculacion) { this.fechaVinculacion = fechaVinculacion; }

    public String getCargo() { return cargo; }
    public void setCargo(String cargo) { this.cargo = cargo; }

    public Double getSalario() { return salario; }
    public void setSalario(Double salario) { this.salario = salario; }
}
