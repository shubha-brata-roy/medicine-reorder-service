package com.royhome.medicinereorderservice.repositories;

import com.royhome.medicinereorderservice.models.MedicineInventory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MedicineRepository extends JpaRepository<MedicineInventory, Long> {
    public MedicineInventory save(MedicineInventory medicineInventory);

    public List<MedicineInventory> findAll();

    public MedicineInventory findMedicineInventoriesByMedicineName(String medicineName);

}
