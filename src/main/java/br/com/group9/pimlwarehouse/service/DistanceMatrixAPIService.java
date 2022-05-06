package br.com.group9.pimlwarehouse.service;

import br.com.group9.pimlwarehouse.dto.DistanceMatrixResponseDTO;
import br.com.group9.pimlwarehouse.dto.DistanceResponseElementDTO;
import br.com.group9.pimlwarehouse.exception.UnavailableException;
import br.com.group9.pimlwarehouse.service.handler.DistanceMatrixAPIErrorHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class DistanceMatrixAPIService {
    @Value("${gcp.distance_matrix.api_key}")
    private String API_KEY = "";
    private static final String DISTANCE_API_URI = "https://maps.googleapis.com/maps/api/distancematrix";
    private static final String DISTANCE_RESOURCE = "/json";
    private final RestTemplate restTemplate;

    public DistanceMatrixAPIService(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder
                .errorHandler(new DistanceMatrixAPIErrorHandler())
                .build();
    }

    /**
     * Creates the URI for a request in the GCP Distance Matrix API
     * @param origins as a Collection where every String is a origin location
     * @param destinations as a Collection where every String is a origin location
     * @return formatted URI as a String
     */
    private String generateRequestURI(Collection<String> origins, Collection<String> destinations) {
        String mode = "mode=driving";
        String originsParam = "origins=";
        originsParam = originsParam.concat(origins.stream().map(d -> "place_id:".concat(d)).collect(Collectors.joining("|")));
        String destinationsParam = "destinations=";
        destinationsParam = destinationsParam.concat(destinations.stream().map(d -> "place_id:".concat(d)).collect(Collectors.joining("|")));
        String reqParams = "?".concat(mode)
                .concat("&").concat(originsParam)
                .concat("&").concat(destinationsParam)
                .concat("&key=").concat(API_KEY);
        return DISTANCE_API_URI.concat(DISTANCE_RESOURCE).concat(reqParams);
    }

    /**
     * Maps a Distance API response to a map of Warehouse ID by calculated distance
     * @param destinationsMap as a map of destinations by warehouse ID
     * @param response of the API call
     * @return a new map of Warehouse ID by distance calculated in the response
     */
    private Map<DistanceResponseElementDTO, Long> getWarehouseByDistance(Map<Long, String> destinationsMap, DistanceMatrixResponseDTO response) {
        Map<DistanceResponseElementDTO, Long> returnMap = new HashMap<>();
        Iterator<Map.Entry<Long, String>> iterator = destinationsMap.entrySet().iterator();
        for(int i = 0; iterator.hasNext(); i++) {
            DistanceResponseElementDTO distance = response.getRows().get(0).getElements().get(i);
            Long warehouse = iterator.next().getKey();
            returnMap.put(distance, warehouse);
        }
        return returnMap;
    }

    /**
     * Fetch GCP calculated distance of an placeId and several destinations
     * @param originId as a String containing the origin placeId
     * @param destinationsMap as a Map of destinations placeIds by their Warehouse ID
     * @return a new map of Warehouse ID by distance calculated in the response
     */
    public Map<DistanceResponseElementDTO, Long> fetchDistances(String originId, Map<Long, String> destinationsMap) {
        String resourceURI = generateRequestURI(Arrays.asList(originId), destinationsMap.values());

        ResponseEntity<DistanceMatrixResponseDTO> result = restTemplate.getForEntity(resourceURI, DistanceMatrixResponseDTO.class);
        DistanceMatrixResponseDTO calculatedDistances = result.getBody();
        if(!calculatedDistances.getStatus().equalsIgnoreCase("OK")){
            switch (calculatedDistances.getStatus()){
                case "INVALID_REQUEST":
                case "MAX_ELEMENTS_EXCEEDED":
                case "MAX_DIMENSIONS_EXCEEDED":
                case "OVER_DAILY_LIMIT":
                case "OVER_QUERY_LIMIT":
                case "REQUEST_DENIED":
                case "UNKNOWN_ERROR":
                default:
                    throw new UnavailableException("DISTANCE_MATRIX_API_UNAVAILABLE");
            }
        }
        return getWarehouseByDistance(destinationsMap, calculatedDistances);
    }
}
