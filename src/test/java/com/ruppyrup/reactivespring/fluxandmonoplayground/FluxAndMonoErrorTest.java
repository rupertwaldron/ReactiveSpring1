package com.ruppyrup.reactivespring.fluxandmonoplayground;

import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.Duration;

public class FluxAndMonoErrorTest {

    @Test
    public void fluxErrorHandling() {

        Flux<String> stringFlux = Flux.just("A", "B", "C")
                .concatWith(Flux.error(new RuntimeException("Flux Error")))
                .concatWith(Flux.just("D"))
                .onErrorResume(e -> {
                    System.out.println("Exception is: " + e);
                    return Flux.just("Error block executed");
                });

        StepVerifier.create(stringFlux.log())
                .expectSubscription()
                .expectNext("A", "B", "C")
                //.expectError(RuntimeException.class)
                .expectNext("Error block executed")
                .verifyComplete();
    }

    @Test
    public void fluxErrorHandling_OnErrorReturn() {

        Flux<String> stringFlux = Flux.just("A", "B", "C")
                .concatWith(Flux.error(new RuntimeException("Flux Error")))
                .concatWith(Flux.just("D"))
                .onErrorReturn("Error Return");

        StepVerifier.create(stringFlux.log())
                .expectSubscription()
                .expectNext("A", "B", "C")
                //.expectError(RuntimeException.class)
                .expectNext("Error Return")
                .verifyComplete();
    }

    @Test
    public void fluxErrorHandling_OnErrorMap() {

        Flux<String> stringFlux = Flux.just("A", "B", "C")
                .concatWith(Flux.error(new RuntimeException("Flux Error")))
                .concatWith(Flux.just("D"))
                .onErrorMap(e -> new CustomException(e));

        StepVerifier.create(stringFlux.log())
                .expectSubscription()
                .expectNext("A", "B", "C")
                .expectError(CustomException.class)
                .verify();
    }

    @Test
    public void fluxErrorHandling_OnErrorMap_withRetry() {

        Flux<String> stringFlux = Flux.just("A", "B", "C")
                .concatWith(Flux.error(new RuntimeException("Flux Error")))
                .concatWith(Flux.just("D"))
                .onErrorMap(e -> new CustomException(e))
                //.retry(2); //retry twice
        .retryBackoff(2, Duration.ofSeconds(5));

        StepVerifier.create(stringFlux.log())
                .expectSubscription()
                .expectNext("A", "B", "C")
                .expectNext("A", "B", "C")
                .expectNext("A", "B", "C")
                .expectError(IllegalStateException.class)
                .verify();
    }

    private class CustomException extends Throwable {

        private String message;

        public CustomException(Throwable e) {
            this.message = e.getMessage();
        }

        @Override
        public String getMessage() {
            return message;
        }
    }
}
