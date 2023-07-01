package main.service;

import main.base.ExcelService;
import main.domain.User;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;

@Service
@PropertySource("classpath:application.properties")
public class ExcelServiceImpl implements ExcelService {


    @Override
    public String createExel(List<User> users) {
        int columnCounter = 0;
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("vk-users");
            while (columnCounter < 6) {
                sheet.setColumnWidth(columnCounter, 6000);
                columnCounter++;
            }
            columnCounter = 0;
            Row header = sheet.createRow(0);
            List<String> fields = List.of("user_id", "user_f_name", "user_l_name", "user_b_date", "user_city", "user_contacts");
            while (columnCounter < 6) {
                Cell headerCell = header.createCell(columnCounter);
                headerCell.setCellValue(fields.get(columnCounter));
                columnCounter++;
            }
            columnCounter = 0;
            Cell cell;
            for (int i = 0; i < users.size(); i++) {
                Row row = sheet.createRow(i + 1);
                User user = users.get(i);
                cell = row.createCell(columnCounter++);
                cell.setCellValue(String.valueOf(user.getUser_id()));
                cell = row.createCell(columnCounter++);
                cell.setCellValue(user.getUser_f_name());
                cell = row.createCell(columnCounter++);
                cell.setCellValue(user.getUser_l_name());
                cell = row.createCell(columnCounter++);
                cell.setCellValue(String.valueOf(user.getUser_b_date()));
                cell = row.createCell(columnCounter++);
                cell.setCellValue(user.getUser_city());
                cell = row.createCell(columnCounter++);
                cell.setCellValue(user.getUser_contacts());
                columnCounter = 0;
            }


            return createExcelFile(workbook);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String createExcelFile(Workbook workbook) throws IOException {
        String fileName = "vk_user_".concat(String.valueOf(LocalDate.now()).replaceAll("-", "_")).concat(".xlsx");
        Path full = Paths.get(".", fileName);
        Files.deleteIfExists(full);
        Files.createFile(full);
        try (FileOutputStream outputStream = new FileOutputStream(full.toFile())) {
            workbook.write(outputStream);
        }
        return full.toString();
    }
}
