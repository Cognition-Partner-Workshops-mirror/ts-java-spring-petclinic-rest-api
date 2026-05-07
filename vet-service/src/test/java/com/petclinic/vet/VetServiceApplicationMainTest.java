package com.petclinic.vet;

import org.junit.jupiter.api.Test;

class VetServiceApplicationMainTest {

    @Test
    void mainMethodRuns() {
        VetServiceApplication.main(new String[]{"--spring.profiles.active=test"});
    }
}
