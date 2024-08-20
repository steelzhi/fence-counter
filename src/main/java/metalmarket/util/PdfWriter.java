package metalmarket.util;

import com.aspose.cells.*;

public class PdfWriter {
    private PdfWriter() {
    }

    public static String writeExcelToPdfAndGetPath(String excelPath) throws Exception {
        Workbook workbook = new Workbook(excelPath);

        //Сохранить как PDF документ
        String pdfPath = excelPath.substring(0, excelPath.length() - 4) + "pdf";
        PdfSaveOptions pdfSaveOptions = new PdfSaveOptions();
        pdfSaveOptions.setOnePagePerSheet(true);

        workbook.save(pdfPath, pdfSaveOptions);

        return pdfPath;
    }
}
