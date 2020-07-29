package com.arun.basicspockspringrest.controller;

import com.arun.basicspockspringrest.model.Profile;
import com.arun.basicspockspringrest.service.HomeService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author arun on 7/28/20
 */
@Component
public class HomeController {

    private final HomeService homeService;

    public HomeController(HomeService homeService) {
        this.homeService = homeService;
    }

    public String salutation() {
        return homeService.salutation();
    }

    public String health() throws JsonProcessingException {
        return homeService.checkStatusOfMockService();
    }

    public List<Profile> profiles() {
        return homeService.getProfiles();
    }

    public Profile profile(String uuid) {
        return homeService.getProfile(uuid);
    }
}
