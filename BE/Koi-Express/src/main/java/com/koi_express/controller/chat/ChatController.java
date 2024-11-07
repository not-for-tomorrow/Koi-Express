package com.koi_express.controller.chat;

import com.koi_express.dto.request.ChatMessageRequest;
import com.koi_express.entity.promotion.ChatMessage;
import com.koi_express.service.chat.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/chat")
@CrossOrigin(origins = "*")
public class ChatController {

    private final ChatService chatService;
    private final SimpMessagingTemplate messagingTemplate;

    // Gửi tin nhắn công khai qua WebSocket
    @MessageMapping("/chat.sendMessage")
    @SendTo("/topic/public")
    public ChatMessage sendMessage(ChatMessage chatMessage) {
        log.info("Received message from {} to {}: {}",
                chatMessage.getCustomer().getCustomerId(),
                chatMessage.getStaff().getStaffId(),
                chatMessage.getContent());

        ChatMessage savedMessage = chatService.saveMessage(chatMessage);
        return savedMessage;
    }

    // Gửi tin nhắn riêng tư qua WebSocket
    @MessageMapping("/chat.sendPrivateMessage")
    public void sendPrivateMessage(ChatMessage chatMessage) {
        log.info("Private message from {} to {}: {}",
                chatMessage.getCustomer().getCustomerId(),
                chatMessage.getStaff().getStaffId(),
                chatMessage.getContent());

        ChatMessage savedMessage = chatService.saveMessage(chatMessage);

        messagingTemplate.convertAndSendToUser(
                chatMessage.getCustomer().getCustomerId().toString(),
                "/queue/private",
                savedMessage
        );

        messagingTemplate.convertAndSendToUser(
                chatMessage.getStaff().getStaffId().toString(),
                "/queue/private",
                savedMessage
        );
    }

    // API để gửi tin nhắn qua HTTP
    @PostMapping("/send-message")
    public ResponseEntity<ChatMessage> sendMessageAPI(@RequestBody ChatMessageRequest message) {
        ChatMessage savedMessage = chatService.sendMessage(message);
        return ResponseEntity.ok(savedMessage);
    }

    @GetMapping("/receive-messages")
    public ResponseEntity<List<ChatMessage>> receiveMessages(
            @RequestParam Long customerId,
            @RequestParam Long staffId,
            @RequestParam(required = false) String afterTimestamp) {

        LocalDateTime afterTime = afterTimestamp != null ? LocalDateTime.parse(afterTimestamp) : LocalDateTime.now().minusDays(1);

        log.info("Retrieving messages for customer {} and staff {} after {}", customerId, staffId, afterTime);

        List<ChatMessage> messages = chatService.getChatHistoryByCustomerAndStaff(customerId, staffId, afterTime);

        return ResponseEntity.ok(messages);
    }

}
