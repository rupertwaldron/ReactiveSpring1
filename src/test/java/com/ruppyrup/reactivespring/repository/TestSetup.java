package com.ruppyrup.reactivespring.repository;

import com.ruppyrup.reactivespring.document.Item;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import reactor.core.publisher.Flux;

import java.util.Arrays;
import java.util.List;

@SpringBootTest
@ActiveProfiles("test")
public class TestSetup {

    @Autowired
    ItemReactiveRepository itemReactiveRepository;

    List<Item> itemList = Arrays.asList(
            new Item("Samsung TV", 400.0),
            new Item("LG TV", 420.0),
            new Item("Apple Watch", 299.99),
            new Item("Beats Headphones", 149.99),
            new Item("Bose Headphones", 149.99));


    @BeforeEach
    public void setUp() {
        itemReactiveRepository.deleteAll()
                .thenMany(Flux.fromIterable(itemList))
                .flatMap(itemReactiveRepository::save)
                .doOnNext(item -> System.out.println("Instered item is : " + item))
                .blockLast(); // wait until all data is loaded before tests
    }
}
