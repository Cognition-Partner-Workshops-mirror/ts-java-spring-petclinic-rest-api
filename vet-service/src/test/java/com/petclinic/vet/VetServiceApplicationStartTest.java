package com.petclinic.vet;

import org.junit.jupiter.api.Test;

class VetServiceApplicationStartTest {

    @Test
    void main_runsWithoutError() {
        VetServiceApplication.main(new String[]{"--spring.profiles.active=test", "--server.port=0"});
    }
}
