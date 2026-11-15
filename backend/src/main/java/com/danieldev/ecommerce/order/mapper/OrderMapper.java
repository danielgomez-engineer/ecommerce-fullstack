package com.danieldev.ecommerce.order.mapper;

import com.danieldev.ecommerce.order.dto.OrderItemResponse;
import com.danieldev.ecommerce.order.dto.OrderResponse;
import com.danieldev.ecommerce.order.entity.Order;
import com.danieldev.ecommerce.order.entity.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    OrderResponse toResponse(Order order);

    @Mapping(source = "product.id", target = "productId")
    @Mapping(source = "product.name", target = "productName")
    OrderItemResponse toItemResponse(OrderItem item);
}