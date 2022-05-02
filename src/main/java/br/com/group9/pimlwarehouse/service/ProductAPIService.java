package br.com.group9.pimlwarehouse.service;

import br.com.group9.pimlwarehouse.dto.ProductDTO;
import br.com.group9.pimlwarehouse.exception.ProductNotFoundException;
import br.com.group9.pimlwarehouse.service.handler.ProductAPIErrorHandler;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ProductAPIService {
    private static final String PRODUCT_API_URI = "https://4bf27f8c-fe37-4752-b67f-a9aba01d33a4.mock.pstmn.io";
    private static final String PRODUCTS_RESOURCE = "/api/v1/fresh-products";
    private final RestTemplate restTemplate;

    public ProductAPIService(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder
                .errorHandler(new ProductAPIErrorHandler())
                .build();
    }

    public ProductDTO fetchProductById(Long id) {
        String resourceURI = PRODUCT_API_URI.concat(PRODUCTS_RESOURCE).concat("/").concat(id.toString());
        ResponseEntity<ProductDTO> result = restTemplate.getForEntity(resourceURI, ProductDTO.class);
        return result.getBody();
    }
}
