package com.tweety.SwithT.comment.dto.delete;

import com.tweety.SwithT.comment.domain.Comment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class CommentDeleteResDto {
    private Long id;
    private String memberName;
    private String contents;

    public static CommentDeleteResDto fromEntity(Comment comment){
        return CommentDeleteResDto.builder()
                .id(comment.getId())
                .memberName(comment.getMemberName())
                .contents(comment.getContents())
                .build();
    }
}
