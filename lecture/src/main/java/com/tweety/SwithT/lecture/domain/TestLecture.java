//package com.tweety.SwithT.lecture.domain;
//
//import com.tweety.SwithT.lecture.dto.LectureDetailResDto;
//import jakarta.persistence.Entity;
//import jakarta.persistence.GeneratedValue;
//import jakarta.persistence.GenerationType;
//import jakarta.persistence.Id;
//import lombok.AllArgsConstructor;
//import lombok.Builder;
//import lombok.Data;
//import lombok.NoArgsConstructor;
//
//@Data
//@Builder
//@Entity
//@AllArgsConstructor
//@NoArgsConstructor
//public class TestLecture {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    private String memberName;
//    private String title;
//    private String contents;
//    private int searchCount;
//    private String hasFreeGroup = "N";
//
//    public LectureDetailResDto toLectureDetailResDto() {
//        return LectureDetailResDto.builder()
//                .id(this.id)
//                .memberName(this.memberName)
//                .title(this.title)
//                .contents(this.contents)
//                .searchCount((long) this.searchCount)
//                .hasFreeGroup(this.hasFreeGroup)
//                .build();
//    }
//}
