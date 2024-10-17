package com.tweety.SwithT.board.dto.read;

import com.tweety.SwithT.board.domain.Board;
import com.tweety.SwithT.board.domain.Type;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class BoardListResDto {
    private Long id;
    private String memberName;
    private String title;
    private String contents;
    private LocalDate postDate;
    private Type type;
    private boolean isAuthor;

    public static BoardListResDto fromEntity(Board board, Long memberId){
        return BoardListResDto.builder()
                .id(board.getId())
                .title(board.getTitle())
                .contents(board.getContents())
                .type(board.getType())
                .memberName(board.getMemberName())
                .postDate(board.getCreatedTime().toLocalDate())
                .isAuthor(board.getMemberId().equals(memberId))
                .build();
    }
}
