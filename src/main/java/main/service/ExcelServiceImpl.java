package main.service;

import main.base.ExcelService;
import main.domain.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.Strings;
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
import java.text.MessageFormat;
import java.time.LocalDate;
import java.util.List;

@Service
@PropertySource("classpath:application.properties")
public class ExcelServiceImpl implements ExcelService {

    private static final Logger logger = LogManager.getLogger(ExcelServiceImpl.class);

    @Value("${exel.directory}")
    private String directory = "exel";

    @Value("${pool.size}")
    private int poolSize = 4;

    @Override
    public String createExel(List<User> users) {
        if (users.isEmpty()) {
            logger.warn("Не найдено данных для создания файла exel");
            return Strings.EMPTY;
        }
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
        } catch (Exception e) {
            StackTraceElement ste = e.getStackTrace()[0];
            String message = MessageFormat.format("Ошибка {0} в классе {1} методе {2} строке {3}.", e.getMessage(), ste.getClassName(), ste.getMethodName(), ste.getLineNumber());
            if (e.getCause() != null) message = message.concat(" Причина: ").concat(e.getCause().getMessage());
            logger.error(message);
            return Strings.EMPTY;
        }
    }

    private String createExcelFile(Workbook workbook) throws IOException {
        Path path = Paths.get(directory);
        if (Files.notExists(path)) {
            Files.createDirectories(path);
        }
        String fileName = "vk_user_".concat(String.valueOf(LocalDate.now()).replaceAll("-", "_")).concat(".xlsx");
        Path full = path.resolve(fileName);
        Files.deleteIfExists(full);
        Files.createFile(full);
        try (FileOutputStream outputStream = new FileOutputStream(full.toFile())) {
            workbook.write(outputStream);
        }
        return full.toString();
    }

}
