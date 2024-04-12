package com.royhome.medicinereorderservice.dtos;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonDeserialize
public class MedicineRequestDto {
    private int serialNumber;
    private String medicineName;
    private Integer quantityPerUnit;
    private Integer reOrderQuantity;
    private Integer reOrderUnits;
}
