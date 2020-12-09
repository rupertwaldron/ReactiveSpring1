package com.ruppyrup.reactivespring.repository;

import com.ruppyrup.reactivespring.document.Item;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface ItemReactiveRepository extends ReactiveCrudRepository<Item, Integer> {

    // Use $ brew services restart mongodb
    @Query("SELECT * FROM item WHERE description = :description")
    Flux<Item> findByDescription(String description);

}
