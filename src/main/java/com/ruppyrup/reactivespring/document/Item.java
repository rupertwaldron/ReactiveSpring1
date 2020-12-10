package com.ruppyrup.reactivespring.document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table("items")
public class Item {
    @Id
    private Integer id;
    private String description;
    private Double price;

    public Item(String description, Double price) {
        this.description = description;
        this.price = price;
    }
}
