package metalmarket.service;

import lombok.RequiredArgsConstructor;
import metalmarket.enums.City;
import metalmarket.model.Ware;
import metalmarket.model.Count;
import metalmarket.dto.WareDto;
import metalmarket.util.DataHandler;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class QueryService {

    public Count getCountByQuantity(City city, List<Ware> wares) {
        List<WareDto> wareList = new ArrayList<>();
        double total = 0;
        Map<String, WareDto> wareDtoMap = DataHandler.getWareDtoMap();
        for (Ware ware : wares) {
            WareDto wareDto = wareDtoMap.get(ware.getId());
            Double price = null;
            if (city == City.SAMARA) {
                price = wareDto.getPriceS();
            } else if (city == City.TOLYATTI) {
                price = wareDto.getPriceT();
            }

            DataHandler.setTotalQuantity(wareDto, ware, city);
            wareDto.setSum(price * wareDto.getQuantity());
            wareList.add(wareDto);
            total += wareDto.getSum();
        }

        return new Count(wareList, city, total, null);
    }

    public Count getCountByPerimeter(City city, List<String> wareIds, double perimeterLength) {
        List<WareDto> wareList = new ArrayList<>();
        double total = 0;
        Map<String, WareDto> wareDtoMap = DataHandler.getWareDtoMap();
        for (String wareId : wareIds) {
            WareDto wareDto = wareDtoMap.get(wareId);
            Double price = null;
            if (city == City.SAMARA) {
                price = wareDto.getPriceS();
            } else if (city == City.TOLYATTI) {
                price = wareDto.getPriceT();
            }

            DataHandler.setTotalQuantity(wareDto, perimeterLength, city);
            wareDto.setSum(price * wareDto.getQuantity());
            wareList.add(wareDto);
            total += wareDto.getSum();
        }

        return new Count(wareList, city, total, null);
    }
}
