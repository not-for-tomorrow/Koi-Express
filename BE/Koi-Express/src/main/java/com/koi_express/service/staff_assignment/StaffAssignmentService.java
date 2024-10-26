package com.koi_express.service.staff_assignment;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

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
import com.koi_express.repository.PendingOrderRepository;
import com.koi_express.repository.StaffAssignmentRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class StaffAssignmentService {

    private final OrderRepository orderRepository;
    private final StaffAssignmentRepository staffAssignmentRepository;
    private final DeliveringStaffRepository deliveringStaffRepository;
    private final PendingOrderRepository pendingOrderRepository;

    public StaffAssignmentService(OrderRepository orderRepository,
                                  StaffAssignmentRepository staffAssignmentRepository,
                                  DeliveringStaffRepository deliveringStaffRepository,
                                  PendingOrderRepository pendingOrderRepository) {
        this.orderRepository = orderRepository;
        this.staffAssignmentRepository = staffAssignmentRepository;
        this.deliveringStaffRepository = deliveringStaffRepository;
        this.pendingOrderRepository = pendingOrderRepository;
    }

    private Long lastAssignedStaffId = null;

    @Transactional
    public String assignOrder(Long orderId) {

        Orders order = orderRepository
                .findById(orderId)
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND, "Order not found with ID: " + orderId));

        if (order.getDeliveringStaff() != null) {
            throw new AppException(
                    ErrorCode.ORDER_ALREADY_ASSIGNED, "Order with ID: " + orderId + " is already assigned.");
        }

        BigDecimal kilometers = order.getOrderDetail().getKilometers();
        DeliveringStaffLevel level = kilometers.compareTo(new BigDecimal(300)) < 0
                ? DeliveringStaffLevel.LEVEL_1
                : DeliveringStaffLevel.LEVEL_2;

        List<DeliveringStaff> availableStaff =
                deliveringStaffRepository.findByLevelAndStatus(level, StaffStatus.AVAILABLE);
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

        return "Order with ID " + orderId + " has been assigned to staffId: " + assignedStaff.getStaffId() + " - "
                + assignedStaff.getFullName();
    }

    private DeliveringStaff findNextAvailableStaff(List<DeliveringStaff> availableStaff) {

        if (availableStaff == null || availableStaff.isEmpty()) {
            log.info("No available staff found");
            return null;
        }

        if (lastAssignedStaffId == null
                || availableStaff.stream().noneMatch(staff -> staff.getStaffId().equals(lastAssignedStaffId))) {
            log.info("Returning first staff in list as lastAssignedStaffId is invalid or null");
            return availableStaff.getFirst();
        }

        for (int i = 0; i < availableStaff.size(); i++) {
            if (availableStaff.get(i).getStaffId().equals(lastAssignedStaffId)) {
                return availableStaff.get((i + 1) % availableStaff.size());
            }
        }

        return availableStaff.getFirst();
    }

    @Scheduled(fixedRate = 60000)
    public void assignPendingOrders() {
        List<DeliveringStaff> availableStaff = deliveringStaffRepository.findByStatus(StaffStatus.AVAILABLE);

        if (!availableStaff.isEmpty()) {
            List<Orders> pendingOrders = pendingOrderRepository.findAll();
            for (Orders order : pendingOrders) {
                DeliveringStaff assignedStaff = availableStaff.removeFirst();
                log.info(
                        "Assigning staff ID: {} to pending order ID: {}",
                        assignedStaff.getStaffId(),
                        order.getOrderId());

                order.setDeliveringStaff(assignedStaff);
                order.setStatus(OrderStatus.ASSIGNED);
                orderRepository.save(order);
                pendingOrderRepository.delete(order);
                assignedStaff.setStatus(StaffStatus.DELIVERING);
                deliveringStaffRepository.save(assignedStaff);

                if (availableStaff.isEmpty()) {
                    break;
                }
            }
        } else {
            log.info("No available staff to assign pending orders.");
        }
    }
}
