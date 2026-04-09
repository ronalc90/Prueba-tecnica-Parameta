package com.parameta.empleados.soap;

import com.parameta.empleados.domain.Empleado;
import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;
import jakarta.jws.WebService;

/** Contrato SOAP (SEI) compartido entre la implementación y el cliente JAX-WS. */
@WebService(targetNamespace = "http://parameta.com/empleados")
public interface EmpleadoSoapService {

    @WebMethod
    void guardar(@WebParam(name = "empleado") Empleado empleado);
}
