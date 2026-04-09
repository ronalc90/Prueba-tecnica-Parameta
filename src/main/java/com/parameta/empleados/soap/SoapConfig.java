package com.parameta.empleados.soap;

import jakarta.xml.ws.Endpoint;
import org.apache.cxf.Bus;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.jaxws.EndpointImpl;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.apache.cxf.transport.http.HTTPConduit;
import org.apache.cxf.transports.http.configuration.HTTPClientPolicy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class SoapConfig {

    /** Publica el endpoint SOAP en /services/empleados (WSDL en ?wsdl). */
    @Bean
    public Endpoint empleadoEndpoint(Bus bus, EmpleadoSoapServiceImpl impl) {
        EndpointImpl endpoint = new EndpointImpl(bus, impl);
        endpoint.publish("/empleados");
        return endpoint;
    }

    /**
     * Cliente JAX-WS que el controlador REST usa para invocar el SOAP.
     * Aunque el endpoint vive en la misma JVM, la llamada viaja por HTTP/SOAP real.
     */
    @Bean
    @Primary
    public EmpleadoSoapService empleadoSoapClient(@Value("${soap.empleado.address}") String address) {
        JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean();
        factory.setServiceClass(EmpleadoSoapService.class);
        factory.setAddress(address);
        EmpleadoSoapService client = (EmpleadoSoapService) factory.create();

        HTTPConduit conduit = (HTTPConduit) ClientProxy.getClient(client).getConduit();
        HTTPClientPolicy policy = new HTTPClientPolicy();
        policy.setConnectionTimeout(5000);
        policy.setReceiveTimeout(10000);
        conduit.setClient(policy);

        return client;
    }
}
