package com.nexient.orders.data.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Builder
@Data
@Document("ordertable")
@AllArgsConstructor
@NoArgsConstructor
public class Order {

    @Id
    private String id;
    private String item;
    private String qty;
    private String price;

}

