package br.com.group9.pimlwarehouse.service;

import br.com.group9.pimlwarehouse.dto.PlacesResponseDTO;
import br.com.group9.pimlwarehouse.entity.Address;
import br.com.group9.pimlwarehouse.exception.InvalidAddressException;
import br.com.group9.pimlwarehouse.exception.UnavailableException;
import br.com.group9.pimlwarehouse.service.handler.PlacesAPIErrorHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class PlacesAPIService {
    @Value("${gcp.places.api_key}")
    private String API_KEY = "";
    private static final String PLACES_API_URI = "https://maps.googleapis.com/maps/api/place";
    private static final String PLACES_RESOURCE = "/findplacefromtext/json";
    private final RestTemplate restTemplate;

    public PlacesAPIService(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder
                .errorHandler(new PlacesAPIErrorHandler())
                .build();
    }

    /**
     * Fetch GCP placeId based in an Address object
     * @param address to search for the placeId
     * @return given placeId as String. If it does not get any results, returns "ADDRESS_NOT_FOUND" in
     * InvalidAddressException, or "PLACES_API_UNAVAILABLE" as UnavailableException if any other response for
     * the GCP Places API.
     */
    public String fetchPlaceIdByAddress(Address address) {
        String inputType = "inputtype=textquery";
        String input = "input=".concat(address.searchQuery());
        String reqParams = "?".concat(inputType).concat("&").concat(input).concat("&key=").concat(API_KEY);
        String resourceURI = PLACES_API_URI.concat(PLACES_RESOURCE).concat(reqParams);

        ResponseEntity<PlacesResponseDTO> result = restTemplate.getForEntity(resourceURI, PlacesResponseDTO.class);
        PlacesResponseDTO response = result.getBody();
        if(!response.getStatus().equalsIgnoreCase("OK")){
            switch (response.getStatus()){
                case "ZERO_RESULTS":
                    throw new InvalidAddressException("ADDRESS_NOT_FOUND");
                case "INVALID_REQUEST":
                case "OVER_QUERY_LIMIT":
                case "REQUEST_DENIED":
                case "UNKNOWN_ERROR":
                default:
                    throw new UnavailableException("PLACES_API_UNAVAILABLE");
            }
        }
        return response.getCandidates().get(0).getPlaceId();
    }

    /**
     * Create a new agent if it has not linked to a warehouse.
     * @param agentDTO receives agent data to do validation.
     * @return warehouse Id will be informed, if it does not exist, returns "WAREHOUSE_NOT-FOUND".
     * Will perform validation with the authentication API and will return, if the given agent already
     * exists, returns "AGENT_ALREADY_EXISTS" or "MALFORMED_AGENT_DATA" if any other response from auth API.
     * In case the validation call doesn't suceeds, returns "AUTHENTICATION_API_UNAVAILABLE" UnavailableException.
     */
//    public AgentDTO createAgent(AgentDTO agentDTO) {
//        String resourceURI = AUTH_API_URI.concat(AUTH_RESOURCE).concat("/");
//
//        if(!this.warehouseService.exists(agentDTO.getWarehouseId()))
//            throw new WarehouseNotFoundException("WAREHOUSE_NOT_FOUND");
//
//        ResponseEntity<AgentDTO> result = restTemplate.postForEntity(resourceURI, agentDTO, AgentDTO.class);
//        return result.getBody();
//    }
}
