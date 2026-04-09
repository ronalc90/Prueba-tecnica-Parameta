package com.parameta.empleados.rest;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;

public record EmpleadoResponse(
        String nombres,
        String apellidos,
        String tipoDocumento,
        String numeroDocumento,

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
        LocalDate fechaNacimiento,

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
        LocalDate fechaVinculacion,

        String cargo,
        Double salario,
        Periodo edad,
        Periodo tiempoVinculacion
) {
    public record Periodo(int anios, int meses, int dias) {}
}
