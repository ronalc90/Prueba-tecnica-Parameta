package com.parameta.empleados.rest;

import com.parameta.empleados.soap.EmpleadoSoapService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:test;MODE=MySQL;DB_CLOSE_DELAY=-1",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
class EmpleadoControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean(name = "empleadoSoapClient")
    private EmpleadoSoapService soapClient;

    @Test
    void registrarEmpleadoValido() throws Exception {
        mvc.perform(get("/api/empleados")
                        .param("nombres", "Ronald")
                        .param("apellidos", "Perez")
                        .param("tipoDocumento", "CC")
                        .param("numeroDocumento", "1234567")
                        .param("fechaNacimiento", "1990-05-10")
                        .param("fechaVinculacion", "2020-01-15")
                        .param("cargo", "Backend Developer")
                        .param("salario", "5000000"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombres").value("Ronald"))
                .andExpect(jsonPath("$.edad.anios").exists())
                .andExpect(jsonPath("$.tiempoVinculacion.anios").exists());

        verify(soapClient, times(1)).guardar(org.mockito.ArgumentMatchers.any());
    }

    @Test
    void rechazarMenorDeEdad() throws Exception {
        LocalDate hace10 = LocalDate.now().minusYears(10);
        mvc.perform(get("/api/empleados")
                        .param("nombres", "Mini")
                        .param("apellidos", "Mouse")
                        .param("tipoDocumento", "TI")
                        .param("numeroDocumento", "999")
                        .param("fechaNacimiento", hace10.toString())
                        .param("fechaVinculacion", "2024-01-01")
                        .param("cargo", "Becario")
                        .param("salario", "1000000"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensaje").value("El empleado debe ser mayor de edad"));
    }

    @Test
    void rechazarCamposVacios() throws Exception {
        mvc.perform(get("/api/empleados")
                        .param("nombres", "")
                        .param("apellidos", "")
                        .param("tipoDocumento", "CC")
                        .param("numeroDocumento", "1")
                        .param("fechaNacimiento", "1990-01-01")
                        .param("fechaVinculacion", "2020-01-01")
                        .param("cargo", "x")
                        .param("salario", "1"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void rechazarFormatoFechaInvalido() throws Exception {
        mvc.perform(get("/api/empleados")
                        .param("nombres", "Ronald")
                        .param("apellidos", "Perez")
                        .param("tipoDocumento", "CC")
                        .param("numeroDocumento", "1")
                        .param("fechaNacimiento", "10-05-1990")
                        .param("fechaVinculacion", "2020-01-01")
                        .param("cargo", "Dev")
                        .param("salario", "1000"))
                .andExpect(status().isBadRequest());
    }
}
