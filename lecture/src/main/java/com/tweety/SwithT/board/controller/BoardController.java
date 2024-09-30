package com.tweety.SwithT.board.controller;

import com.tweety.SwithT.board.dto.create.BoardCreateReqDto;
import com.tweety.SwithT.board.dto.create.BoardCreateResDto;
import com.tweety.SwithT.board.dto.delete.BoardDeleteResDto;
import com.tweety.SwithT.board.dto.read.BoardDetailResDto;
import com.tweety.SwithT.board.dto.read.BoardListResDto;
import com.tweety.SwithT.board.dto.update.BoardUpdateReqDto;
import com.tweety.SwithT.board.dto.update.BoardUpdateResDto;
import com.tweety.SwithT.board.service.BoardService;
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
public class BoardController {

    private final BoardService boardService;

    // 게시글 생성
    @PostMapping("/lecture/{lectureGroupId}/board/create")
    public ResponseEntity<CommonResDto> createBoard( @PathVariable("lectureGroupId") Long lectureGroupId, @RequestBody BoardCreateReqDto boardCreateReqDto) {
        BoardCreateResDto boardCreateResDto = boardService.createBoard( lectureGroupId, boardCreateReqDto);

        return new ResponseEntity<>(new CommonResDto(HttpStatus.CREATED, "게시글이 등록되었습니다", boardCreateResDto), HttpStatus.CREATED);
    }

    // 게시글 목록 조회 - Todo :공지사항인 것만 또는 전체
    @GetMapping("/lecture/{lectureGroupId}/board/list")
    public ResponseEntity<CommonResDto> boardList(@PathVariable("lectureGroupId") Long lectureGroupId, @PageableDefault(size = 5)Pageable pageable){
        Page<BoardListResDto> boardList = boardService.boardList(lectureGroupId,pageable);

        return new ResponseEntity<>(new CommonResDto(HttpStatus.OK,"게시글 목록 조회입니다.",boardList),HttpStatus.OK);
    }

    // 게시글 상세
    @GetMapping("/lecture/board/{boardId}")
    public ResponseEntity<CommonResDto> boardDetail(@PathVariable("boardId") Long boardId){
        BoardDetailResDto board = boardService.boardDetail(boardId);

        return new ResponseEntity<>(new CommonResDto(HttpStatus.OK,"게시글 상세 조회입니다.",board),HttpStatus.OK);
    }

    // 게시글 update
    @PutMapping("/lecture/board/{boardId}")
    public ResponseEntity<CommonResDto> updateBoard(@PathVariable("boardId") Long boardId, @RequestBody BoardUpdateReqDto boardUpdateReqDto) {
        BoardUpdateResDto boardUpdateResDto = boardService.updateBoard(boardId, boardUpdateReqDto);

        return new ResponseEntity<>(new CommonResDto(HttpStatus.CREATED,"게시글이 수정되었습니다", boardUpdateResDto), HttpStatus.CREATED);
    }
    // 게시글 삭제
    @PatchMapping("/lecture/board/{boardId}/delete")
    public ResponseEntity<CommonResDto> deleteBoard(@PathVariable("boardId") Long boardId){
        BoardDeleteResDto boardDeleteResDto = boardService.boardDelete(boardId);

        return new ResponseEntity<>(new CommonResDto(HttpStatus.OK,"게시글이 삭제되었습니다", boardDeleteResDto), HttpStatus.OK);
    }

}
