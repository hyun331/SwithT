package com.tweety.SwithT.lecture_apply.domain;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GroupLimit {
    private Long groupId;
    private int limit;

    private static final int END = 0;

    public GroupLimit(Long groupId, int limit) {
        this.groupId = groupId;
        this.limit = limit;
    }

    public synchronized void decrease(){
        this.limit--;
    }

    public boolean end(){
        return this.limit == END;
    }
}
