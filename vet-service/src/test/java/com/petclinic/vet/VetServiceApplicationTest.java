package com.petclinic.vet;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class VetServiceApplicationTest {

    @Test
    void contextLoads() {
    }

    @Test
    void main_startsApplication() {
        VetServiceApplication.main(new String[]{"--spring.profiles.active=test"});
    }
}
