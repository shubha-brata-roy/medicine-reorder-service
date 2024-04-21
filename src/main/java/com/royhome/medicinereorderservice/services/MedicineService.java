package com.royhome.medicinereorderservice.services;

import com.royhome.medicinereorderservice.dtos.MedicineInsertDto;
import com.royhome.medicinereorderservice.dtos.MedicineOrderedDto;
import com.royhome.medicinereorderservice.dtos.MedicineRequestDto;
import com.royhome.medicinereorderservice.models.MedicineInventory;
import com.royhome.medicinereorderservice.repositories.MedicineRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class MedicineService {
    private final MedicineRepository medicineRepository;

    public MedicineService(MedicineRepository medicineRepository) {
        this.medicineRepository = medicineRepository;
    }

    public List<MedicineInventory> insertMedicineDetails(List<MedicineInsertDto> records) {

        List<MedicineInventory> returnList = new ArrayList<>();

        for (MedicineInsertDto record : records) {
            MedicineInventory medicineInventory = new MedicineInventory();

            medicineInventory.setMedicineName(record.getMedicineName());
            medicineInventory.setQuantityPerUnit(record.getQuantityPerUnit());

            if(record.getConsumptionPerDay() != null) {
                medicineInventory.setConsumptionPerDay(record.getConsumptionPerDay());
            }

            if(record.getConsumptionPerMonth() != null) {
                medicineInventory.setConsumptionPerMonth(record.getConsumptionPerMonth());
            }

            medicineInventory.setQuantityAvailableToday(record.getQuantityAvailableToday());
            medicineInventory.setDateLastUpdated(new Date());

            medicineInventory = medicineRepository.save(medicineInventory);


            returnList.add(medicineInventory);
        }

        return returnList;
    }

    public void refreshMedicineAvailability() {
        List<MedicineInventory> medicineInventories = medicineRepository.findAll();

        for (MedicineInventory medicineInventory : medicineInventories) {
            Date today = new Date();
            long diff = today.getTime() - medicineInventory.getDateLastUpdated().getTime();
            long diffDays = diff / (24 * 60 * 60 * 1000);

            double newAvailability = medicineInventory.getQuantityAvailableToday() -
                                    (medicineInventory.getConsumptionPerDay() * diffDays);

            medicineInventory.setQuantityAvailableToday(newAvailability);

            medicineInventory.setQuantityAvailableToday(medicineInventory.getQuantityAvailableToday());
            medicineInventory.setDateLastUpdated(today);
            medicineRepository.save(medicineInventory);
        }
    }

    public List<MedicineRequestDto> generateMedicineReorderList(Date reorderDate) {
        List<MedicineInventory> records = medicineRepository.findAll();
        List<MedicineRequestDto> returnList = new ArrayList<>();
        long diff = reorderDate.getTime() - new Date().getTime();
        long daysToOrderFor = diff / (24 * 60 * 60 * 1000);
        int serialNumber = 1;

        for(MedicineInventory record : records) {
            double quantityToOrder = (record.getConsumptionPerDay() * daysToOrderFor)
                                        - record.getQuantityAvailableToday();

            if(quantityToOrder > 0) {
                double unitsToOrder = quantityToOrder / record.getQuantityPerUnit();
                int unitsToOrderInt = (int) Math.ceil(unitsToOrder);
                int quantityToOrderInt = unitsToOrderInt * record.getQuantityPerUnit();

                MedicineRequestDto medicineRequestDto = new MedicineRequestDto();
                medicineRequestDto.setSerialNumber(serialNumber);
                serialNumber++;
                medicineRequestDto.setMedicineName(record.getMedicineName());
                medicineRequestDto.setQuantityPerUnit(record.getQuantityPerUnit());
                medicineRequestDto.setReOrderQuantity(quantityToOrderInt);
                medicineRequestDto.setReOrderUnits(unitsToOrderInt);

                returnList.add(medicineRequestDto);
            }
        }

        return returnList;
    }

    @Transactional
    public List<MedicineInventory> saveOrderedMedicines(List<MedicineOrderedDto> records) {
        List<MedicineInventory> returnList = new ArrayList<>();

        for(MedicineOrderedDto record : records) {
            MedicineInventory medicine =
                    medicineRepository.findMedicineInventoriesByMedicineName(record.getMedicineName());

            medicine.setQuantityPerUnit(record.getQuantityPerUnit());
            int totalMedicinesOrdered = record.getUnitsOrdered() * record.getQuantityPerUnit();

            medicine.setQuantityAvailableToday(medicine.getQuantityAvailableToday() + totalMedicinesOrdered);
            medicine.setDateLastUpdated(new Date());

            returnList.add(medicine);
            medicineRepository.save(medicine);
        }

        return returnList;
    }

    public Date reorderBy() {
        List<MedicineInventory> medicines = medicineRepository.findAll();
        Date earliestDate = new Date(new Date().getTime() + (long) (365L * 24 * 60 * 60 * 1000));

        for(MedicineInventory medicine : medicines) {
            Date endByDate = medicine.getEndingByDate();
            if(endByDate.before(earliestDate)) {
                earliestDate = endByDate;
            }
        }

        return earliestDate;
    }

    public List<String> getAllMedicineNames() {
        List<MedicineInventory> medicines = medicineRepository.findAll();

        List<String> medicineNames = new ArrayList<>();

        for(MedicineInventory medicine : medicines) {
            medicineNames.add(medicine.getMedicineName());
        }

        return medicineNames;
    }

    public void updateMedicineInventory(String medicineName,
                                        Double quantityAvailableToday,
                                        Double consumptionPerDay,
                                        Double consumptionPerMonth) {

        MedicineInventory medicine = medicineRepository.findMedicineInventoriesByMedicineName(medicineName);

        if(quantityAvailableToday != null) {
            medicine.setQuantityAvailableToday(quantityAvailableToday);
            medicine.setDateLastUpdated(new Date());
        }

        if(consumptionPerDay != null) {
            medicine.setConsumptionPerDay(consumptionPerDay);
            medicine.setQuantityAvailableToday(medicine.getQuantityAvailableToday());
        }

        if(consumptionPerMonth != null) {
            medicine.setConsumptionPerMonth(consumptionPerMonth);
            medicine.setQuantityAvailableToday(medicine.getQuantityAvailableToday());
        }

        medicineRepository.save(medicine);
    }
}
