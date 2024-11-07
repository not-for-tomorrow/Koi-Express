package com.koi_express.service.chat;

import com.koi_express.entity.account.Staff;
import com.koi_express.entity.customer.Customers;
import com.koi_express.entity.promotion.ChatMessage;
import com.koi_express.repository.ChatRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class ChatService {

    private final ChatRepository chatRepository;

    public ChatMessage saveMessage(ChatMessage message) {
        log.info("Saving message from {} to {}: {}",
                message.getCustomer().getCustomerId(),
                message.getStaff().getStaffId(),
                message.getContent());
        return chatRepository.save(message);
    }

    public List<ChatMessage> getChatHistory(Customers customer, Staff staff) {
        log.info("Retrieving chat history between customer {} and staff {}",
                customer.getCustomerId(), staff.getStaffId());
        return chatRepository.findByCustomerAndStaff(customer, staff);
    }

    public List<ChatMessage> getRecentMessages(Customers customer, Staff staff, LocalDateTime afterTimestamp) {
        log.info("Retrieving recent messages between customer {} and staff {} after {}",
                customer.getCustomerId(), staff.getStaffId(), afterTimestamp);
        return chatRepository.findByCustomerAndStaffAndTimestampAfter(customer, staff, afterTimestamp);
    }

}
