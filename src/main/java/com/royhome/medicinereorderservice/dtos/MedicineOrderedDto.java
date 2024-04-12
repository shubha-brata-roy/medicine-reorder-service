package com.royhome.medicinereorderservice.dtos;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonDeserialize
public class MedicineOrderedDto {
    private String medicineName;
    private Integer quantityPerUnit;
    private Integer unitsOrdered;
}
