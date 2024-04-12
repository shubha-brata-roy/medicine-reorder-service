package com.royhome.medicinereorderservice;

import com.royhome.medicinereorderservice.controllers.MedicineController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MedicineReorderServiceApplication {

	private static MedicineController medicineController;

	@Autowired
	public MedicineReorderServiceApplication(MedicineController medicineController) {
		MedicineReorderServiceApplication.medicineController = medicineController;
	}

	public static void main(String[] args) {
		SpringApplication.run(MedicineReorderServiceApplication.class, args);
		medicineController.refreshMedicineAvailability();
	}

}
