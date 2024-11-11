package com.tweety.SwithT.lecture_chat_room.domain;

import com.tweety.SwithT.common.domain.BaseTimeEntity;
import com.tweety.SwithT.common.domain.MemberType;
import com.tweety.SwithT.lecture.domain.LectureGroup;
import com.tweety.SwithT.lecture.domain.LectureType;
import com.tweety.SwithT.lecture_chat_room.dto.MyChatRoomListResDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;

import java.security.Security;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class LectureChatParticipants extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chatRoom_id")
    private LectureChatRoom lectureChatRoom;

    private Long memberId;


    public MyChatRoomListResDto fromEntityToMyChatRoomListResDto(String memberName){
        LectureGroup lectureGroup = this.lectureChatRoom.getLectureGroup();
        Long loginMemberId = Long.parseLong(SecurityContextHolder.getContext().getAuthentication().getName());

        return MyChatRoomListResDto.builder()
                .chatRoomId(this.lectureChatRoom.getId())
                .chatRoomTitle(lectureGroup.getLecture().getTitle())
                .lectureType(lectureGroup.getLecture().getLectureType())
                .memberName(memberName)
                .build();
    }
}
