package com.iag.reactive.controller;


import com.iag.reactive.dto.Person;
import com.iag.reactive.service.PersonDetailsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping ("/api/v1")
public class PersonDetailsController {

    private PersonDetailsService personService;

    public PersonDetailsController(PersonDetailsService personService) {
        this.personService = personService;
    }

    @GetMapping("/person/{name}")
    public Mono<Person> getPersonDetails(@PathVariable String name){
        return personService.getDetails(name);

    }
}
