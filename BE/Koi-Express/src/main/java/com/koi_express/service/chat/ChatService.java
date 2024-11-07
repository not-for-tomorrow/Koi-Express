package com.koi_express.service.chat;

import com.koi_express.dto.request.ChatMessageRequest;
import com.koi_express.entity.account.Staff;
import com.koi_express.entity.customer.Customers;
import com.koi_express.entity.promotion.ChatMessage;
import com.koi_express.repository.ChatRepository;
import com.koi_express.repository.CustomersRepository;
import com.koi_express.repository.StaffRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class ChatService {

    private final ChatRepository chatRepository;
    private final CustomersRepository customerRepository;
    private final StaffRepository staffRepository;

    public ChatMessage saveMessage(ChatMessage message) {
        log.info("Saving message from {} to {}: {}",
                message.getCustomer().getCustomerId(),
                message.getStaff().getStaffId(),
                message.getContent());
        return chatRepository.save(message);
    }

    public ChatMessage sendMessage(ChatMessageRequest message) {
        ChatMessage messages = new ChatMessage();
        message.setCustomer(message.getCustomer());
        message.setStaff(message.getStaff());
        message.setContent(message.getContent());
        messages.setTimestamp(LocalDateTime.now());

        log.info("Sending message from {} to {}: {}",
                message.getCustomer().getCustomerId(),
                message.getStaff().getStaffId(),
                message.getContent());

        return chatRepository.save(messages);
    }

    public List<ChatMessage> receiveMessages(Customers customer, Staff staff, LocalDateTime afterTimestamp) {
        log.info("Receiving messages for customer {} and staff {} after {}",
                customer.getCustomerId(), staff.getStaffId(), afterTimestamp);

        return chatRepository.findByCustomerAndStaffAndTimestampAfter(customer, staff, afterTimestamp);
    }

    public List<ChatMessage> getChatHistoryByCustomerAndStaff(Long customerId, Long staffId, LocalDateTime afterTimestamp) {
        // Tìm kiếm khách hàng và nhân viên từ cơ sở dữ liệu
        Optional<Customers> customer = customerRepository.findById(customerId);
        Optional<Staff> staff = staffRepository.findById(staffId);

        if (customer.isPresent() && staff.isPresent()) {
            log.info("Retrieving chat history between customer {} and staff {} after {}", customerId, staffId, afterTimestamp);

            // Truy vấn tin nhắn theo customer, staff và timestamp
            return chatRepository.findByCustomerAndStaffAndTimestampAfter(customer.get(), staff.get(), afterTimestamp);
        } else {
            log.warn("Customer or Staff not found with provided IDs: customerId = {}, staffId = {}", customerId, staffId);
            return List.of(); // Trả về danh sách trống nếu không tìm thấy customer hoặc staff
        }
    }
}
