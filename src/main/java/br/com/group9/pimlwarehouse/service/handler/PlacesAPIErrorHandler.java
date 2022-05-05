package br.com.group9.pimlwarehouse.service.handler;

import br.com.group9.pimlwarehouse.dto.ErrorMessageDTO;
import br.com.group9.pimlwarehouse.exception.ProductNotFoundException;
import br.com.group9.pimlwarehouse.exception.UnavailableException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResponseErrorHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.Series.CLIENT_ERROR;
import static org.springframework.http.HttpStatus.Series.SERVER_ERROR;

@Component
public class PlacesAPIErrorHandler implements ResponseErrorHandler {

    @Override
    public boolean hasError(ClientHttpResponse httpResponse) throws IOException {

        return (httpResponse.getStatusCode().series() == CLIENT_ERROR
                    || httpResponse.getStatusCode().series() == SERVER_ERROR);
    }

    @Override
    public void handleError(ClientHttpResponse httpResponse) throws IOException {

        if (httpResponse.getStatusCode().series() == SERVER_ERROR) {
        } else if (httpResponse.getStatusCode().series() == HttpStatus.Series.CLIENT_ERROR) {
        }

        throw new UnavailableException("PLACES_API_UNAVAILABLE");
    }
}