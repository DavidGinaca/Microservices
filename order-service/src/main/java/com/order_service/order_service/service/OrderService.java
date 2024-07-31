package com.order_service.order_service.service;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;


import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import com.order_service.order_service.dto.InventoryResponse;
import com.order_service.order_service.dto.OrderLineItemsDto;
import com.order_service.order_service.dto.OrderRequest;
import com.order_service.order_service.model.Order;
import com.order_service.order_service.model.OrderLineItems;
import com.order_service.order_service.repository.OrderRepository;

//import io.micrometer.observation.Observation;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final WebClient webClient;

  public void placeOrder(OrderRequest orderRequest) {
        Order order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());

        List<OrderLineItems> orderLineItems = orderRequest.getOrderLineItemsDtoList()
                .stream()
                .map(this::mapToDto)
                .toList();

        order.setOrderLineItemsList(orderLineItems);

        List<String> skuCodes = order.getOrderLineItemsList()
                            .stream()
                            .map(OrderLineItems::getSkuCode)
                            .toList();

        

        InventoryResponse[] inventoryResponseArray =webClient.get()
                    .uri("http://localhost:8082/api/inventory",
                    uriBuilder -> uriBuilder.queryParam("skuCode", skuCodes)
                    .build())
                    .retrieve()
                    .bodyToMono(InventoryResponse[].class)
                    .block();
                    
        boolean allProductsInStock = Arrays.stream(inventoryResponseArray)
                .allMatch(InventoryResponse::isInStock);
               
       
        if(allProductsInStock){
            orderRepository.save(order);
        }else{
            throw new IllegalArgumentException("Product is not in stock.");
        }
    }

     private OrderLineItems mapToDto(OrderLineItemsDto orderLineItemsDto) {
        OrderLineItems orderLineItems = new OrderLineItems();
        orderLineItems.setPrice(orderLineItemsDto.getPrice());
        orderLineItems.setQuantity(orderLineItemsDto.getQuantity());
        orderLineItems.setSkuCode(orderLineItemsDto.getSkuCode());
        return orderLineItems;
    }

}
