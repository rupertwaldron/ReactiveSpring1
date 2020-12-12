package com.ruppyrup.reactivespring.repository;


import com.ruppyrup.reactivespring.document.Item;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;

@SpringBootTest
@ActiveProfiles("test")
public class ItemReactiveRepositorytest {

    @Autowired
    ItemReactiveRepository itemReactiveRepository;

    @Autowired
    DatabaseClient databaseClient;

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

    @Test
    @Order(1)
    public void getAllItems() {
        StepVerifier.create(itemReactiveRepository.findAll())
        .expectSubscription()
        .expectNextCount(5)
        .verifyComplete();
    }

    @Test
    public void findItemByDescription() {
        StepVerifier.create(itemReactiveRepository.findByDescription("Beats Headphones"))
                .expectSubscription()
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    public void saveItem() {

        Item item = new Item("Google Home Mini", 30.00);
        Mono<Item> savedItem = itemReactiveRepository.save(item);

        StepVerifier.create(savedItem.log("saved item: "))
                .expectSubscription()
                .expectNextMatches(item1 -> item1.getDescription().equals("Google Home Mini"))
                .verifyComplete();
    }


    @Test
    public void updateItem() {
        double newPrice = 520.00;

        Mono<Item> updatedItem = itemReactiveRepository.findByDescription("Samsung TV")
                .map(item -> {
                    item.setPrice(newPrice);
                    return item;
                })
                .flatMap(item ->
                    itemReactiveRepository.save(item)
                );

        StepVerifier.create(updatedItem)
                .expectSubscription()
                .expectNextMatches(item ->   item.getPrice().intValue() == 520)
                .verifyComplete();

    }

    @Test
    @DisplayName("Deleted Item Test")
    public void deleteItemByDescription() {
        Mono<Void> deletedItem = itemReactiveRepository.findByDescription("Apple Watch") //Mono<Item>
                .map(Item::getId)
                .flatMap(id -> itemReactiveRepository.deleteById(id));

        StepVerifier.create(deletedItem)
                .expectSubscription()
                .verifyComplete();

        StepVerifier.create(itemReactiveRepository.findAll().log("The new item list: "))
                .expectSubscription()
                .expectNextCount(4)
                .verifyComplete();
    }

}
