package com.royhome.medicinereorderservice.dtos;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@JsonDeserialize
public class DateDto {
    private Date reOrderDate;
}
