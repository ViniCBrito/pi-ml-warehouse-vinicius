package br.com.group9.pimlwarehouse.service.handler;

import br.com.group9.pimlwarehouse.exception.InvalidAddressException;
import br.com.group9.pimlwarehouse.exception.UnavailableException;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResponseErrorHandler;

import java.io.IOException;

import static org.springframework.http.HttpStatus.Series.CLIENT_ERROR;
import static org.springframework.http.HttpStatus.Series.SERVER_ERROR;

@Component
public class DistanceMatrixAPIErrorHandler implements ResponseErrorHandler {

    @Override
    public boolean hasError(ClientHttpResponse httpResponse) throws IOException {

        return (httpResponse.getStatusCode().series() == CLIENT_ERROR
                    || httpResponse.getStatusCode().series() == SERVER_ERROR);
    }

    @Override
    public void handleError(ClientHttpResponse httpResponse) throws IOException {

        if (httpResponse.getStatusCode().series() == SERVER_ERROR) {
        } else if (httpResponse.getStatusCode().series() == HttpStatus.Series.CLIENT_ERROR) {

            if (httpResponse.getStatusCode() == HttpStatus.BAD_REQUEST) {
                throw new InvalidAddressException("ADDRESS_NOT_FOUND");
            }
        }

        throw new UnavailableException("DISTANCE_MATRIX_API_UNAVAILABLE");
    }
}