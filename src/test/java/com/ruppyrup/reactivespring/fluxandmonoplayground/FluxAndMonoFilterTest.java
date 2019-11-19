package com.ruppyrup.reactivespring.fluxandmonoplayground;

import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;

public class FluxAndMonoFilterTest {
    @Test
    public void fluxUsingIterable() {
        List<String> names = Arrays.asList("Adam", "Betty", "Carl", "Andy", "Andrew");
        Flux<String> namesFlux = Flux.fromIterable(names)
                .filter(s -> s.startsWith("A") && s.length() > 4)
                .log();

        StepVerifier.create(namesFlux)
                .expectNext("Andrew")
                .verifyComplete();

    }
}
