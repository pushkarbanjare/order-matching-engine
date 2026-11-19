package com.pushkar.ordermatchingengine.dto;

import java.math.BigDecimal;

import com.pushkar.ordermatchingengine.model.OrderSide;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter 
@Setter 
public class OrderRequest {

    @NotBlank(message = "symbol is required")
    private String symbol;

    @NotNull(message = "side is required")
    private OrderSide side;

    @NotNull @Positive(message = "price must be positive")
    private BigDecimal price;

    @NotNull @Positive(message = "quantity must be positive")
    private BigDecimal quantity;
}
