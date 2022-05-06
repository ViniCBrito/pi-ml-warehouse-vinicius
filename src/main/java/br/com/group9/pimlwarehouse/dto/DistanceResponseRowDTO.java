package br.com.group9.pimlwarehouse.dto;

import lombok.*;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder @Getter @Setter
public class DistanceResponseRowDTO {
    private List<DistanceResponseElementDTO> elements;
}
