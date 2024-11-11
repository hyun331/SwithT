package com.tweety.SwithT.comment.dto.update;

import com.tweety.SwithT.comment.domain.Comment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class CommentUpdateResDto {

    private Long id;
    private Long memberId;
    private String memberName;
    private String contents;

    public static CommentUpdateResDto fromEntity(Comment comment){
        return CommentUpdateResDto.builder()
                .id(comment.getId())
                .memberId(comment.getMemberId())
                .memberName(comment.getMemberName())
                .contents(comment.getContents())
                .build();
    }
}
