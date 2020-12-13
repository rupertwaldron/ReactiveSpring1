package com.ruppyrup.reactivespring.controller.v1;


import com.ruppyrup.reactivespring.document.Item;
import com.ruppyrup.reactivespring.repository.ItemReactiveRepository;
import com.ruppyrup.reactivespring.setup.TestSetup;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static com.ruppyrup.reactivespring.constants.ItemConstants.ITEM_END_POINT_V1;

@AutoConfigureWebTestClient
public class ItemControllerTest extends TestSetup {

    @Autowired
    WebTestClient webTestClient;

    @Autowired
    ItemReactiveRepository itemReactiveRepository;

    @Test
    public void getAllItems() {
        webTestClient.get().uri(ITEM_END_POINT_V1)
                .exchange()
                .expectStatus()
                .isOk()
                .expectHeader()
                .contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(Item.class)
                .hasSize(5)
                .consumeWith(response -> {
                    List<Item> items1 = response.getResponseBody();
                    items1.forEach(item -> Assertions.assertTrue(item.getId() != null));
                });
    }

    @Test
    public void getAllItems2() {
        Flux<Item> itemsFlux = webTestClient.get().uri(ITEM_END_POINT_V1)
                .exchange()
                .expectStatus()
                .isOk()
                .expectHeader()
                .contentType(MediaType.APPLICATION_JSON)
                .returnResult(Item.class)
                .getResponseBody();

        StepVerifier.create(itemsFlux.log("value from network: "))
                .expectNextCount(5)
                .verifyComplete();
    }

    @Test
    public void getOneItem() {
    webTestClient
            .get()
            .uri(ITEM_END_POINT_V1.concat("/{id}"), 1)
            .exchange()
            .expectStatus()
            .isOk()
            .expectBody()
            .jsonPath("$.price", 400.00);
    }

    @Test
    public void getOneItem_notFound() {
        webTestClient
                .get()
                .uri(ITEM_END_POINT_V1.concat("/{id}"), "1000000")
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    public void createOneItem() {

        Item item = new Item("iPhone XS", 1199.99);

        webTestClient
                .post()
                .uri(ITEM_END_POINT_V1.concat("/{id}"), "ABC")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(item), Item.class)
                .exchange() // body posted
                .expectStatus()
                .isCreated()
                .expectBody()
                .jsonPath("$.id").isNotEmpty()
                .jsonPath("$.description").isEqualTo("iPhone XS")
                .jsonPath("$.price").isEqualTo(1199.99);
    }

    @Test
    public void deleteOneItem() {
        webTestClient
                .delete()
                .uri(ITEM_END_POINT_V1.concat("/{id}"), 3)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(Void.class);

        webTestClient
                .get()
                .uri(ITEM_END_POINT_V1.concat("/{id}"), 3)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    public void updateOneItem() {

        Item item = new Item(1, "Samsund TV 5K", 1495.99);

        webTestClient
                .put()
                .uri(ITEM_END_POINT_V1.concat("/{id}"), 1)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(item), Item.class)
                .exchange() // body posted
                .expectStatus()
                .isOk()
                .expectBody()
                .jsonPath("$.id").isNotEmpty()
                .jsonPath("$.description").isEqualTo("Samsund TV 5K")
                .jsonPath("$.price").isEqualTo(1495.99);
    }

    @Test
    public void updateOneItem_notFound() {

        Item item = new Item(1, "Samsund TV 5K", 1495.99);

        webTestClient
                .put()
                .uri(ITEM_END_POINT_V1.concat("/{id}"), 6)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(item), Item.class)
                .exchange() // body posted
                .expectStatus()
                .isNotFound();
    }
}
