package com.tweety.SwithT.lecture_chat_room.repository;

import com.tweety.SwithT.lecture.domain.LectureGroup;
import com.tweety.SwithT.lecture_chat_room.domain.LectureChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LectureChatRoomRepository extends JpaRepository<LectureChatRoom, Long> {
    List<LectureChatRoom> findByLectureGroupAndDelYn(LectureGroup lectureGroup,String delYn);
}
