package com.tweety.SwithT.common.domain;

import lombok.Getter;

@Getter
public enum Status {
    WAITING,
    STANDBY,
    REJECT,
    ADMIT,
    TERMINATE,
    CANCEL

}
