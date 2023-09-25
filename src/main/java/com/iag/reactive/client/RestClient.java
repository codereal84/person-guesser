package com.iag.reactive.client;

import com.iag.reactive.dto.Age;
import com.iag.reactive.dto.Gender;
import com.iag.reactive.dto.Nationality;
import com.iag.reactive.exceptions.PersonDetailsServerException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.Exceptions;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;


@Component
@Slf4j
public class RestClient {

    @Value("${restclient.age.url}")
    private String ageUrl;

    @Value("${restclient.gender.url}")
    private String genderUrl;

    @Value("${restclient.nationality.url}")
    private String nationalityUrl;

    @Value("${restclient.retry.attempts}")
    private int retryAttempts;

    @Value("${restclient.retry.backoffSeconds}")
    private int backOffSeconds;
    private WebClient webClient;

    public RestClient(WebClient webClient) {
        this.webClient = webClient;
    }


    public Mono<Age> getAge(String name) {

        var url = UriComponentsBuilder.fromHttpUrl(ageUrl).queryParam("name", name).buildAndExpand().toUri();
        return webClient
                .get()
                .uri(url)
                .retrieve()
                .bodyToMono(Age.class)
                .retryWhen(retryLogic(retryAttempts, backOffSeconds));

    }

    public Mono<Gender> getGender(String name) {

        var url = UriComponentsBuilder.fromHttpUrl(genderUrl).queryParam("name", name).buildAndExpand().toUri();
        return webClient
                .get()
                .uri(url)
                .retrieve()
                .bodyToMono(Gender.class)
                .retryWhen(retryLogic(retryAttempts, backOffSeconds));
    }

    public Mono<Nationality> getNationality(String name) {
        var url = UriComponentsBuilder.fromHttpUrl(nationalityUrl).queryParam("name", name).buildAndExpand().toUri();
        return webClient
                .get()
                .uri(url)
                .retrieve()
                .bodyToMono(Nationality.class)
                .retryWhen(retryLogic(retryAttempts, backOffSeconds));
    }


    private Retry retryLogic(int retries, int backOffSeconds) {
        return Retry
                .backoff(retryAttempts, Duration.ofSeconds(backOffSeconds))
                .onRetryExhaustedThrow((retryBackoffSpec, retrySignal) -> Exceptions.propagate(retrySignal.failure()))
                .filter(ex -> ex instanceof PersonDetailsServerException);
    }


}
