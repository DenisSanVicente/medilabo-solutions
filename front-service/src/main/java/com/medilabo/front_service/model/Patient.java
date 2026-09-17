package com.medilabo.front_service.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class Patient {

    private Long id;

    @NotBlank(message = "Le nom est obligatoire")
    private String lastName;

    @NotBlank(message = "Le prénom est oblgatoire")
    private String firstName;

    @NotNull(message = "La date de naissance est obligatoire")
    private LocalDate birth;

    @NotBlank(message = "Le genre est obligatoire")
    private String gender;

    private String address;
    private String phone;
}