package com.arun.basicspockspringrest.service;

import com.arun.basicspockspringrest.config.UrlConfig;
import com.arun.basicspockspringrest.model.Profile;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author arun on 7/28/20
 */
@Service
public class HomeServiceImpl implements HomeService {

    private final WebClient webClient;
    private final UrlConfig urlConfig;

    @Autowired
    public HomeServiceImpl(WebClient webClient, UrlConfig urlConfig) {
        this.webClient = webClient;
        this.urlConfig = urlConfig;
    }

    @Override
    public String salutation() {
        return "Hello World dear";
    }

    @Override
    public String checkStatusOfMockService() throws JsonProcessingException {
        Health mockHealth = Health.down().build();
        Mono<JsonNode> jsonNodeMono = webClient.method(HttpMethod.GET).uri(URI.create(urlConfig.getMockActuatorUrl()))
                .retrieve().bodyToMono(JsonNode.class);
        JsonNode jsonNode = jsonNodeMono.blockOptional().orElseThrow();
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, String> valueStatus = objectMapper.readValue(String.valueOf(jsonNode), new TypeReference<>() {
        });


        if (valueStatus.get("status").equals("UP")) {
            mockHealth = Health.up().build();
        }
        return mockHealth.getStatus().getCode();
    }

    @Override
    public List<Profile> getProfiles() {
        Mono<ResponseEntity<List<Profile>>> responseEntityMono = webClient.method(HttpMethod.GET).uri(URI.create(urlConfig.getMockUrl()))
                .retrieve().toEntityList(Profile.class);
        List<Profile> body = Objects.requireNonNull(responseEntityMono.block()).getBody();
        System.out.println("Arun " + body);
        return body;

    }

    @Override
    public Profile getProfile(String uuid) {
        Mono<ResponseEntity<Profile>> responseEntityMono = webClient.method(HttpMethod.GET).uri(URI.create(urlConfig.getMockUrl() + "/"+ uuid))
                .retrieve().toEntity(Profile.class);
        Profile body = Objects.requireNonNull(responseEntityMono.block()).getBody();
        System.out.println(body);
        return body;
    }
}
