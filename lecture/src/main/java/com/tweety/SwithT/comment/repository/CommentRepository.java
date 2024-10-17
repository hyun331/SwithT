package com.tweety.SwithT.comment.repository;

import com.tweety.SwithT.comment.domain.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
//    Todo : querydsl 적용
    Page<Comment> findByBoardIdAndDelYn(@Param("id") Long id, @Param("delYn")String delYn, Pageable pageable);
}
