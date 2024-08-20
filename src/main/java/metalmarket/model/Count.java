package metalmarket.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import metalmarket.dto.WareDto;
import metalmarket.enums.City;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class Count {
    private List<WareDto> wareDtosList;
    private City city;
    private double totalSum;

    @Setter
    private String path;
}
