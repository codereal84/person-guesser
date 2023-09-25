# person-guesser
Spring Webflux project to implement a persons age, gender and nationality based on name
=======
## Person Age,Gender, Nationality Guesser


This assignment has been implemented with [Spring Boot WebFlux](https://docs.spring.io/spring/docs/current/spring-framework-reference/web-reactive.html)
app using Java.

This project:
* Uses annotated controllers 
* Contains a single GET endpoint to guess Person age, gender and nationality based on a name
* Uses the following services to get Age, Gender and Nationality
  1.	Age Service : https://agify.io/
  2. 	Gender Service: https://genderize.io/
  3. 	Nationality Service: https://nationalize.io/


## Assumptions
1. The service will only give a successful response if all the 3 services age, gender and nationality are successfull
2. If any of the 3 external services fail , the api to guess the person details will fail as well.

## Extrenal Libraries
1. Wiremock : Use this dependency to simulate different API responses/error codes for the 3 external services

## Running The App

Clone the app from the following Github Repository
```
https://github.com/codereal84/person-guesser
```

Ensure you have Java 8 or later.

Navigate to your project root and run the below commands
```
./mvnw clean package
java -jar target/persondetails-guess-service.jar
```


## Testing The Endpoints
Point your browser to `http://localhost:8080/api/v1/person/{name} or use `curl` in command line.

Example 1: name = badri
```
curl  http://localhost:8080/api/v1/person/badri
```

