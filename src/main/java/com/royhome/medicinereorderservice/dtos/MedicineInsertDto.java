package com.royhome.medicinereorderservice.dtos;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@JsonDeserialize
public class MedicineInsertDto {
    private Long id;
    private String medicineName;
    private Integer quantityPerUnit;
    private Double consumptionPerDay;
    private Double consumptionPerMonth;
    private Double quantityAvailableToday;
}
