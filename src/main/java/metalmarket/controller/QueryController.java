package metalmarket.controller;

import lombok.RequiredArgsConstructor;
import metalmarket.enums.City;
import metalmarket.model.Count;
import metalmarket.model.Ware;
import metalmarket.service.QueryService;
import metalmarket.util.ExcelWriter;
//import metalmarket.util.PdfWriter;
import metalmarket.util.PdfWriter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class QueryController {
    private final QueryService queryService;

    @GetMapping("/get/count-by-quantity/{cityName}")
    @ResponseStatus(HttpStatus.OK)
    public Count getCountByQuantity(@PathVariable String cityName,
                                    @RequestBody List<Ware> wares) throws Exception {
        City city = City.valueOf(cityName.toUpperCase());
        Count count = queryService.getCountByQuantity(city, wares);
        String excelPath = ExcelWriter.writeToExcelFileAndGetPath(count, city);
        String pdfPath = PdfWriter.writeExcelToPdfAndGetPath(excelPath);
        count.setPath(pdfPath);
        return count;
    }

    @GetMapping("/get/count-by-perimeter/{cityName}")
    @ResponseStatus(HttpStatus.OK)
    public Count getCountByPerimeter(@PathVariable String cityName,
                                     @RequestBody List<String> wareIds,
                                     @RequestParam double perimeterLength) throws Exception {
        City city = City.valueOf(cityName.toUpperCase());
        Count count = queryService.getCountByPerimeter(city, wareIds, perimeterLength);
        String excelPath = ExcelWriter.writeToExcelFileAndGetPath(count, city);
        String pdfPath = PdfWriter.writeExcelToPdfAndGetPath(excelPath);
        count.setPath(pdfPath);
        return count;
    }
}