# Spock framework used to test Spring boot microservices with gradle

The below repo uses spock framework to test rest microservice mock service.

Below are the details of build.gradle

    plugins {
        id 'org.springframework.boot' version "${springBootVersion}"
        id 'io.spring.dependency-management' version '1.0.9.RELEASE'
        id 'java'
        id 'groovy'
    }
    
    defaultTasks('clean', 'test')
    
    group = 'com.arun'
    version = '0.0.1-SNAPSHOT'
    sourceCompatibility = '11'
    
    configurations {
        compileOnly {
            extendsFrom annotationProcessor
        }
    }
    
    repositories {
        mavenCentral()
    }
    
    dependencies {
        implementation 'org.springframework.boot:spring-boot-starter-web'
        compileOnly 'org.projectlombok:lombok'
        developmentOnly 'org.springframework.boot:spring-boot-devtools'
        annotationProcessor 'org.springframework.boot:spring-boot-configuration-processor'
        annotationProcessor 'org.projectlombok:lombok'
        testImplementation('org.springframework.boot:spring-boot-starter-test') {
            exclude group: 'org.junit.vintage', module: 'junit-vintage-engine'
        }
    
        compile 'org.springframework.boot:spring-boot-starter-actuator'
    
        testCompile 'org.codehaus.groovy:groovy-all:2.4.11'
    
        //reactive
        implementation 'org.springframework.boot:spring-boot-starter-webflux'
        compile 'org.projectreactor:reactor-spring:1.0.1.RELEASE'
    
    
        //Spock configuration
        testCompile "org.spockframework:spock-core:${spookVersion}"
        testCompile "org.spockframework:spock-spring:${spookVersion}"
    
    }
    
    //this is required to enable the groovy test to be run
    sourceSets {
        test {
            groovy {
                srcDirs = ["src/test/java/com/arun/basicspockspringrest/groovy"]
            }
        }
    }
    
    
The groovy file where the test cases are written

    package com.arun.basicspockspringrest.groovy
    
    import com.arun.basicspockspringrest.controller.HomeController
    import com.arun.basicspockspringrest.model.Profile
    import org.springframework.beans.factory.annotation.Autowired
    import org.springframework.boot.test.context.SpringBootTest
    import spock.lang.Specification
    import spock.lang.Stepwise
    
    /**
     * @author arun on 7/28/20
     */
    
    @SpringBootTest
    @Stepwise
    class BeanCreationSpec extends Specification {
    
        @Autowired
        private HomeController homeController;
        private List<Profile> profiles;
    
    
        def "Assert bean creation"() {
            expect: "bean creation successful"
            homeController != null
    
        }
    
        def "Assert that salutation returns hello world"() {
            expect: "Hello World"
            homeController.salutation().toString() == "Hello World dear"
        }
    
    
        def "Check the health of Mock Service"() {
            expect: "Up"
            homeController.health().toString() == "UP"
        }
    
        def "get the list of the profiles and use the uuid to get the profile"() {
            expect: "size greater than 0"
            when:
            profiles = homeController.profiles()
            then:
            profiles.size() > 0
            then: "get a profile based on the uuid"
            when:
            def profile = homeController.profile(profiles.get(0).uuid)
            then:
            println profile
            profile.getEmail() == profiles.get(0).getEmail()
        }
    }


This is the web client which is used to call the end point exposed in mock service.

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


The Web client configuration

    package com.arun.basicspockspringrest.client;
    
    import io.netty.channel.ChannelOption;
    import io.netty.handler.timeout.ReadTimeoutHandler;
    import io.netty.handler.timeout.WriteTimeoutHandler;
    import org.springframework.context.annotation.Bean;
    import org.springframework.context.annotation.Configuration;
    import org.springframework.http.client.reactive.ReactorClientHttpConnector;
    import org.springframework.web.reactive.function.client.WebClient;
    import reactor.netty.http.client.HttpClient;
    import reactor.netty.tcp.TcpClient;
    
    /**
     * @author arun on 7/28/20
     */
    @Configuration
    public class WebClientConfig {
        @Bean
        public WebClient webClient(WebClient.Builder webClientBuilder) {
    
            TcpClient tcpClient = TcpClient.create()
                    .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 2_000)
                    .doOnConnected(connection -> connection.addHandlerLast(new ReadTimeoutHandler(2))
                            .addHandlerLast(new WriteTimeoutHandler(2)));
    
            return webClientBuilder
                    .clientConnector(new ReactorClientHttpConnector(HttpClient.from(tcpClient))).build();
        }
    }


The api configuration for url ( note :: webservice rest service should be up for all the test to pass)
    
    client:
      url:
        mockUrl: "http://localhost:8443/v1/profiles"
        mockActuatorUrl: "http://localhost:8443/actuator/health"


when you run ./gradlew 

on the terminal, a report is generated as below

![report generated](https://github.com/arun786/basicspockspringrest/blob/master/src/main/resources/image/Screen%20Shot%202020-07-28%20at%209.50.38%20PM.png)

