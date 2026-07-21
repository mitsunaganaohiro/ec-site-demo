package com.example.demo.dto;

import lombok.Data;

@Data
public class CartItemDto {

    private Integer cartItemId;
    private Integer productId;
    private String productName;
    private Integer price;
    private Integer quantity;
    private Integer subTotal;
}
