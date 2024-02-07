package com.pruebas.api.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "Pacientes")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class Paciente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long pacienteId;

    @NonNull
    private String nombre;

    @NonNull
    private Integer edad;

    @NonNull
    private String correo;
}
