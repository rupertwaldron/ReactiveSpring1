package com.ruppyrup.reactivespring.fluxandmonoplayground;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.ConnectableFlux;
import reactor.core.publisher.Flux;

import java.time.Duration;

public class HotAndColdPublisher {

    @Test
    public void coldPublisherTest() throws InterruptedException {
        // COLD: both subscribers receive all the elements
        Flux<String> flux = Flux.just("A", "B", "C", "D", "E")
                .delayElements(Duration.ofSeconds(1));

        flux.subscribe(s -> System.out.println("Subscriber 1 : " + s));

        Thread.sleep(2000);

        flux.subscribe(s -> System.out.println("Subscriber 2 : " + s));

        Thread.sleep(4000);
    }

    @Test
    public void hotPublisherTest() throws InterruptedException {
        // COLD: both subscribers receive all the elements
        Flux<String> flux = Flux.just("A", "B", "C", "D", "E")
                .delayElements(Duration.ofSeconds(1));

        ConnectableFlux<String> connectableFlux = flux.publish();
        connectableFlux.connect();

        connectableFlux.subscribe(s -> System.out.println("Subscriber 1 : " + s));
        Thread.sleep(3000);

        connectableFlux.subscribe(s -> System.out.println("Subscriber 2 : " + s));
        // does not get A and B only after has started receives all the elements
        Thread.sleep(4000);
    }
}
