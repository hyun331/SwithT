package com.tweety.SwithT.board.dto.create;

import com.tweety.SwithT.board.domain.Board;
import com.tweety.SwithT.board.domain.Type;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class BoardCreateResDto {
    private Long id;
    private Long lectureGroupId;
    private Long memberId;
    private String memberName;
    private String title;
    private String contents;
    private Type type;

    public static BoardCreateResDto fromEntity(Board board){
        return BoardCreateResDto.builder()
                .id(board.getId())
                .lectureGroupId(board.getLectureGroup().getId())
                .contents(board.getContents())
                .title(board.getTitle())
                .type(board.getType())
                .memberId(board.getMemberId())
                .memberName(board.getMemberName())
                .build();
    }
}
