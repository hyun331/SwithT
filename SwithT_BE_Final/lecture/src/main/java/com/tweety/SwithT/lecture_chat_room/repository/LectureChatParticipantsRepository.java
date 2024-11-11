package com.tweety.SwithT.lecture_chat_room.repository;

import com.tweety.SwithT.lecture_chat_room.domain.LectureChatParticipants;
import com.tweety.SwithT.lecture_chat_room.domain.LectureChatRoom;
import com.tweety.SwithT.lecture_chat_room.domain.LectureChatRoom;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface LectureChatParticipantsRepository extends JpaRepository<LectureChatParticipants, Long> {
    // 채팅방 id 기준으로 lecturechatparticipant 받기
    List<LectureChatParticipants> findByLectureChatRoom(LectureChatRoom lectureChatRoom);
    Optional<LectureChatParticipants> findByLectureChatRoomAndMemberIdAndDelYn(LectureChatRoom lectureChatRoom,Long memberId,String delYn);
    @Query("SELECT lcp FROM LectureChatParticipants lcp WHERE lcp.lectureChatRoom.id = :chatRoomId AND lcp.memberId = :memberId AND lcp.delYn = :delYn")
    Optional<LectureChatParticipants> findByLectureChatRoomIdAndMemberIdAndDelYn(@Param("chatRoomId") Long chatRoomId,@Param("memberId") Long memberId,@Param("delYn") String delYn);

    List<LectureChatParticipants> findByMemberIdAndDelYn(Long memberId, String delYn);


}
