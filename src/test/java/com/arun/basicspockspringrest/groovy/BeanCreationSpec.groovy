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
