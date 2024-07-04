package metalmarket.controller;

import lombok.RequiredArgsConstructor;
import metalmarket.enums.City;
import metalmarket.model.Ware;
import metalmarket.model.Count;
import metalmarket.service.QueryService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class QueryController {
    private final QueryService queryService;

    @GetMapping("/get/{cityName}")
    @ResponseStatus(HttpStatus.OK)
    public Count getCount(@PathVariable String cityName, @RequestBody List<Ware> wares) throws IOException {
        if (cityName.equals("Samara")) {
            return queryService.getCount(City.SAMARA, wares);
        } else if (cityName.equals("Tolyatti")) {
            return queryService.getCount(City.TOLYATTI, wares);
        }
        return null;
    }
}