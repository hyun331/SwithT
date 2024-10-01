package com.tweety.SwithT.lecture_chat_room.service;

import com.tweety.SwithT.lecture.domain.LectureGroup;
import com.tweety.SwithT.lecture.repository.LectureGroupRepository;
import com.tweety.SwithT.lecture_chat_room.domain.LectureChatParticipants;
import com.tweety.SwithT.lecture_chat_room.domain.LectureChatRoom;
import com.tweety.SwithT.lecture_chat_room.dto.ChatRoomCheckDto;
import com.tweety.SwithT.lecture_chat_room.dto.ChatRoomConnectDto;
import com.tweety.SwithT.lecture_chat_room.dto.ChatRoomResDto;
import com.tweety.SwithT.lecture_chat_room.dto.SendMessageDto;
import com.tweety.SwithT.lecture_chat_room.repository.LectureChatParticipantsRepository;
import com.tweety.SwithT.lecture_chat_room.repository.LectureChatRoomRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import jakarta.persistence.EntityNotFoundException;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class LectureChatRoomService {
    private final LectureChatRoomRepository chatRoomRepository;
    private final LectureGroupRepository lectureGroupRepository;
    private final LectureChatParticipantsRepository chatParticipantsRepository;
    private final SimpMessageSendingOperations sendingOperations;
    @Value("${jwt.secretKey}")
    private String secretKey;

    //튜티가 채팅하기 눌렀을 때 채팅방 가져오거나 없으면 생성하기
    @Transactional
    public ChatRoomResDto chatRoomCheckOrCreate(Long lectureGroupId) {
        Long memberId = Long.parseLong(SecurityContextHolder.getContext().getAuthentication().getName());
        LectureGroup lectureGroup = lectureGroupRepository.findByIdAndDelYn(lectureGroupId, "N").orElseThrow(()->{
            throw new EntityNotFoundException("강의그룹을 찾을 수 없습니다.");
        });
        List<LectureChatRoom> lectureChatRoomList = chatRoomRepository.findByLectureGroupAndDelYn(lectureGroup, "N");
        for(LectureChatRoom chatRoom: lectureChatRoomList){
            Long roomId = chatRoom.getId();
            if(chatParticipantsRepository.findByLectureChatRoomAndMemberIdAndDelYn(chatRoom, memberId, "N").isPresent()){
                //채팅방 존재하면
                return ChatRoomResDto.builder()
                        .roomId(roomId)
                        .build();

            }
        }

        //채팅방 존재하지않음.
        //채팅방 생성
        LectureChatRoom newChatRoom = LectureChatRoom.builder()
                .lectureGroup(lectureGroup)
                .build();
        chatRoomRepository.save(newChatRoom);

        //튜티 참여자 추가
        LectureChatParticipants tutee = LectureChatParticipants.builder()
                .lectureChatRoom(newChatRoom)
                .memberId(memberId)
                .build();
        chatParticipantsRepository.save(tutee);

        //튜터 참가자 추가
        LectureChatParticipants tutor = LectureChatParticipants.builder()
                .lectureChatRoom(newChatRoom)
                .memberId(lectureGroup.getLecture().getMemberId())
                .build();

        chatParticipantsRepository.save(tutor);

        return ChatRoomResDto.builder()
                .roomId(newChatRoom.getId())
                .build();

    }

    public ChatRoomResDto tutorLessonChatCheckOrCreate(ChatRoomCheckDto dto) {
        LectureGroup lectureGroup = lectureGroupRepository.findByIdAndDelYn(dto.getLectureGroupId(), "N").orElseThrow(()->{
            throw new EntityNotFoundException("강의그룹을 찾을 수 없습니다.");
        });
        Long tutorId = Long.parseLong(SecurityContextHolder.getContext().getAuthentication().getName());

        List<LectureChatRoom> lectureChatRoomList = chatRoomRepository.findByLectureGroupAndDelYn(lectureGroup, "N");
        for(LectureChatRoom chatRoom: lectureChatRoomList){
            if(chatParticipantsRepository.findByLectureChatRoomAndMemberIdAndDelYn(chatRoom, dto.getTuteeId(), "N").isPresent()){
                //채팅방 존재
                return ChatRoomResDto.builder()
                        .roomId(chatRoom.getId())
                        .build();
            }
        }

        //채팅방 없는 경우
        //채팅방 생성
        LectureChatRoom newChatRoom = LectureChatRoom.builder()
                .lectureGroup(lectureGroup)
                .build();
        chatRoomRepository.save(newChatRoom);

        //튜티 참여자 추가
        LectureChatParticipants tutee = LectureChatParticipants.builder()
                .lectureChatRoom(newChatRoom)
                .memberId(dto.getTuteeId())
                .build();
        chatParticipantsRepository.save(tutee);

        //튜터 참가자 추가
        LectureChatParticipants tutor = LectureChatParticipants.builder()
                .lectureChatRoom(newChatRoom)
                .memberId(tutorId)
                .build();

        chatParticipantsRepository.save(tutor);

        return ChatRoomResDto.builder()
                .roomId(newChatRoom.getId())
                .build();
    }

    public ChatRoomResDto tutorLectureChatCheck(Long lectureGroupId) {
        Long memberId = Long.parseLong(SecurityContextHolder.getContext().getAuthentication().getName());
        LectureGroup lectureGroup = lectureGroupRepository.findByIdAndDelYn(lectureGroupId, "N").orElseThrow(()->{
            throw new EntityNotFoundException("강의그룹을 찾을 수 없습니다.");
        });
        if(lectureGroup.getLecture().getMemberId()!=memberId){
            throw new IllegalArgumentException("접근할 수 없는 채팅방입니다.");
        }

        if(chatRoomRepository.findByLectureGroupAndDelYn(lectureGroup, "N").isEmpty()){
            throw new IllegalArgumentException("접근할 수 없는 채팅방입니다.");
        }
        //강의는 강의 그룹당 채팅방 단 한개
        LectureChatRoom chatRoom = chatRoomRepository.findByLectureGroupAndDelYn(lectureGroup, "N").get(0);

        return ChatRoomResDto.builder()
                .roomId(chatRoom.getId())
                .build();


    }
}
