package com.royhome.medicinereorderservice.controllers;

import com.royhome.medicinereorderservice.dtos.DateDto;
import com.royhome.medicinereorderservice.dtos.MedicineInsertDto;
import com.royhome.medicinereorderservice.dtos.MedicineOrderedDto;
import com.royhome.medicinereorderservice.dtos.MedicineRequestDto;
import com.royhome.medicinereorderservice.models.MedicineInventory;
import com.royhome.medicinereorderservice.services.MedicineService;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.view.RedirectView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

@RestController
@RequestMapping("/medicines")
public class MedicineController {

    private final MedicineService medicineService;

    @Autowired
    public MedicineController(MedicineService medicineService) {
        this.medicineService = medicineService;
    }

    @GetMapping("/refresh")
    public void refreshMedicineAvailability() {
        medicineService.refreshMedicineAvailability();
    }


    /************************** Download using Excel code ********************************/

    @GetMapping("/download")
    public void downloadExcel(
            @RequestParam("date") @DateTimeFormat(pattern = "yyyy-MM-dd") Date date,
            HttpServletResponse response) throws IOException {

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Medicine Reorder List");

        String[] headers = {"Sl no",
                            "Medicine Name",
                            "Quantity Per Unit",
                            "Reorder Quantity",
                            "Reorder Units" };

        Row headerRow = sheet.createRow(0);

        for(int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
        }

        List<MedicineRequestDto> medicines = medicineService.generateMedicineReorderList(date);

        int rowNum = 1;

        for(MedicineRequestDto medicine : medicines) {
            Row row = sheet.createRow(rowNum++);

            row.createCell(0).setCellValue(medicine.getSerialNumber());
            row.createCell(1).setCellValue(medicine.getMedicineName());
            row.createCell(2).setCellValue(medicine.getQuantityPerUnit());
            row.createCell(3).setCellValue(medicine.getReOrderQuantity());
            row.createCell(4).setCellValue(medicine.getReOrderUnits());
        }

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=medicine-reorder-list.xlsx");

        workbook.write(response.getOutputStream());
        workbook.close();

    }

    /**************************************************/

    @PostMapping("/reorder")
    public ResponseEntity<List<MedicineRequestDto>> generateMedicineReorderList(@RequestBody DateDto reorderDate) {
        List<MedicineRequestDto> medicineRequest = medicineService
                                                    .generateMedicineReorderList(reorderDate.getReOrderDate());

        return new ResponseEntity<List<MedicineRequestDto>>(medicineRequest, HttpStatus.OK);
    }

    /************************** Upload using Excel code ********************************/

    @PostMapping("/upload")
    public ResponseEntity<String> uploadExcel(@RequestParam("file") MultipartFile file) {
        try {
            // Load the Excel file
            Workbook workbook = new XSSFWorkbook(file.getInputStream());

            // Read data from the Excel file
            List<MedicineOrderedDto> medicines = new ArrayList<>();

            Sheet sheet = workbook.getSheetAt(0);

            for (Row row : sheet) {
                if(row.getRowNum() == 0) {
                    continue;
                }

                // Here the data for each record is getting read
                MedicineOrderedDto medicine = new MedicineOrderedDto();

                medicine.setMedicineName(row.getCell(0).getStringCellValue());
                medicine.setQuantityPerUnit((int) row.getCell(1).getNumericCellValue());
                medicine.setUnitsOrdered((int) row.getCell(2).getNumericCellValue());
                medicines.add(medicine);
            }

            // Process the data (e.g., save to database)

            medicineService.saveOrderedMedicines(medicines);

            workbook.close();
            return new ResponseEntity<>("File uploaded successfully", HttpStatus.OK);
        } catch (IOException e) {
            e.printStackTrace();
            return new ResponseEntity<>("Failed to upload file", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /******************************************************************/

    @RequestMapping(value = "/ordered", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<MedicineInventory>> orderCompleted(@RequestBody List<MedicineOrderedDto> records) {
        List<MedicineInventory> medicines = medicineService.saveOrderedMedicines(records);
        return new ResponseEntity<List<MedicineInventory>>(medicines, HttpStatus.OK);
    }

    @GetMapping("/reorder-by-date")
    public ResponseEntity<DateDto> reorderBy() {
        DateDto reorderDate = new DateDto();
        reorderDate.setReOrderDate(medicineService.reorderBy());

        return new ResponseEntity<>(reorderDate, HttpStatus.OK);
    }

    // need to write GET, POST, PATCH, DELETE
    // Post-Insert medicine records on the database
    @RequestMapping(value = "/insert", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<MedicineInventory>> insertMedicineDetails(@RequestBody List<MedicineInsertDto> records) {
        // insert dateLastUpdated as the current date
        List<MedicineInventory> medicineSaved = medicineService.insertMedicineDetails(records);
        return new ResponseEntity<List<MedicineInventory>>(medicineSaved, HttpStatus.OK);
    }

    @GetMapping("/names")
    public List<String> getAllMedicineNames() {
        return medicineService.getAllMedicineNames();
    }

    @PostMapping("/update")
    public ResponseEntity<String> updateMedicineInventory(
                                    @RequestParam(name = "medicineName", required = true) String medicineName,
                                    @RequestParam(name = "quantityAvailableToday", required = false) Double quantityAvailableToday,
                                    @RequestParam(name = "consumptionPerDay", required = false) Double consumptionPerDay,
                                    @RequestParam(name = "consumptionPerMonth", required = false) Double consumptionPerMonth) {

        medicineService.updateMedicineInventory(medicineName, quantityAvailableToday, consumptionPerDay, consumptionPerMonth);

        return new ResponseEntity<>("Updated Successfully. \n \n " +
                                    "<button onclick=\"window.location.href = 'http://localhost:8080/update-medicines.html'\">Go to Update Medicines</button>",
                                    HttpStatus.OK);
    }
}
