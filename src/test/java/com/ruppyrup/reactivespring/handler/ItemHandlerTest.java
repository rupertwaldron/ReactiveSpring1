package com.ruppyrup.reactivespring.handler;

import com.ruppyrup.reactivespring.document.Item;
import com.ruppyrup.reactivespring.repository.ItemReactiveRepository;
import com.ruppyrup.reactivespring.setup.TestSetup;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;

import static com.ruppyrup.reactivespring.constants.ItemConstants.ITEM_FUNCTIONAL_END_POINT_V1;


@AutoConfigureWebTestClient
public class ItemHandlerTest extends TestSetup {

    @Autowired
    WebTestClient webTestClient;

    @Autowired
    ItemReactiveRepository itemReactiveRepository;

    @Test
    public void getAllItems() {
        webTestClient.get().uri(ITEM_FUNCTIONAL_END_POINT_V1)
                .exchange()
                .expectStatus()
                .isOk()
                .expectHeader()
                .contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(Item.class)
                .hasSize(4)
                .consumeWith(response -> {
                    List<Item> items1 = response.getResponseBody();
                    items1.forEach(item -> Assertions.assertNotNull(item.getId()));
                });
    }

    @Test
    public void getAllItems2() {
    Flux<Item> itemsFlux = webTestClient.get().uri(ITEM_FUNCTIONAL_END_POINT_V1)
                .exchange()
                .expectStatus()
                .isOk()
                .expectHeader()
                .contentType(MediaType.APPLICATION_JSON)
                .returnResult(Item.class)
                .getResponseBody();

        StepVerifier.create(itemsFlux.log("value from network: "))
                .expectNextCount(4)
                .verifyComplete();
    }

    @Test
    public void getOneItem() {
        webTestClient
                .get()
                .uri(ITEM_FUNCTIONAL_END_POINT_V1.concat("/{id}"), 1)
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
                .uri(ITEM_FUNCTIONAL_END_POINT_V1.concat("/{id}"), 8)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    public void createItem() {

        Item item = new Item("Monkey", 27.50);

        webTestClient
                .post()
                .uri(ITEM_FUNCTIONAL_END_POINT_V1)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(item), Item.class)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.id").isNotEmpty()
                .jsonPath("$.description").isEqualTo("Monkey")
                .jsonPath("$.price").isEqualTo(27.50)
                .consumeWith(System.out::println);
    }

    @Test
    public void deleteOneItem() {
        webTestClient
                .delete()
                .uri(ITEM_FUNCTIONAL_END_POINT_V1.concat("/{id}"), 5)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(Void.class);

        webTestClient
                .get()
                .uri(ITEM_FUNCTIONAL_END_POINT_V1.concat("/{id}"), 5)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    public void updateItem() {

        Item item = new Item("Monkey", 27.50);

        webTestClient
                .put()
                .uri(ITEM_FUNCTIONAL_END_POINT_V1.concat("/{id}"), 1)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(item), Item.class)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(1)
                .jsonPath("$.description").isEqualTo("Monkey")
                .jsonPath("$.price").isEqualTo(27.50)
                .consumeWith(System.out::println);

        webTestClient
                .get()
                .uri(ITEM_FUNCTIONAL_END_POINT_V1.concat("/{id}"), 1)
                .exchange()
                .expectStatus().isOk();
    }
}
