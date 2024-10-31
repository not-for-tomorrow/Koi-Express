package com.koi_express.entity.promotion;

import com.koi_express.enums.MessageType;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ChatMessage {

    String content;

    String sender;

    MessageType type;
}
