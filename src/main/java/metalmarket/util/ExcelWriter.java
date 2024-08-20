package metalmarket.util;

import metalmarket.dto.WareDto;
import metalmarket.enums.City;
import metalmarket.model.Count;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.List;

public class ExcelWriter {
    private ExcelWriter() {
    }

    public static String writeToExcelFileAndGetPath(Count count, City city) {
        String title = CalculationsPath.getCalculationsTitle(city);
        String fileName = title + ".xlsx";
        String path = CalculationsPath.getCalculationsDisk() + fileName; // "G:\\" - тестовый путь. В итоге он будет заменен на путь на сервере
        Object[][] data = getDataForWritingInExcel(title, count, city);
        ExcelWriter.write(path, data);
        return path;
    }

    public static Object[][] getDataForWritingInExcel(String title, Count count, City city) {
        Field[] countFields = WareDto.class.getDeclaredFields();
        int numberOfColumns = countFields.length - 6;
        List<WareDto> wareDtos = count.getWareDtosList();

        Object[][] dataArray = new Object[wareDtos.size() + 3][numberOfColumns];
        dataArray[0][0] = title;

        dataArray[1][0] = "№";
        dataArray[1][1] = "Название товара";
        dataArray[1][2] = "Тип";
        dataArray[1][3] = "Сечение/Ячейка";
        dataArray[1][4] = "Диаметр";
        dataArray[1][5] = "Высота листа, рулона/длина хлыста";
        dataArray[1][6] = "Ширина листа/длина рулона";
        dataArray[1][7] = "Толщина";
        dataArray[1][8] = "Цвет/Покрытие";
        dataArray[1][9] = "Количество";
        dataArray[1][10] = "Цена";
        dataArray[1][11] = "Сумма";
        dataArray[1][12] = "Примечание";
        for (int i = 2; i < dataArray.length - 1; i++) {
            WareDto wareDto = wareDtos.get(i - 2);
            dataArray[i][0] = i - 1;
            dataArray[i][1] = wareDto.getName();
            dataArray[i][2] = wareDto.getType();
            dataArray[i][3] = wareDto.getDimension();
            dataArray[i][4] = wareDto.getDiameter();
            dataArray[i][5] = city == City.SAMARA ? wareDto.getHeightS() : wareDto.getHeightT();
            dataArray[i][6] = city == City.SAMARA ? wareDto.getWidthS() : wareDto.getWidthT();
            dataArray[i][7] = wareDto.getThickness();
            dataArray[i][8] = wareDto.getCover();
            dataArray[i][9] = wareDto.getQuantity();
            dataArray[i][10] = city == City.SAMARA ? wareDto.getPriceS() : wareDto.getPriceT();
            dataArray[i][11] = wareDto.getQuantity() * (city == City.SAMARA ? wareDto.getPriceS() : wareDto.getPriceT());
            dataArray[i][12] = wareDto.getComment();
        }
        dataArray[dataArray.length - 1][0] = "Всего:";
        dataArray[dataArray.length - 1][11] = count.getTotalSum();
        return dataArray;
    }

    public static void write(String path, Object[][] data) {
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("Расчет");

        Font fontBold = workbook.createFont();
        fontBold.setBold(true);
        Font fontThin = workbook.createFont();
        CellStyle styleBold = getCellStyle(workbook, fontBold);
        CellStyle styleThin = getCellStyle(workbook, fontThin);

        int rowNum = 0;
        for (int i = 0; i < data.length; i++) {
            Row row = sheet.createRow(rowNum++);
            int colNum = 0;
            for (Object field : data[i]) {
                Cell cell = row.createCell(colNum++);
                if (i <= 1 || i == data.length - 1) {
                    cell.setCellStyle(styleBold);
                } else {
                    cell.setCellStyle(styleThin);
                }

                if (field instanceof String) {
                    cell.setCellValue((String) field);
                } else if (field instanceof Integer) {
                    cell.setCellValue((Integer) field);
                } else if (field instanceof Double) {
                    cell.setCellValue((Double) field);
                }
            }
        }

        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, data[0].length - 1));
        sheet.addMergedRegion(new CellRangeAddress(
                data.length - 1, data.length - 1, 0, data[0].length - 3));

        for (int i = 0; i < data[0].length; i++) {
            sheet.autoSizeColumn(i);
        }

        try (FileOutputStream outputStream = new FileOutputStream(path)) {
            workbook.write(outputStream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Файл с расчетом сохранен по адресу: " + path);
    }

    private static CellStyle getCellStyle(XSSFWorkbook workbook, Font font) {
        CellStyle style = workbook.createCellStyle();
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setFont(font);
        return style;
    }
}
