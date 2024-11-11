package com.tweety.SwithT.comment.controller;

import com.tweety.SwithT.comment.dto.create.CommentCreateReqDto;
import com.tweety.SwithT.comment.dto.create.CommentCreateResDto;
import com.tweety.SwithT.comment.dto.delete.CommentDeleteResDto;
import com.tweety.SwithT.comment.dto.read.CommentListResDto;
import com.tweety.SwithT.comment.dto.update.CommentUpdateReqDto;
import com.tweety.SwithT.comment.dto.update.CommentUpdateResDto;
import com.tweety.SwithT.comment.service.CommentService;
import com.tweety.SwithT.common.dto.CommonResDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;

    // 댓글 생성
    @PostMapping("/board/{id}/comment/create")
    public ResponseEntity<CommonResDto> commentCreate(@PathVariable("id") Long id, @RequestBody CommentCreateReqDto commentCreateReqDto){
        CommentCreateResDto commentCreateResDto = commentService.commentCreate(id, commentCreateReqDto);

        return new ResponseEntity<>(new CommonResDto(HttpStatus.CREATED,"댓글이 등록되었습니다", commentCreateReqDto), HttpStatus.CREATED);
    }

    // 댓글 목록 조회
    @GetMapping("/board/{id}/comment/list")
    public ResponseEntity<CommonResDto> commentList(@PathVariable("id") Long id,@PageableDefault(size = 5) Pageable pageable){
        Page<CommentListResDto> commentListResponses = commentService.commentList(id, pageable);

        return new ResponseEntity<>(new CommonResDto(HttpStatus.OK,"댓글 목록 조회입니다.",commentListResponses),HttpStatus.OK);
    }

    // 댓글 수정
    @PutMapping("/board/comment/{id}")
    public ResponseEntity<CommonResDto> commentUpdate(@PathVariable("id") Long id,@RequestBody CommentUpdateReqDto dto){
        CommentUpdateResDto commentUpdateResDto = commentService.commentUpdate(id,dto);

        return new ResponseEntity<>(new CommonResDto(HttpStatus.OK,"댓글 수정 됐습니다.", commentUpdateResDto),HttpStatus.OK);
    }

    // 댓글 삭제
    @PatchMapping("/board/comment/{id}/delete")
    public ResponseEntity<CommonResDto> commentDelete(@PathVariable("id") Long id){
        CommentDeleteResDto commentDeleteResDto = commentService.commentDelete(id);

        return new ResponseEntity<>(new CommonResDto(HttpStatus.OK,"댓글이 삭제되었습니다", commentDeleteResDto), HttpStatus.OK);
    }
}
