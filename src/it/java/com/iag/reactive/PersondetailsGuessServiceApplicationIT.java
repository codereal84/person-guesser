package com.iag.reactive;


import com.iag.reactive.dto.Person;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
public class PersondetailsGuessServiceApplicationIT {
    @Autowired
    private WebTestClient webTestClient;
    @Test
    void getProjectDetailsSuccess() {
         String PERSON_DETAILS_URL = "/api/v1/person/badri";
         webTestClient
                 .get()
                 .uri(PERSON_DETAILS_URL)
                 .exchange()
                 .expectStatus()
                 .is2xxSuccessful()
                 .expectBody(Person.class)
                 .consumeWith(personEntityExchangeResult ->  {
                     var personResponse = personEntityExchangeResult.getResponseBody();
                     assertThat(personResponse).isNotNull();
                     assertThat(personResponse.getAge()).isNotNull();
                     assertThat(personResponse.getGender()).isNotNull();
                     assertThat(personResponse.getNationality()).isNotNull();
                 });
    }

    @Test
    void notFoundPersonDetailsForRequestParamAsNumber() {
        String PERSON_DETAILS_URL = "/api/v1/person/1111";
        webTestClient
                .get()
                .uri(PERSON_DETAILS_URL)
                .exchange()
                .expectStatus()
                .isNotFound();
    }
}
