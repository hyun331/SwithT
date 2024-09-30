package com.tweety.SwithT.comment.dto.create;

import com.tweety.SwithT.board.domain.Board;
import com.tweety.SwithT.comment.domain.Comment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class CommentCreateResDto {

    private Long id;
    private Board board;
    private Long memberId;
    private String memberName;
    private String contents;

    public static CommentCreateResDto fromEntity(Comment comment){
        return CommentCreateResDto.builder()
                .id(comment.getId())
                .memberId(comment.getMemberId())
                .memberName(comment.getMemberName())
                .board(comment.getBoard())
                .contents(comment.getContents())
                .build();
    }
}
