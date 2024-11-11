package com.tweety.SwithT.lecture_chat_room.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SendMessageDto {
    private Long chatRoomId;
    private String message;
    private Long memberId;
    private String memberName;
}
