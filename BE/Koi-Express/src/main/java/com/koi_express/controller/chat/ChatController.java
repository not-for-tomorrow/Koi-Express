package com.koi_express.controller.chat;

import com.koi_express.entity.promotion.ChatMessage;
import com.koi_express.service.chat.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
@Slf4j
public class ChatController {

    private final ChatService chatService;
    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/chat.sendMessage")
    @SendTo("/topic/public")
    public ChatMessage sendMessage(ChatMessage chatMessage) {
        log.info("Received message from {} to {}: {}",
                chatMessage.getCustomer().getCustomerId(),
                chatMessage.getStaff().getStaffId(),
                chatMessage.getContent());

        // Save the message to the database
        ChatMessage savedMessage = chatService.saveMessage(chatMessage);

        // Return the saved message, which will be broadcasted to subscribers of "/topic/public"
        return savedMessage;
    }

    @MessageMapping("/chat.sendPrivateMessage")
    public void sendPrivateMessage(ChatMessage chatMessage) {
        log.info("Private message from {} to {}: {}",
                chatMessage.getCustomer().getCustomerId(),
                chatMessage.getStaff().getStaffId(),
                chatMessage.getContent());

        // Save the message to the database
        ChatMessage savedMessage = chatService.saveMessage(chatMessage);

        // Send the message privately to the specific customer and staff
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

}