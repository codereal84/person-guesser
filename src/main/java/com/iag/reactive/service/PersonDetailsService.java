package com.iag.reactive.service;


import com.iag.reactive.client.RestClient;
import com.iag.reactive.dto.Country;
import com.iag.reactive.dto.Person;
import com.iag.reactive.exceptions.PersonDetailsClientException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@Slf4j
public class PersonDetailsService {

    private RestClient restClient;

    public PersonDetailsService(RestClient restClient) {
        this.restClient = restClient;
    }

    public Mono<Person> getDetails(String name) {
        var ageMono = restClient.getAge(name);
        var genderMono = restClient.getGender(name);
        var nationalityMono = restClient.getNationality(name);
        return Mono.zip(ageMono, genderMono, nationalityMono)
                .flatMap(t3 -> {
                    var age = t3.getT1().getAge();
                    var gender = t3.getT2().getGender();
                    var countryList = t3.getT3().getCountry();
                    if (countryList.size() == 0 || age == null || gender == null)
                        return Mono.error(new PersonDetailsClientException(errorMessage(age, gender, countryList), HttpStatus.NOT_FOUND.value()));
                    return Mono.just(new Person(t3.getT1().getAge(), t3.getT2().getGender(), t3.getT3().getCountry().get(0).getCountry_id()));
                });
    }

    private String errorMessage(Integer age, String gender, List<Country> countryList) {
        String nationality = countryList.size() == 0 ? null : countryList.get(1).getCountry_id();
        return "Person details could not be fully extracted as one or many attributes have empty/null values".concat("(Age: " + age).concat(",Gender: " + gender).concat(",Nationalility: " + nationality);
    }



}
