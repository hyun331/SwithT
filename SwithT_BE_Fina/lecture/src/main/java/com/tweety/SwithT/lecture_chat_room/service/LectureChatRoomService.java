package com.tweety.SwithT.lecture_chat_room.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tweety.SwithT.common.dto.CommonResDto;
import com.tweety.SwithT.common.dto.MemberNameResDto;
import com.tweety.SwithT.common.service.MemberFeign;
import com.tweety.SwithT.lecture.domain.*;
import com.tweety.SwithT.lecture.repository.GroupTimeRepository;
import com.tweety.SwithT.lecture.repository.LectureGroupRepository;
import com.tweety.SwithT.lecture_chat_room.domain.LectureChatLogs;
import com.tweety.SwithT.lecture_chat_room.domain.LectureChatParticipants;
import com.tweety.SwithT.lecture_chat_room.domain.LectureChatRoom;
import com.tweety.SwithT.lecture_chat_room.dto.ChatRoomCheckDto;
import com.tweety.SwithT.lecture_chat_room.dto.ChatRoomResDto;
import com.tweety.SwithT.lecture_chat_room.dto.MyChatRoomListResDto;
import com.tweety.SwithT.lecture_chat_room.dto.SendMessageDto;
import com.tweety.SwithT.lecture_chat_room.repository.LectureChatLogsRepository;
import com.tweety.SwithT.lecture_chat_room.repository.LectureChatParticipantsRepository;
import com.tweety.SwithT.lecture_chat_room.repository.LectureChatRoomRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class LectureChatRoomService {
    private final LectureChatRoomRepository chatRoomRepository;
    private final LectureGroupRepository lectureGroupRepository;
    private final LectureChatParticipantsRepository chatParticipantsRepository;
    private final SimpMessageSendingOperations sendingOperations;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final SimpMessagingTemplate template;
    private final MemberFeign memberFeign;
    private final LectureChatLogsRepository lectureChatLogsRepository;
    private final GroupTimeRepository groupTimeRepository;

    //튜티가 채팅하기 눌렀을 때 채팅방 가져오거나 없으면 생성하기
    @Transactional
    public ChatRoomResDto chatRoomCheckOrCreate(Long lectureGroupId) {
        LectureGroup lectureGroup = lectureGroupRepository.findByIdAndDelYn(lectureGroupId, "N")
                .orElseThrow(() -> new EntityNotFoundException("강의그룹을 찾을 수 없습니다."));


        Long tuteeId = Long.valueOf(SecurityContextHolder.getContext().getAuthentication().getName());

        // 기존 채팅방 리스트 가져오기
        List<LectureChatRoom> lectureChatRoomList = chatRoomRepository.findByLectureGroupAndDelYn(lectureGroup, "N");

        for (LectureChatRoom chatRoom : lectureChatRoomList) {
            // 튜티가 이미 참여 중인지 확인
            if (chatParticipantsRepository.findByLectureChatRoomAndMemberIdAndDelYn(chatRoom, tuteeId, "N").isPresent()) {
                return ChatRoomResDto.builder()
                        .roomId(chatRoom.getId())
                        .build();  // 이미 채팅방이 존재하면 그대로 반환
            }
        }

        // 채팅방 생성
        LectureChatRoom newChatRoom = LectureChatRoom.builder()
                .lectureGroup(lectureGroup)
                .build();
        chatRoomRepository.save(newChatRoom);

        // 튜티 참가자 추가
        LectureChatParticipants tutee = LectureChatParticipants.builder()
                .lectureChatRoom(newChatRoom)
                .memberId(tuteeId)
                .build();
        chatParticipantsRepository.save(tutee);

        // 튜터가 이미 해당 채팅방에 참여 중인지 확인
        Long tutorId = lectureGroup.getLecture().getMemberId();
        boolean isTutorAlreadyInRoom = chatParticipantsRepository
                .findByLectureChatRoomAndMemberIdAndDelYn(newChatRoom, tutorId, "N")
                .isPresent();

        // 튜터가 없는 경우에만 추가
        if (!isTutorAlreadyInRoom) {
            LectureChatParticipants tutor = LectureChatParticipants.builder()
                    .lectureChatRoom(newChatRoom)
                    .memberId(tutorId)
                    .build();
            chatParticipantsRepository.save(tutor);
        }

        return ChatRoomResDto.builder()
                .roomId(newChatRoom.getId())
                .build();
    }

    @Transactional
    public ChatRoomResDto chatRoomCheckOrCreateApply(Long lectureGroupId, Long tuteeId) {
        LectureGroup lectureGroup = lectureGroupRepository.findByIdAndDelYn(lectureGroupId, "N")
                .orElseThrow(() -> new EntityNotFoundException("강의그룹을 찾을 수 없습니다."));

        Long tutorId = lectureGroup.getLecture().getMemberId();

        // 기존에 튜티와 튜터가 모두 참여 중인 채팅방이 있는지 확인
        List<LectureChatRoom> lectureChatRoomList = chatRoomRepository.findByLectureGroupAndDelYn(lectureGroup, "N");

        for (LectureChatRoom chatRoom : lectureChatRoomList) {
            boolean isTuteeInRoom = chatParticipantsRepository
                    .findByLectureChatRoomAndMemberIdAndDelYn(chatRoom, tuteeId, "N")
                    .isPresent();

            boolean isTutorInRoom = chatParticipantsRepository
                    .findByLectureChatRoomAndMemberIdAndDelYn(chatRoom, tutorId, "N")
                    .isPresent();

            // 튜티와 튜터가 모두 참여하는 채팅방이 있으면 해당 채팅방 반환
            if (isTuteeInRoom && isTutorInRoom) {
                return ChatRoomResDto.builder()
                        .roomId(chatRoom.getId())
                        .build();
            }
        }

        // 여기까지 왔다는 것은 튜티와 튜터가 모두 포함된 채팅방이 없다는 의미이므로, 새로운 채팅방 생성
        LectureChatRoom newChatRoom = LectureChatRoom.builder()
                .lectureGroup(lectureGroup)
                .build();
        chatRoomRepository.save(newChatRoom);

        // 새 채팅방에 튜티 추가
        LectureChatParticipants tuteeParticipant = LectureChatParticipants.builder()
                .lectureChatRoom(newChatRoom)
                .memberId(tuteeId)
                .build();
        chatParticipantsRepository.save(tuteeParticipant);

        // 새 채팅방에 튜터 추가
        LectureChatParticipants tutorParticipant = LectureChatParticipants.builder()
                .lectureChatRoom(newChatRoom)
                .memberId(tutorId)
                .build();
        chatParticipantsRepository.save(tutorParticipant);

        return ChatRoomResDto.builder()
                .roomId(newChatRoom.getId())
                .build();
    }


    // 튜터가 채팅하기 클릭 (과외)
    @Transactional
    public ChatRoomResDto tutorLessonChatCheckOrCreate(ChatRoomCheckDto dto) {
        LectureGroup lectureGroup = lectureGroupRepository.findByIdAndDelYn(dto.getLectureGroupId(), "N").orElseThrow(()->{
            throw new EntityNotFoundException("강의그룹을 찾을 수 없습니다.");
        });
//        승인 보내는 시점에서 tutorId가 admin의 id로 들어가서 주석 처리
//        Long tutorId = Long.parseLong(SecurityContextHolder.getContext().getAuthentication().getName());

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
                .memberId(lectureGroup.getLecture().getMemberId())
                .build();

        chatParticipantsRepository.save(tutor);

        return ChatRoomResDto.builder()
                .roomId(newChatRoom.getId())
                .build();
    }

    //튜터가 강의 채팅방 있는지 확인하기
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

//    강의 결제 후 채팅방 자동 참여(강의의 경우 그룹 당 채팅방 1개 제한)
    @Transactional
    public void tuteeLectureChatRoomEnterAfterPay(Long lectureGroupId, Long memberId){
        LectureGroup lectureGroup = lectureGroupRepository.findByIdAndDelYn(lectureGroupId, "N").orElseThrow(
                ()-> new EntityNotFoundException("강의 그룹 가져오기에 실패했습니다."));

        List<LectureChatRoom> chatRooms = chatRoomRepository.findByLectureGroupAndDelYn(lectureGroup, "N");

        for(LectureChatRoom lectureChatRoom: chatRooms){
            LectureChatParticipants lectureChatParticipants = LectureChatParticipants.builder()
                    .lectureChatRoom(lectureChatRoom)
                    .memberId(memberId)
                    .build();
            chatParticipantsRepository.save(lectureChatParticipants);
        }
    }

    //강의 승인 후 채팅방 생성 및 튜터 넣어주기
    @Transactional
    public void lectureChatRoomCreateAndTutorParticipant(Long lectureGroupId){
        LectureGroup lectureGroup = lectureGroupRepository.findByIdAndDelYn(lectureGroupId, "N").orElseThrow(()->{
            throw new EntityNotFoundException("id에 맞는 lectureGroup 없습니다.");
        });
        LectureChatRoom newRoom = LectureChatRoom.builder().lectureGroup(lectureGroup).build();
        chatRoomRepository.save(newRoom);
        System.out.println("새 채팅방 id" + newRoom.getId()+"\n\n\n");
        long tutorId = lectureGroup.getLecture().getMemberId();
        LectureChatParticipants participants = LectureChatParticipants.builder()
                .lectureChatRoom(newRoom)
                .memberId(tutorId)
                .build();
        chatParticipantsRepository.save(participants);

    }

    //채팅방 입장
    public void chatRoomEntered(String roomId) {
        System.out.println(roomId + "방에 입장했습니다.");
    }

    //채팅 전송
    @Transactional
    public void chatSend(String roomId, String message, Long memberId, String memberName) {
        System.out.println(memberId+"님이 채팅을 보냈습니다. : "+message);
//        kafkaTemplate.send("chat-"+roomId, message);
        SendMessageDto sendMessageDto = SendMessageDto.builder()
                .chatRoomId(Long.parseLong(roomId))
                .message(message)
                .memberId(memberId)
                .memberName(memberName)
                .build();

        LectureChatRoom lectureChatRoom = chatRoomRepository.findById(Long.parseLong(roomId)).orElseThrow(()->{
            throw new EntityNotFoundException("채팅방이 존재하지 않습니다.");
        });
        LectureChatLogs chatLogs = LectureChatLogs.builder()
                .lectureChatRoom(lectureChatRoom)
                .memberId(memberId)
                .memberName(memberName)
                .contents(message)
                .build();
        lectureChatLogsRepository.save(chatLogs);
        System.out.println("db 저장 먼저한 뒤 kafka send");


        try{
            System.out.println("kafka send전!!!");
            kafkaTemplate.send("chat-room-topic", roomId, sendMessageDto);
            System.out.println("kafka send후!!!");

        }catch (Exception e){
            e.printStackTrace();
        }


    }

    @KafkaListener(topics = "chat-room-topic", containerFactory = "kafkaListenerContainerFactory")
    public void consumerChat(@Header(KafkaHeaders.RECEIVED_KEY) String chatRoomId, @Payload String msg) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            SendMessageDto sendMessageDto = objectMapper.readValue(msg, SendMessageDto.class);
//            System.out.println("Received message for room " + chatRoomId + ": " + sendMessageDto.getMessage());
            template.convertAndSend("/topic/chat-" + chatRoomId, sendMessageDto);
        } catch (Exception e) {
            System.out.println("카프카 리스너 에러!!!!!"+e.getMessage()+"\n\n\n\n\n\n\n\n\n\n\n");
            throw new RuntimeException(e);
        }
    }


    //내 채팅방 리스트
    public Page<MyChatRoomListResDto> myChatRoomList(String chatRoomId, Pageable pageable) {
        Long memberId = Long.parseLong(SecurityContextHolder.getContext().getAuthentication().getName());
        System.out.println("채팅방 : "+chatRoomId);
        List<LectureChatParticipants> lectureChatParticipantsList = new ArrayList<>();

        //특정 채팅방으로 입장하는 경우
        if(chatRoomId != null && !chatRoomId.isEmpty() && chatRoomId!=""){
            LectureChatParticipants selectedChatRoom = chatParticipantsRepository.findByLectureChatRoomIdAndMemberIdAndDelYn(Long.parseLong(chatRoomId), memberId, "N").orElseThrow(()->{
                throw new EntityNotFoundException("채팅방이 존재하지 않습니다.");
            });
            lectureChatParticipantsList.add(selectedChatRoom);
        }

        //특정 채팅방으로 입장한 경우 list에 추가해주기
        if(!lectureChatParticipantsList.isEmpty()){
            for(LectureChatParticipants participants: chatParticipantsRepository.findByMemberIdAndDelYn(memberId, "N")){
                if(lectureChatParticipantsList.get(0).getLectureChatRoom().getId() != participants.getLectureChatRoom().getId()){
                    lectureChatParticipantsList.add(participants);
                }
            }
        }else{
            lectureChatParticipantsList.addAll(chatParticipantsRepository.findByMemberIdAndDelYn(memberId, "N"));
        }
        List<MyChatRoomListResDto> myChatRoomListResDtoList = lectureChatParticipantsList.stream().map(a-> {
            //과외인 경우 상대 이름을 채팅 목록에 표시해주기
            String memberName = "";
            if(a.getLectureChatRoom().getLectureGroup().getLecture().getLectureType() == LectureType.LESSON) {
                List<LectureChatParticipants> chatParticipantsList = chatParticipantsRepository.findByLectureChatRoom(a.getLectureChatRoom());
                for (LectureChatParticipants participants : chatParticipantsList) {
                    if (participants.getMemberId() != memberId) {
                        CommonResDto commonResDto = memberFeign.getMemberNameById(participants.getMemberId());
                        ObjectMapper objectMapper = new ObjectMapper();
                        MemberNameResDto memberNameResDto = objectMapper.convertValue(commonResDto.getResult(), MemberNameResDto.class);
                        memberName = memberNameResDto.getName();
                        break;
                    }
                }
            }else{
                //강의인 경우 시간대 보여주기
                LectureGroup lectureGroup = a.getLectureChatRoom().getLectureGroup();
                List<GroupTime> groupTimeList = groupTimeRepository.findByLectureGroupIdAndDelYn(lectureGroup.getId(), "N");
                StringBuilder groupTitle = new StringBuilder();
                for(GroupTime groupTime : groupTimeList){
                    groupTitle.append(changeLectureDayKorean(groupTime.getLectureDay())).append(" ").append(groupTime.getStartTime()).append("~").append(groupTime.getEndTime()).append("  /  ");
                }

                if (!groupTitle.isEmpty()) {
                    groupTitle.setLength(groupTitle.length() - 5);
                }
                memberName = groupTitle.toString();
            }
            return a.fromEntityToMyChatRoomListResDto(memberName);
        }).toList();

        PageRequest pageRequest = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize());
        int start = (int) pageRequest.getOffset();
        int end = Math.min((start + pageRequest.getPageSize()), myChatRoomListResDtoList.size());
        Page<MyChatRoomListResDto> result = new PageImpl<>(myChatRoomListResDtoList.subList(start, end), pageRequest, myChatRoomListResDtoList.size());


        return result;
    }

    //채팅 내역 가져오기
    public Page<SendMessageDto> getChatRoomLog(Long roomId, Pageable pageable) {
        LectureChatRoom lectureChatRoom = chatRoomRepository.findByIdAndDelYn(roomId, "N").orElseThrow(()->{
            throw new EntityNotFoundException("채팅방을 찾을 수 없습니다.");
        });

        Long memberId = Long.parseLong(SecurityContextHolder.getContext().getAuthentication().getName());
        if(chatParticipantsRepository.findByLectureChatRoomIdAndMemberIdAndDelYn(roomId, memberId, "N").isEmpty()){
            throw new IllegalArgumentException("접근할 수 없는 채팅방입니다.");
        }

        Page<LectureChatLogs> lectureChatLogsPage = lectureChatLogsRepository.findByLectureChatRoomAndOrderByCreatedTime(lectureChatRoom, pageable);

        List<LectureChatLogs> reversedList = new ArrayList<>();
        for(int i=lectureChatLogsPage.getContent().size()-1; i>=0; i--){
            reversedList.add(lectureChatLogsPage.getContent().get(i));
        }

        Page<LectureChatLogs> reversedPage = new PageImpl<>(reversedList, pageable, lectureChatLogsPage.getTotalElements());

        return reversedPage.map(LectureChatLogs::fromEntityToSendMessageDto);
    }

    public String changeLectureDayKorean(LectureDay lectureDay){
        return switch (lectureDay) {
            case MONDAY -> "월";
            case TUESDAY -> "화";
            case WEDNESDAY -> "수";
            case THURSDAY -> "목";
            case FRIDAY -> "금";
            case SATURDAY -> "토";
            case SUNDAY -> "일";
        };
    }

    @Transactional
    public void exitChatRoom(LectureGroup lectureGroup, Long memberId){
        List<LectureChatRoom> chatRooms = chatRoomRepository.findByLectureGroupAndDelYn(lectureGroup, "N");

        for(LectureChatRoom lectureChatRoom: chatRooms){
            LectureChatParticipants lectureChatParticipants = chatParticipantsRepository.
                    findByLectureChatRoomAndMemberIdAndDelYn(lectureChatRoom, memberId, "N")
                    .orElseThrow(() -> new EntityNotFoundException("참가 정보를 불러오는 데 실패했습니다"));
            lectureChatParticipants.updateDelYn();
        }
//        나갔다는 정보 알려주는 게 있으면 좋을까요?
    }
}
