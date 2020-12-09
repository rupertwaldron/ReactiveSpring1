package com.ruppyrup.reactivespring.fluxandmonoplayground;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;

public class FluxAndMonoFactoryTest {
    @Test
    public void fluxUsingIterable() {
        List<String> names = Arrays.asList("Adam", "Betty", "Carl");
        Flux<String> namesFlux = Flux.fromIterable(names)
                .log();

        StepVerifier.create(namesFlux)
                .expectNext("Adam", "Betty", "Carl")
                .verifyComplete();
    }

    @Test
    public void fluxUsingArray() {
        String[] names = new String[]{"Adam", "Betty", "Carl"};

        Flux<String> namesFlux = Flux.fromArray(names)
                .log();

        StepVerifier.create(namesFlux)
                .expectNext("Adam", "Betty", "Carl")
                .verifyComplete();
    }

    @Test
    public void fluxUsingStream() {
        List<String> names = Arrays.asList("Adam", "Betty", "Carl");
        Flux<String> namesFlux = Flux.fromStream(names.stream());

        StepVerifier.create(namesFlux)
                .expectNext("Adam", "Betty", "Carl")
                .verifyComplete();
    }

    @Test
    public void monoUsingJustOrEmpty() {
        Mono<String> mono = Mono.justOrEmpty(null);

        StepVerifier.create(mono.log())
                .verifyComplete();
    }

    @Test
    public void fluxUsingRange() {
        Flux<Integer> integerFlux = Flux.range(1,5).log();

        StepVerifier.create(integerFlux)
                .expectNext(1,2,3,4,5)
                .verifyComplete();
    }
}
