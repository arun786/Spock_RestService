package com.arun.basicspockspringrest.service;

import com.arun.basicspockspringrest.model.Profile;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.List;

/**
 * @author arun on 7/28/20
 */
public interface HomeService {
    String salutation();

    String checkStatusOfMockService() throws JsonProcessingException;

    List<Profile> getProfiles();

    Profile getProfile(String uuid);
}
