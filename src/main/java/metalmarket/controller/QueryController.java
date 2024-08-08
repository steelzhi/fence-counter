package metalmarket.controller;

import lombok.RequiredArgsConstructor;
import metalmarket.enums.City;
import metalmarket.model.Count;
import metalmarket.model.Ware;
import metalmarket.service.QueryService;
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
                                    @RequestBody List<Ware> wares) {
        return queryService.getCountByQuantity(City.valueOf(cityName.toUpperCase()), wares);
    }

    @GetMapping("/get/count-by-perimeter/{cityName}")
    @ResponseStatus(HttpStatus.OK)
    public Count getCountByPerimeter(@PathVariable String cityName,
                                     @RequestBody List<String> wareIds,
                                     @RequestParam double perimeterLength) {
        return queryService.getCountByPerimeter(City.valueOf(cityName.toUpperCase()), wareIds, perimeterLength);
    }
}