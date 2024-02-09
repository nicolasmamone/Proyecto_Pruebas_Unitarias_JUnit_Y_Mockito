package com.pruebas.api.controller;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pruebas.api.entity.Paciente;
import com.pruebas.api.exceptions.InvalidRequestException;
import com.pruebas.api.exceptions.NotFoundException;
import com.pruebas.api.service.PacienteService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@WebMvcTest(PacienteController.class)
public class PacienteControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    PacienteService pacienteService;

    //Datos de prueba
    Paciente PACIENTE_001 = new Paciente(1L, "Pepe Argento", 23, "pepe@gmail.com"  );
    Paciente PACIENTE_002 = new Paciente(2L, "Juan Castro", 44, "juan@gmail.com"  );
    Paciente PACIENTE_003 = new Paciente(3L, "Felipe Melo", 18, "felipe@gmail.com"  );

    @Test
    @DisplayName("Listado de pacientes") // Anotacion que sirve para titular el metodo cuando lo ejecuto
    void testListarPacientes() throws Exception {

        List<Paciente> pacientes = new ArrayList<>(Arrays.asList(PACIENTE_001, PACIENTE_002, PACIENTE_003));
        Mockito.when(pacienteService.getAllPacientes()).thenReturn(pacientes); // Simulo el service con los datos de prueba

        mockMvc.perform(MockMvcRequestBuilders
                .get("/api/pacientes")  // Cuando yo ejecute una peticion a la siguiente api...
                .contentType(MediaType.APPLICATION_JSON)) // El contenido que me va a retornar es de tipo...
                .andExpect(
                        status().isOk() // que el httpStatus sea .OK
                ) // y va a esperar los siguientes valores
                .andExpect(
                        jsonPath("$", hasSize(3)) // que el tamaño sea de 3
                )
                .andExpect(
                        jsonPath("$[2].nombre", is("Felipe Melo"))
                );
    }

    @Test
    @DisplayName("Listando un solo paciente")
    void testListarPacientesPorId() throws Exception {
        Mockito.when(pacienteService.getPacienteById(PACIENTE_001.getPacienteId()))
                .thenReturn(Optional.of(PACIENTE_001));

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/pacientes")  // Cuando yo ejecute una peticion a la siguiente api...
                        .contentType(MediaType.APPLICATION_JSON)) // El contenido que me va a retornar es de tipo...
                .andExpect(
                        status().isOk() // que el httpStatus sea .OK
                ) // y va a esperar los siguientes valores
                .andExpect(
                        jsonPath("$", notNullValue()) // que el valor no sea nulo
                )
                .andExpect(
                        jsonPath("$.nombre", is("Pepe Argento")) // Que el nombre del paciente sea igual al valor dado "Pepe Argento"
                );


    }

    @Test
    @DisplayName("Guardando un paciente")
    void testGuardarPaciente() throws Exception {
        // paciente que voy a guardar
        Paciente paciente = Paciente.builder()
                .pacienteId(4L)
                .nombre("Pedro Lopez")
                .edad(56)
                .correo("pedro@gmail.com")
                .build();
        // Cuando yo llame al metodo createPaciente...
        Mockito.when(pacienteService.createPaciente(paciente)).thenReturn(paciente);

        //genero la peticion
        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.post("/api/pacientes")
                .contentType(MediaType.APPLICATION_JSON) //indico que esta request me va a devolver un json
                .accept(MediaType.APPLICATION_JSON) // indico que esta request acepta json
                .content(objectMapper.writeValueAsString(paciente)); // transformo el paciente q voy a guardar en json

        mockMvc.perform(mockRequest) // cuando ejecuto la peticion
                .andExpect(status().isOk())
                .andExpect(jsonPath("$",notNullValue())) // notiene que ser nulo
                .andExpect(jsonPath("$.nombre",is("Pedro Lopez")));


    }

    @Test
    @DisplayName("Actualizando un paciente")
    void testActualizarPacienteConExito() throws Exception {
        // paciente que voy a actualizar
        Paciente pacienteUpdate = Paciente.builder()
                .pacienteId(1L)
                .nombre("Pedro Argento")
                .edad(48)
                .correo("pepe@gmail.com")
                .build();
        // Cuando yo llame primero al metodo getPacienteid entonces...
        Mockito.when(pacienteService.getPacienteById(PACIENTE_001.getPacienteId())).thenReturn(Optional.of(PACIENTE_001));
        // y Cuando llame al updateService entonces ....
        Mockito.when(pacienteService.updatePaciente(pacienteUpdate)).thenReturn(pacienteUpdate);

        //genero la peticion
        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.put("/api/pacientes")
                .contentType(MediaType.APPLICATION_JSON) //indico que esta request me va a devolver un json
                .accept(MediaType.APPLICATION_JSON) // indico que esta request acepta json
                .content(objectMapper.writeValueAsString(pacienteUpdate)); // transformo el paciente q voy a guardar en json

        mockMvc.perform(mockRequest) // cuando ejecuto la peticion
                .andExpect(status().isOk())
                .andExpect(jsonPath("$",notNullValue())) // notiene que ser nulo
                .andExpect(jsonPath("$.nombre",is("Pedro Argento")));


    }

    @Test
    @DisplayName("Actualizando un paciente que no existe")
    void testActualizarPacienteNoEncontrado() throws Exception {
        // paciente que voy a actualizar
        Paciente pacienteUpdate = Paciente.builder()
                .pacienteId(8L) // Id que no existe
                .nombre("Pedro Argento")
                .edad(48)
                .correo("pepe@gmail.com")
                .build();


        //genero la peticion
        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.put("/api/pacientes")
                .contentType(MediaType.APPLICATION_JSON) //indico que esta request me va a devolver un json
                .accept(MediaType.APPLICATION_JSON) // indico que esta request acepta json
                .content(objectMapper.writeValueAsString(pacienteUpdate)); // transformo el paciente q voy a guardar en json

        mockMvc.perform(mockRequest) // cuando ejecuto la peticion
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue( //Si el error es instance de NotFoundException entonces devuelve true
                        result.getResolvedException() instanceof NotFoundException
                ))
                .andExpect(result -> assertEquals(
                        "Paciente con el ID: " + pacienteUpdate.getPacienteId() + " No Existe!", result.getResolvedException().getMessage()));
    }
    @Test
    @DisplayName("Actualizando un paciente que no existe")
    void testActualizarPacienteConIdNulo() throws Exception {
        // paciente que voy a actualizar
        Paciente pacienteUpdate = Paciente.builder()
                .nombre("Pedro Argento")
                .edad(48)
                .correo("pepe@gmail.com")
                .build();


        //genero la peticion
        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.put("/api/pacientes")
                .contentType(MediaType.APPLICATION_JSON) //indico que esta request me va a devolver un json
                .accept(MediaType.APPLICATION_JSON) // indico que esta request acepta json
                .content(objectMapper.writeValueAsString(pacienteUpdate)); // transformo el paciente q voy a guardar en json

        mockMvc.perform(mockRequest) // cuando ejecuto la peticion
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue( //Si el error es instance de NotFoundException entonces devuelve true
                        result.getResolvedException() instanceof InvalidRequestException
                ))
                .andExpect(result -> assertEquals(
                        "Los datos del paciente no pueden ser nulos", result.getResolvedException().getMessage()));
    }

    @Test
    @DisplayName("Eliminando un paciente")
    void testEliminarPacienteConExito() throws Exception {
        Mockito.when(pacienteService.getPacienteById(PACIENTE_002.getPacienteId())).thenReturn(Optional.of(PACIENTE_002));

        mockMvc.perform(MockMvcRequestBuilders
                .delete("/api/pacientes/2")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

    }

    @Test
    @DisplayName("Eliminando un paciente que no existe")
    void testEliminarPacienteNoEncontrado() throws Exception {
        Mockito.when(pacienteService.getPacienteById(10L)).thenReturn(Optional.of(PACIENTE_002));

        mockMvc.perform(MockMvcRequestBuilders
                .delete("/api/pacientes/2")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(result ->
                        assertTrue(result.getResolvedException() instanceof NotFoundException));

    }
}
