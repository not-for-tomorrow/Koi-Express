package com.koi_express.mapper;

import com.koi_express.dto.order_dto.OrderDetailDto;
import com.koi_express.dto.order_dto.OrdersDTO;
import com.koi_express.dto.order_dto.ShipmentsDTO;
import com.koi_express.entity.order.OrderDetail;
import com.koi_express.entity.order.Orders;
import com.koi_express.entity.shipment.Shipments;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface OrderMapper {

    OrdersDTO toOrdersDTO(Orders order);

    OrderDetailDto toOrderDetailDTO(OrderDetail orderDetail);

    ShipmentsDTO toShipmentsDTO(Shipments shipment);

    // Ngược lại
    Orders toOrdersEntity(OrdersDTO orderDTO);

    OrderDetail toOrderDetailEntity(OrderDetailDto orderDetailDTO);

    Shipments toShipmentsEntity(ShipmentsDTO shipmentDTO);
}
