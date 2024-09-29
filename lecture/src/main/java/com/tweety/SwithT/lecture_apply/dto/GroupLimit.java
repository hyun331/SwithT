package com.tweety.SwithT.lecture_apply.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GroupLimit {
    private long lectureGroupId;
    private int limitPeople;

    private static final int END = 0;

    public GroupLimit(Long lectureGroupId, int limitPeople) {
        this.lectureGroupId = lectureGroupId;
        this.limitPeople = limitPeople;
    }

    public synchronized void decrease(){
        if (this.limitPeople > END) {
            this.limitPeople--;
        }
    }

    public boolean end(){
        return this.limitPeople == END;
    }
}
