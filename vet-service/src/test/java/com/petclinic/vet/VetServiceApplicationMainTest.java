package com.petclinic.vet;

import org.junit.jupiter.api.Test;

class VetServiceApplicationMainTest {

    @Test
    void main_runsWithoutException() {
        VetServiceApplication.main(new String[]{
            "--spring.profiles.active=test",
            "--server.port=0"
        });
    }
}
