package com.pruebas.api.controller;


import com.pruebas.api.entity.Paciente;
import com.pruebas.api.exceptions.InvalidRequestException;
import com.pruebas.api.exceptions.NotFoundException;
import com.pruebas.api.service.PacienteService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/pacientes")
public class PacienteController {

    @Autowired
    private PacienteService pacienteService;

    @GetMapping
    public List<Paciente> listarPacientes(){
        return pacienteService.getAllPacientes();
    }

    @GetMapping("/{pacienteId}")
    public Paciente listarPacientePorId(@PathVariable Long pacienteId){
        return pacienteService.getPacienteById(pacienteId).get();
    }

    @PostMapping
    public Paciente guardarPaciente(@RequestBody @Valid Paciente paciente){
        return pacienteService.createPaciente(paciente);
    }

    @PutMapping
    public Paciente actualizarPaciente(@RequestBody Paciente paciente) throws InvalidRequestException {
        if (paciente == null || paciente.getPacienteId() == null){
            throw new InvalidRequestException("Los datos del paciente no pueden ser nulos");
        }//verifica si los datos del pacientes son nulos

        Optional<Paciente> pacienteOptional = pacienteService.getPacienteById(paciente.getPacienteId());
        if (pacienteOptional.isEmpty()){
            throw new NotFoundException("Paciente con el ID: " + paciente.getPacienteId() + " No Existe!");
        }//Si el paciente existe

        Paciente pacienteExistente = pacienteOptional.get();
        pacienteExistente.setNombre(paciente.getNombre());
        pacienteExistente.setEdad(paciente.getEdad());
        pacienteExistente.setCorreo(paciente.getCorreo());

        return pacienteService.updatePaciente(pacienteExistente);
    }

    @DeleteMapping("/{pacienteId}")
    public void eliminarPaciente(@PathVariable Long pacienteId){
        if (pacienteService.getPacienteById(pacienteId).isEmpty()){
            throw new NotFoundException("Paciente con el ID: " + pacienteId + " No Existe!");
        }
        pacienteService.deletePaciente(pacienteId);
    }
}
