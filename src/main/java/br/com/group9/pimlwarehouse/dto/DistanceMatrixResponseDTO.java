package br.com.group9.pimlwarehouse.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;
import java.util.Map;

@NoArgsConstructor
@AllArgsConstructor
@Builder @Getter @Setter
public class DistanceMatrixResponseDTO {
    @JsonProperty(value = "destination_addresses")
    private List<String> destinations;
    @JsonProperty(value = "origin_addresses")
    private List<String> origins;
    private List<DistanceResponseRowDTO> rows;
    private String status;

//    @JsonProperty("rows")
//    private void unpackElementFromNestedObject(List<Object> rows) {
//        rows.forEach(element -> {
//            element.a
//        });
//        this.distanceText = distance.get("text");
//        this.distanceValue = Integer.valueOf(distance.get("value"));
//    }
}
