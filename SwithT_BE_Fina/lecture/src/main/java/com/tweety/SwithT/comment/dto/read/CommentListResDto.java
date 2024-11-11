package com.tweety.SwithT.comment.dto.read;

import com.tweety.SwithT.comment.domain.Comment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class CommentListResDto {
    private Long id;
    private String memberName;
    private String contents;

    public static CommentListResDto fromEntity(Comment comment){
        return CommentListResDto.builder()
                .id(comment.getId())
                .memberName(comment.getMemberName())
                .contents(comment.getContents())
                .build();
    }
}
