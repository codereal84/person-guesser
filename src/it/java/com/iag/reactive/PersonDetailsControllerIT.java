package com.iag.reactive;


import com.iag.reactive.dto.Person;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient(timeout = "36000")
@ActiveProfiles("test")
@AutoConfigureWireMock(port = 8090)
@TestPropertySource(
        properties = {
                "restclient.age.url=http://localhost:8090/age",
                "restclient.gender.url=http://localhost:8090/gender",
                "restclient.nationality.url=http://localhost:8090/nationality"
        }
)
public class PersonDetailsControllerIT {

    @Value("${restclient.retry.attempts}")
    private int retryAttempts;

    @Value("${restclient.retry.backoffSeconds}")
    private int backOffSeconds;

    @Autowired
    WebTestClient webTestClient;


    static String URL_PERSON_DETAILS = "/api/v1/person/";


    @Test
    void getPersonDetails2000OK() {

        var name = "badri";

        stubFor(get(urlPathEqualTo("/age")).withQueryParam("name", equalTo(name))
                .willReturn(aResponse().withHeader("Content-Type", "application/json").withBodyFile("age.json")));
        stubFor(get(urlPathEqualTo("/gender")).withQueryParam("name", equalTo(name))
                .willReturn(aResponse().withHeader("Content-Type", "application/json").withBodyFile("gender.json")));
        stubFor(get(urlPathEqualTo("/nationality")).withQueryParam("name", equalTo(name))
                .willReturn(aResponse().withHeader("Content-Type", "application/json").withBodyFile("nationality.json")));

        webTestClient
                .get()
                .uri(URL_PERSON_DETAILS.concat(name))
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBody(Person.class)
                .consumeWith(personEntityExchangeResult -> {
                    var person = personEntityExchangeResult.getResponseBody();
                    assertThat(person.getAge()).isEqualTo(14);
                    assertThat(person.getGender()).isEqualTo("male");
                    assertThat(person.getNationality()).isEqualTo("ABC");
                });

    }

    @Test
    void getPersonDetailsNotFoundWhenNameIsANumber() {

        var name = "1111";

        stubFor(get(urlPathEqualTo("/age")).withQueryParam("name", equalTo(name))
                .willReturn(aResponse().withHeader("Content-Type", "application/json").withBodyFile("age_null.json")));
        stubFor(get(urlPathEqualTo("/gender")).withQueryParam("name", equalTo(name))
                .willReturn(aResponse().withHeader("Content-Type", "application/json").withBodyFile("gender_null.json")));
        stubFor(get(urlPathEqualTo("/nationality")).withQueryParam("name", equalTo(name))
                .willReturn(aResponse().withHeader("Content-Type", "application/json").withBodyFile("nationality_null.json")));

        webTestClient
                .get()
                .uri(URL_PERSON_DETAILS.concat(name))
                .exchange()
                .expectStatus()
                .isNotFound();

    }

    @Test
    void getPersonDetailsAgeServiceInvalidNameReturns422() {

        var name = "invalidname";

        stubFor(get(urlPathEqualTo("/age")).withQueryParam("name", equalTo(name)).willReturn(aResponse().withStatus(422).withBodyFile("errorInvalidName.json")));
        stubFor(get(urlPathEqualTo("/gender")).withQueryParam("name", equalTo(name))
                .willReturn(aResponse().withHeader("Content-Type", "application/json").withBodyFile("gender.json")));
        stubFor(get(urlPathEqualTo("/nationality")).withQueryParam("name", equalTo(name))
                .willReturn(aResponse().withHeader("Content-Type", "application/json").withBodyFile("nationality.json")));

        webTestClient
                .get()
                .uri(URL_PERSON_DETAILS.concat(name))
                .exchange()
                .expectStatus()
                .is4xxClientError()
                .expectBody(String.class)
                .consumeWith(resposne -> {
                    var response = resposne.getResponseBody();
                    assertThat(response).contains("Invalid 'name' parameter");
                });

    }

    @Test
    void getPersonDetailsGenderServiceInvalidNameReturns422() {

        var name = "invalidname";

        stubFor(get(urlPathEqualTo("/age")).withQueryParam("name", equalTo(name))
                .willReturn(aResponse().withHeader("Content-Type", "application/json").withBodyFile("age.json")));
        stubFor(get(urlPathEqualTo("/gender")).withQueryParam("name", equalTo(name)).willReturn(aResponse().withStatus(422).withBodyFile("errorInvalidName.json")));
        stubFor(get(urlPathEqualTo("/nationality")).withQueryParam("name", equalTo(name))
                .willReturn(aResponse().withHeader("Content-Type", "application/json").withBodyFile("nationality.json")));

        webTestClient
                .get()
                .uri(URL_PERSON_DETAILS.concat(name))
                .exchange()
                .expectStatus()
                .is4xxClientError();

    }

    @Test
    void getPersonDetailsNationalityServiceInvalidNameReturns422() {

        var name = "invalidname";

        stubFor(get(urlPathEqualTo("/age")).withQueryParam("name", equalTo(name))
                .willReturn(aResponse().withHeader("Content-Type", "application/json").withBodyFile("age.json")));
        stubFor(get(urlPathEqualTo("/gender")).withQueryParam("name", equalTo(name))
                .willReturn(aResponse().withHeader("Content-Type", "application/json").withBodyFile("gender.json")));
        stubFor(get(urlPathEqualTo("/nationality")).withQueryParam("name", equalTo(name)).willReturn(aResponse().withStatus(422).withBodyFile("errorInvalidName.json")));

        webTestClient
                .get()
                .uri(URL_PERSON_DETAILS.concat(name))
                .exchange()
                .expectStatus()
                .is4xxClientError();

    }


    @Test
    void getPersonDetailsAllServicesReturn422() {

        var name = "invalidname";

        stubFor(get(urlPathEqualTo("/name")).withQueryParam("name", equalTo(name)).willReturn(aResponse().withStatus(422).withBodyFile("errorInvalidName.json")));
        stubFor(get(urlPathEqualTo("/gender")).withQueryParam("name", equalTo(name)).willReturn(aResponse().withStatus(422).withBodyFile("errorInvalidName.json")));
        stubFor(get(urlPathEqualTo("/nationality")).withQueryParam("name", equalTo(name)).willReturn(aResponse().withStatus(422).withBodyFile("errorInvalidName.json")));

        webTestClient
                .get()
                .uri(URL_PERSON_DETAILS.concat(name))
                .exchange()
                .expectStatus()
                .is4xxClientError();

    }

    // Test Retry

    @Test
    void testWhenAServicethrows500() {
        var name = "badri";

        stubFor(get(urlPathEqualTo("/age")).withQueryParam("name", equalTo(name)).willReturn(aResponse().withStatus(500).withBodyFile("AgeServerError.json")));
        stubFor(get(urlPathEqualTo("/gender")).withQueryParam("name", equalTo(name))
                .willReturn(aResponse().withHeader("Content-Type", "application/json").withBodyFile("gender.json")));
        stubFor(get(urlPathEqualTo("/nationality")).withQueryParam("name", equalTo(name))
                .willReturn(aResponse().withHeader("Content-Type", "application/json").withBodyFile("nationality.json")));

        webTestClient
                .get()
                .uri(URL_PERSON_DETAILS.concat(name))
                .exchange()
                .expectStatus()
                .is5xxServerError()
                .expectBody(String.class)
                .consumeWith(err -> {
                    System.out.println("Error *** " + err.getResponseBody());
                    var servError = err.getResponseBody();
                    assertThat(servError).contains("Person Details Server Error::Internal server error");
                });


    }

    @Test
    void testRetryWhenAServicethrows500() {
        var name = "badri";

        stubFor(get(urlPathEqualTo("/age")).withQueryParam("name", equalTo(name)).willReturn(aResponse().withStatus(500).withBodyFile("AgeServerError.json")));
        stubFor(get(urlPathEqualTo("/gender")).withQueryParam("name", equalTo(name))
                .willReturn(aResponse().withHeader("Content-Type", "application/json").withBodyFile("gender.json")));
        stubFor(get(urlPathEqualTo("/nationality")).withQueryParam("name", equalTo(name))
                .willReturn(aResponse().withHeader("Content-Type", "application/json").withBodyFile("nationality.json")));

        webTestClient
                .get()
                .uri(URL_PERSON_DETAILS.concat(name))
                .exchange()
                .expectStatus()
                .is5xxServerError()
                .expectBody(String.class)
                .consumeWith(err -> {
                    var servError = err.getResponseBody();
                    assertThat(servError).contains("Person Details Server Error::Internal server error");
                });

        verify(retryAttempts + 1, getRequestedFor(urlEqualTo("/age?name=badri")));

    }


}
