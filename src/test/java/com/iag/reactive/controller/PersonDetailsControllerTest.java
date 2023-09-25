package com.iag.reactive.controller;

import com.iag.reactive.dto.Person;
import com.iag.reactive.service.PersonDetailsService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;


@WebFluxTest(controllers = PersonDetailsController.class)
@AutoConfigureWebTestClient
class PersonDetailsControllerTest {


    @Autowired
    private WebTestClient webTestClient;

    static String PERSON_DETAILS_URL = "/api/v1/person/badri";

    @MockBean
    private PersonDetailsService personDetailsService;

    @Test
    void getPersonDetailsSuccessful() {
        var person = new Person(10,"Badri","AS");
        when(personDetailsService.getDetails(any(String.class))).thenReturn(Mono.just(person));
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
                });
    }
}