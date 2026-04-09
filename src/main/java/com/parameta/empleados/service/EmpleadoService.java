package com.parameta.empleados.service;

import com.parameta.empleados.domain.Empleado;
import com.parameta.empleados.rest.EmpleadoRequest;
import com.parameta.empleados.rest.EmpleadoResponse;
import com.parameta.empleados.soap.EmpleadoSoapService;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.LocalDate;
import java.time.Period;

/**
 * Orquesta la regla de negocio: valida la edad mínima, delega la persistencia
 * al servicio SOAP y construye la respuesta enriquecida con edad y tiempo de
 * vinculación.
 */
@Service
public class EmpleadoService {

    private static final int EDAD_MINIMA = 18;

    private final EmpleadoSoapService soapClient;
    private final Clock clock;

    public EmpleadoService(EmpleadoSoapService soapClient, Clock clock) {
        this.soapClient = soapClient;
        this.clock = clock;
    }

    public EmpleadoResponse registrar(EmpleadoRequest req) {
        LocalDate hoy = LocalDate.now(clock);

        Period edad = Period.between(req.getFechaNacimiento(), hoy);
        if (edad.getYears() < EDAD_MINIMA) {
            throw new IllegalArgumentException("El empleado debe ser mayor de edad");
        }
        if (req.getFechaVinculacion().isBefore(req.getFechaNacimiento())) {
            throw new IllegalArgumentException("fechaVinculacion no puede ser anterior a fechaNacimiento");
        }

        Empleado empleado = toDto(req);
        soapClient.guardar(empleado);

        Period vinculacion = Period.between(req.getFechaVinculacion(), hoy);
        return toResponse(empleado, edad, vinculacion);
    }

    private Empleado toDto(EmpleadoRequest r) {
        Empleado e = new Empleado();
        e.setNombres(r.getNombres());
        e.setApellidos(r.getApellidos());
        e.setTipoDocumento(r.getTipoDocumento());
        e.setNumeroDocumento(r.getNumeroDocumento());
        e.setFechaNacimiento(r.getFechaNacimiento());
        e.setFechaVinculacion(r.getFechaVinculacion());
        e.setCargo(r.getCargo());
        e.setSalario(r.getSalario());
        return e;
    }

    private EmpleadoResponse toResponse(Empleado e, Period edad, Period vinculacion) {
        return new EmpleadoResponse(
                e.getNombres(),
                e.getApellidos(),
                e.getTipoDocumento(),
                e.getNumeroDocumento(),
                e.getFechaNacimiento(),
                e.getFechaVinculacion(),
                e.getCargo(),
                e.getSalario(),
                new EmpleadoResponse.Periodo(edad.getYears(), edad.getMonths(), edad.getDays()),
                new EmpleadoResponse.Periodo(vinculacion.getYears(), vinculacion.getMonths(), vinculacion.getDays())
        );
    }
}
