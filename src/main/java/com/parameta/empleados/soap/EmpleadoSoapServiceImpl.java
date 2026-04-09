package com.parameta.empleados.soap;

import com.parameta.empleados.domain.Empleado;
import com.parameta.empleados.domain.EmpleadoMapper;
import com.parameta.empleados.domain.EmpleadoRepository;
import jakarta.jws.WebService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@WebService(
        serviceName = "EmpleadoService",
        portName = "EmpleadoPort",
        targetNamespace = "http://parameta.com/empleados",
        endpointInterface = "com.parameta.empleados.soap.EmpleadoSoapService"
)
public class EmpleadoSoapServiceImpl implements EmpleadoSoapService {

    private static final Logger log = LoggerFactory.getLogger(EmpleadoSoapServiceImpl.class);

    private final EmpleadoRepository repository;

    public EmpleadoSoapServiceImpl(EmpleadoRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional
    public void guardar(Empleado empleado) {
        log.info("[SOAP] Guardando empleado {} {}", empleado.getNombres(), empleado.getApellidos());
        repository.save(EmpleadoMapper.toEntity(empleado));
    }
}
