package com.tweety.SwithT.lecture_chat_room.repository;

import com.tweety.SwithT.lecture_chat_room.domain.LectureChatLogs;
import com.tweety.SwithT.lecture_chat_room.domain.LectureChatRoom;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository

public interface LectureChatLogsRepository extends JpaRepository<LectureChatLogs, Long> {
    @Query("SELECT lcl FROM LectureChatLogs lcl WHERE lcl.delYn = 'N' AND lcl.lectureChatRoom = :lectureChatRoom ORDER BY lcl.createdTime DESC")
    Page<LectureChatLogs> findByLectureChatRoomAndOrderByCreatedTime(@Param("lectureChatRoom") LectureChatRoom lectureChatRoom, Pageable pageable);
}
