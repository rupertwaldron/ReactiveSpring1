package com.ruppyrup.reactivespring.repository;


import com.ruppyrup.reactivespring.document.Item;
import com.ruppyrup.reactivespring.setup.TestSetup;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.r2dbc.core.DatabaseClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;


public class ItemReactiveRepositorytest extends TestSetup {

    @Autowired
    ItemReactiveRepository itemReactiveRepository;

    @Autowired
    DatabaseClient databaseClient;

    @Test
    public void getAllItems() {
        StepVerifier.create(itemReactiveRepository.findAll())
        .expectSubscription()
        .expectNextCount(6)
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
                .expectNextCount(5)
                .verifyComplete();
    }

}
