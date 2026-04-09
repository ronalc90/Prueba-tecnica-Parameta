package com.parameta.empleados.domain;

/** Conversiones DTO ↔ entidad. Mantiene aisladas las dos representaciones. */
public final class EmpleadoMapper {

    private EmpleadoMapper() {}

    public static EmpleadoEntity toEntity(Empleado dto) {
        EmpleadoEntity e = new EmpleadoEntity();
        e.setNombres(dto.getNombres());
        e.setApellidos(dto.getApellidos());
        e.setTipoDocumento(dto.getTipoDocumento());
        e.setNumeroDocumento(dto.getNumeroDocumento());
        e.setFechaNacimiento(dto.getFechaNacimiento());
        e.setFechaVinculacion(dto.getFechaVinculacion());
        e.setCargo(dto.getCargo());
        e.setSalario(dto.getSalario());
        return e;
    }
}
