package com.ruppyrup.reactivespring.repository;

import com.ruppyrup.reactivespring.document.Item;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface ItemReactiveRepository extends ReactiveCrudRepository<Item, Integer> {

    // Use $ brew services restart mongodb
    @Query("SELECT * FROM items WHERE description =:description LIMIT 1")
    Mono<Item> findByDescription(String description);

    @Query("DELETE from items")
    Mono<Void> deleteAll();

}
