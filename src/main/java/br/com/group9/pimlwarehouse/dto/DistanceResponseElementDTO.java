package br.com.group9.pimlwarehouse.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.Map;

@NoArgsConstructor
@AllArgsConstructor
@Builder @Getter @Setter
public class DistanceResponseElementDTO {
    private String distanceText;
    private Integer distanceValue;
    private String durationText;
    private Integer durationValue;
    private String status;

    @JsonProperty("distance")
    private void unpackDistanceFromNestedObject(Map<String, String> distance) {
        this.distanceText = distance.get("text");
        this.distanceValue = Integer.valueOf(distance.get("value"));
    }

    @JsonProperty("duration")
    private void unpackDurationFromNestedObject(Map<String, String> duration) {
        this.durationText = duration.get("text");
        this.durationValue = Integer.valueOf(duration.get("value"));
    }
}
