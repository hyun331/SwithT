package com.tweety.SwithT.board.dto.update;

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
public class BoardUpdateResDto {
    private Long id;
    private Long lectureGroupId;
    private Long memberId;
    private String memberName;
    private String title;
    private String contents;
    private Type type;

    public static BoardUpdateResDto fromEntity(Board board){
        return BoardUpdateResDto.builder()
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
