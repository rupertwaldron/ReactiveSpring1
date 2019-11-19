package com.ruppyrup.reactivespring.fluxandmonoplayground;

import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;

import static reactor.core.scheduler.Schedulers.parallel;

public class FluxAndMonoTransformTest {

    @Test
    public void transformFluxUsingMap() {
        List<String> names = Arrays.asList("Adam", "Betty", "Carl");
        Flux<String> namesFlux = Flux.fromIterable(names)
                .map(s -> s.toUpperCase())
                .log();

        StepVerifier.create(namesFlux)
                .expectSubscription()
                .expectNext("ADAM", "BETTY", "CARL")
                .verifyComplete();

    }

    @Test
    public void transformFluxUsingMap_Length() {
        List<String> names = Arrays.asList("Adam", "Betty", "Carl");
        Flux<Integer> namesFlux = Flux.fromIterable(names)
                .map(s -> s.length())
                .repeat(1)
                .log();

        StepVerifier.create(namesFlux)
                .expectNext(4, 5, 4, 4, 5, 4)
                .verifyComplete();

    }

    @Test
    public void transformFluxUsingFlatMap() {
        String[] letters = {"A", "B", "C", "D", "E"};

        Flux<String> lettersFlux = Flux.fromArray(letters)
                .flatMap(s -> {
                    return Flux.fromIterable(convertToList(s));
                    // A -> List[A, newValue], B -> List[B, newValue]
                })
                .log();

        StepVerifier.create(lettersFlux)
                .expectNextCount(10)
                .verifyComplete();

    }

    private List<String> convertToList(String s) {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return Arrays.asList(s, "newValue");
    }

    @Test
    public void transformFluxUsingFlatMap_usingParallel() {
        String[] letters = {"A", "B", "C", "D", "E"};

        Flux<String> lettersFlux = Flux.fromArray(letters)
                .window(2) // Flux<Flux<String>> -> (A, B), (C, D), (E,)
                .flatMap(s ->
                    s.map(this::convertToList).subscribeOn(parallel())) //Flux<List<String>>
                .flatMap(s -> Flux.fromIterable(s)) // Flux<String>
                .log();

        StepVerifier.create(lettersFlux)
                .expectNextCount(10)
                .verifyComplete();

    }

    @Test
    public void transformFluxUsingFlatMap_parallel_maintain_order() {
        String[] letters = {"A", "B", "C", "D", "E"};

        Flux<String> lettersFlux = Flux.fromArray(letters)
                .window(2) // Flux<Flux<String>> -> (A, B), (C, D), (E,)
                .flatMapSequential(s -> // keeps the order
                        s.map(this::convertToList).subscribeOn(parallel())) //Flux<List<String>>
                .flatMap(s -> Flux.fromIterable(s)) // Flux<String>
                .log();

        StepVerifier.create(lettersFlux)
                .expectNextCount(10)
                .verifyComplete();

    }
}
