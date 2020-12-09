package com.ruppyrup.reactivespring.fluxandmonoplayground;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;



public class FluxAndMonoCombineTest {


    @Test
    public void combineUsingMerge() {
        Flux<String> flux1 = Flux.just("A", "B", "C");
        Flux<String> flux2 = Flux.just("D", "E", "F");

        Flux<String> mergedFlux = Flux.merge(flux1, flux2).log();
        // don't always get these in the correct order
        // use concat to get the correct order

        StepVerifier.create(mergedFlux)
                .expectSubscription()// don't need this
                .expectNext("A", "B", "C", "D", "E", "F")
                .verifyComplete();

        Flux<String> concatFlux = Flux.concat(flux1, flux2).log();
        // maintains order of elements
        StepVerifier.create(concatFlux)
                .expectSubscription()// don't need this
                .expectNext("A", "B", "C", "D", "E", "F")
                .verifyComplete();


        Flux<String> zipFlux = Flux.zip(flux1, flux2, (t1, t2) -> {
            return t1.concat(t2); //AD, BE, CF
            });

        StepVerifier.create(zipFlux.log())
                .expectSubscription()// don't need this
                .expectNext("AD", "BE", "CF")
                .verifyComplete();
    }
}
