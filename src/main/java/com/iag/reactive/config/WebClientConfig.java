package com.iag.reactive.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.iag.reactive.exceptions.PersonDetailsClientException;
import com.iag.reactive.exceptions.PersonDetailsServerException;
import com.iag.reactive.dto.ServError;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Configuration
@Slf4j
public class WebClientConfig {


    @Bean
    public WebClient getWebClient(WebClient.Builder builder) {
        return builder.filter(errorHandler()).build();
    }


    public static ExchangeFilterFunction errorHandler() {

        return ExchangeFilterFunction.ofResponseProcessor(clientResponse -> {
            if (clientResponse.statusCode().is5xxServerError()) {
                return clientResponse.bodyToMono(String.class)
                        .flatMap(errorBody -> {
                            try {
                                log.error("Server error with error code {} and message  {} ", clientResponse.statusCode(), objectMapper(errorBody).getError());
                                return Mono.error(new PersonDetailsServerException("Person Details Server Error::" + objectMapper(errorBody).getError(), clientResponse.statusCode().value()));
                            } catch (JsonProcessingException e) {
                                return Mono.error(new RuntimeException(e));
                            }

                        });
            } else if (clientResponse.statusCode().is4xxClientError()) {
                return clientResponse.bodyToMono(String.class)
                        .flatMap(errorBody -> {
                            try {
                                log.error("Client error with error code {} and message  {} ", clientResponse.statusCode(), objectMapper(errorBody).getError());
                                return Mono.error(new PersonDetailsClientException("Person Details client error::" + objectMapper(errorBody).getError(), clientResponse.statusCode().value()));
                            } catch (JsonProcessingException e) {
                                return Mono.error(new RuntimeException(e));
                            }

                        });
            } else {
                log.error("Unexpected error with error code {}  ",clientResponse.statusCode());
                return Mono.just(clientResponse);
            }
        });
    }

    private static ServError objectMapper(String error) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(error, ServError.class);
    }

}

