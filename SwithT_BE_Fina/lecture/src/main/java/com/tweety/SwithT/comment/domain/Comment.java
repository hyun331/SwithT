package com.tweety.SwithT.comment.domain;

import com.tweety.SwithT.board.domain.Board;
import com.tweety.SwithT.comment.dto.update.CommentUpdateReqDto;
import com.tweety.SwithT.common.domain.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class Comment extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id")
    private Board board;

    @Column(nullable = false)
    private Long memberId;
    @Column(nullable = false)
    private String memberName;

    @Column(nullable = false)
    private String contents;

    public void updateComment(CommentUpdateReqDto dto) {
        this.contents = dto.getContents();
    }
}
