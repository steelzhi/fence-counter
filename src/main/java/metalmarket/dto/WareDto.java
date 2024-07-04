package metalmarket.dto;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class WareDto {
    private String id;
    private String name;
    private String type;
    private String dimension;
    private String diameter;
    private double heightS;
    private double heightT;
    private double widthS;
    private double widthT;
    private String thickness;
    private String cover;
    private double priceS;
    private double priceT;
    private boolean availabilityS;
    private boolean availabilityT;
    private String comment;
    private double quantity;
    private double multiple;
    private double sum;
}

