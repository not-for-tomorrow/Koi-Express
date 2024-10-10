package com.koi_express.service.staffAssignment;

import com.koi_express.entity.order.Orders;
import com.koi_express.entity.shipment.DeliveringStaff;
import com.koi_express.entity.staff.StaffAssignment;
import com.koi_express.enums.DeliveringStaffLevel;
import com.koi_express.enums.OrderStatus;
import com.koi_express.enums.StaffStatus;
import com.koi_express.exception.AppException;
import com.koi_express.exception.ErrorCode;
import com.koi_express.repository.DeliveringStaffRepository;
import com.koi_express.repository.OrderRepository;
import com.koi_express.repository.StaffAssignmentRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class StaffAssignmentService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private StaffAssignmentRepository staffAssignmentRepository;

    @Autowired
    private DeliveringStaffRepository deliveringStaffRepository;

    private Long lastAssignedStaffId = null;

    @Transactional
    public String assignOrder(Long orderId) throws Exception {

        Orders order = orderRepository.findById(orderId)
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND, "Order not found with ID: " + orderId));

        if (order.getDeliveringStaff() != null) {
            throw new AppException(ErrorCode.ORDER_ALREADY_ASSIGNED, "Order with ID: " + orderId + " is already assigned.");
        }

        double kilometers = order.getOrderDetail().getKilometers();
        DeliveringStaffLevel level = kilometers < 300 ? DeliveringStaffLevel.LEVEL_1 : DeliveringStaffLevel.LEVEL_2;

        List<DeliveringStaff> availableStaff = deliveringStaffRepository.findByLevelAndStatus(level, StaffStatus.AVAILABLE);
        log.info("Available staff count for level {}: {}", level, availableStaff.size());

        if (availableStaff.isEmpty()) {
            throw new AppException(ErrorCode.NO_AVAILABLE_STAFF);
        }

        DeliveringStaff assignedStaff = findNextAvailableStaff(availableStaff);
        if (assignedStaff == null) {
            throw new AppException(ErrorCode.NO_AVAILABLE_STAFF);
        }

        log.info("Attempting to assign staff for order ID: {}", orderId);
        log.info("Assigned staff ID: {}, Name: {}", assignedStaff.getStaffId(), assignedStaff.getFullName());

        order.setDeliveringStaff(assignedStaff);
        order.setStatus(OrderStatus.ASSIGNED);
        orderRepository.save(order);

        assignedStaff.setStatus(StaffStatus.DELIVERING);
        deliveringStaffRepository.save(assignedStaff);

        StaffAssignment staffAssignment = StaffAssignment.builder()
                .staff(assignedStaff)
                .order(order)
                .customerId(order.getCustomer().getCustomerId())
                .role(assignedStaff.getRole())
                .assignedAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        staffAssignmentRepository.save(staffAssignment);

        lastAssignedStaffId = assignedStaff.getStaffId();

        return "Order with ID " + orderId + " has been assigned to staffId: " + assignedStaff.getStaffId() + " - " + assignedStaff.getFullName();
    }

    private DeliveringStaff findNextAvailableStaff(List<DeliveringStaff> availableStaff) {
        if (lastAssignedStaffId == null || availableStaff.stream().noneMatch(staff -> staff.getStaffId().equals(lastAssignedStaffId))) {
            log.info("Returning first staff in list as lastAssignedStaffId is invalid or null");
            return availableStaff.get(0);
        }

        for (int i = 0; i < availableStaff.size(); i++) {
            if (availableStaff.get(i).getStaffId().equals(lastAssignedStaffId)) {
                return availableStaff.get((i + 1) % availableStaff.size());
            }
        }

        return availableStaff.get(0);
    }
}