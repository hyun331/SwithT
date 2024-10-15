package com.tweety.SwithT.lecture_chat_room.dto;

import com.tweety.SwithT.lecture.domain.LectureType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MyChatRoomListResDto {
    private Long chatRoomId;
    private String chatRoomTitle;
    private LectureType lectureType;
    private String memberName;

}
