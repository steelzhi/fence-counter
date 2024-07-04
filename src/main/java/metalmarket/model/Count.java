package metalmarket.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import metalmarket.dto.WareDto;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class Count {
    private List<WareDto> wareDtosList;
    private double totalSum;
}
