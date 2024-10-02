package com.tweety.SwithT.board.dto.update;

import com.tweety.SwithT.board.domain.Board;
import com.tweety.SwithT.board.domain.Type;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BoardUpdateReqDto {
    private String title;
    private String contents;
    private Type type;

    public static Board toEntity(BoardUpdateReqDto dto){
        return Board.builder()
                .contents(dto.getContents())
                .title(dto.getTitle())
                .type(dto.getType())
                .build();
    }
}
