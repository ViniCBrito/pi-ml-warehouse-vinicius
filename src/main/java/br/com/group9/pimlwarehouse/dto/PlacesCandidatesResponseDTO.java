package br.com.group9.pimlwarehouse.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder @Getter @Setter
public class PlacesCandidatesResponseDTO {
    @JsonProperty(value = "place_id")
    private String placeId;
}
