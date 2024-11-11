package com.tweety.SwithT.lecture_chat_room.domain;

import com.tweety.SwithT.common.domain.BaseTimeEntity;
import com.tweety.SwithT.common.domain.MemberType;
import com.tweety.SwithT.lecture_chat_room.dto.SendMessageDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class LectureChatLogs extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chatRoom_id")
    private LectureChatRoom lectureChatRoom;


    @Column(nullable = false)
    private String contents;

    @Column(nullable = false)
    private Long memberId;

    @Column(nullable = false)
    private String memberName;

    public SendMessageDto fromEntityToSendMessageDto(){
        return SendMessageDto.builder()
                .chatRoomId(this.lectureChatRoom.getId())
                .memberId(this.memberId)
                .memberName(this.memberName)
                .message(this.contents)
                .build();
    }


}
