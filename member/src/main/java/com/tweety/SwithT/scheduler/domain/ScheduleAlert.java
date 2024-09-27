package com.tweety.SwithT.scheduler.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.tweety.SwithT.scheduler.dto.ScheduleAlertUpdateDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class ScheduleAlert {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "schedule_id")
    private Scheduler scheduler;

    private char sendYn;

    @Column(nullable = false)
    @JsonFormat(pattern = "HH:mm")
    private LocalTime reserveTime;

    @Column(nullable = false)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate reserveDay;

    public void sended(){
        this.sendYn = 'Y';
    }

    public ScheduleAlert updateAlert(ScheduleAlertUpdateDto dto){
        this.reserveDay = dto.getNewReserveDay();
        this.reserveTime = dto.getNewReserveTime().withSecond(0).withNano(0);

        return this;
    }

}
