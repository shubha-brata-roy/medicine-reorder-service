package com.royhome.medicinereorderservice.models;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Entity
@Getter
@Setter
@JsonDeserialize
public class MedicineInventory {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String medicineName;
    private Integer quantityPerUnit;

    @Setter(AccessLevel.NONE)
    private Double consumptionPerDay;

    @Setter(AccessLevel.NONE)
    private Double consumptionPerMonth;

    @Setter(AccessLevel.NONE)
    private Double quantityAvailableToday;

    @Setter(AccessLevel.NONE)
    private Double daysAvailableFor;

    private Date dateLastUpdated;

    @Setter(AccessLevel.NONE)
    private Date endingByDate;

    public void setConsumptionPerDay(Double consumptionPerDay) {
        this.consumptionPerDay = consumptionPerDay;
        this.consumptionPerMonth = consumptionPerDay * 30;
    }

    public void setConsumptionPerMonth(Double consumptionPerMonth) {
        this.consumptionPerMonth = consumptionPerMonth;
        this.consumptionPerDay = consumptionPerMonth / 30;
    }

    public void setQuantityAvailableToday(Double quantityAvailableToday) {
        this.quantityAvailableToday = quantityAvailableToday;
        this.daysAvailableFor = quantityAvailableToday / this.consumptionPerDay;
        this.endingByDate = new Date(new Date().getTime() + (long) (this.daysAvailableFor * 24 * 60 * 60 * 1000));
    }
}
