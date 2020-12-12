package com.ruppyrup.reactivespring.initialize;

import com.ruppyrup.reactivespring.document.Item;
import com.ruppyrup.reactivespring.repository.ItemReactiveRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.util.Arrays;
import java.util.List;

@Component
@Profile("!test") // don't want commandLineRunner running during test
public class ItemDataInitializer implements CommandLineRunner {

    @Autowired
    ItemReactiveRepository itemReactiveRepository;

    @Override
    public void run(String... args) throws Exception {
//        initalDataSetup();
    }

    private void initalDataSetup() {
        itemReactiveRepository.deleteAll()
                .thenMany(Flux.fromIterable(data()))
                .flatMap(itemReactiveRepository::save)
                .thenMany(itemReactiveRepository.findAll())
                .log()
                .subscribe(item -> System.out.println("Item inserted from Command Line Runner : " + item));
    }

    private List<Item> data() {
        return Arrays.asList(
                new Item("Samsung TV", 399.99),
                new Item("LG TV", 420.00),
                new Item("Apple Watch", 299.99),
                new Item("BMW M3", 540000.00),
                new Item("Beats Headphones", 149.99));
    }
}
