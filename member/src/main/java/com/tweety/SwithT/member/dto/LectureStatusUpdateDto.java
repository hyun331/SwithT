package com.tweety.SwithT.member.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Builder
public class LectureStatusUpdateDto {
    private Long lectureId;
    private String status;  // Status 대신 String으로 변경

    @JsonCreator
    public LectureStatusUpdateDto(@JsonProperty("lectureId") Long lectureId,
                                  @JsonProperty("status") String status) {
        this.lectureId = lectureId;
        this.status = status;
    }
}
