package com.koi_express.repository;

import com.koi_express.entity.order.Orders;
import com.koi_express.entity.shipment.DeliveringStaff;
import com.koi_express.entity.shipment.Shipments;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ShipmentsRepository extends JpaRepository<Shipments, Long> {

    Optional<Shipments> findByOrder_orderId(Long orderId);

    void delete(Shipments shipment);

    List<Shipments> findByOrder(Orders order);

    List<Shipments> findByDeliveringStaffId(DeliveringStaff deliveringStaffId);
}
