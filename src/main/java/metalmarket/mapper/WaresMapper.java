package metalmarket.mapper;

import metalmarket.model.Ware;
import metalmarket.dto.WareDto;

public class WaresMapper {
    private WaresMapper() {
    }

    public static WareDto mapToWareDto(Ware ware, String wareDaraFile) {
        WareDto wareDto = null;
        if (ware != null) {
            wareDto = new WareDto();
            wareDto.setId(ware.getId());
            String[] params = wareDaraFile.split("; ");
            wareDto.setName(params[1]);
            if (params[2] != "") {
                wareDto.setType(params[2]);
            }
            if (params[3] != "") {
                wareDto.setDimension(params[3]);
            }
            if (params[4] != "") {
                wareDto.setDimension(params[4]);
            }
            if (params[5] != "") {
                wareDto.setHeightS(Double.parseDouble(params[5]));
            }
            if (params[6] != "") {
                wareDto.setHeightT(Double.parseDouble(params[6]));
            }
            if (params[7] != "") {
                wareDto.setWidthS(Double.parseDouble(params[7]));
            }
            if (params[8] != "") {
                wareDto.setWidthT(Double.parseDouble(params[8]));
            }
            if (params[9] != "") {
                wareDto.setThickness(params[9]);
            }
            if (params[10] != "") {
                wareDto.setCover(params[10]);
            }
            if (params[11] != "") {
                wareDto.setPriceS(Double.parseDouble(params[11]));
            }
            if (params[12] != "") {
                wareDto.setPriceS(Double.parseDouble(params[12]));
            }
            if (wareDto.getPriceS() == 0) {
                wareDto.setPriceS(wareDto.getPriceT());
            } else if (wareDto.getPriceT() == 0) {
                wareDto.setPriceT(wareDto.getPriceS());
            }

            wareDto.setAvailabilityS(params[13].equals("1") ? true : false);
            wareDto.setAvailabilityS(params[14].equals("1") ? true : false);
            wareDto.setComment(params[15]);
            wareDto.setQuantity(ware.getQuantity());
            /*
             * По умолчанию, все товары должны быть кратны 1.0 (шт., кг, листу). Исключение - труба (она будет кратна м;
             * это будет учтено в сервисе).
             */
            wareDto.setMultiple(1);

        }

        return wareDto;
    }
}

