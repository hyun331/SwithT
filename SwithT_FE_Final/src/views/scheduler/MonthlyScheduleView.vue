<template>
  <div class="calendar-wrapper">
    <div class="sidebar">


      <div class="today-schedule text-left" >
        <span style="font-size: 22px;"><strong>오늘의 일정</strong></span>
        <div v-if="sortedTodayEvents.length != 0">
          <div class="d-flex" v-for="event in sortedTodayEvents" :key="event.id"
            style="list-style: none; margin-top: 20px; background-color: #f5f5f5; padding: 10px; border-radius: 10px; margin-bottom: 10px;">
            <div class="mr-1">
              <span v-if="event.extendedProps.groupId == 1" class="color-box" style="background-color: #82B1FF;"></span>
              <span v-if="event.extendedProps.groupId == 2" class="color-box" style="background-color: #FF8F00;"></span>
              <span v-if="event.extendedProps.groupId == 3" class="color-box" style="background-color: #FFF490;"></span>
            </div>
            <div class="mr-1" style="font-weight: 700;">{{ formatTime(event.start) }}</div>
            <div>{{ event.title }}</div>
          </div>

        </div>
        <div class="mt-3 mb-20" v-else>
          오늘의 일정이 없습니다.
        </div>
      </div>
      <div class="week-alert-schedule text-left">
        <span style="font-size: 22px;"><strong>주간 알림 일정</strong></span>
        
        <div v-if="sortedWeekAlertEvents.length !== 0">
          <div
            class="d-flex"
            v-for="event in sortedWeekAlertEvents"
            :key="event.id"
            style="list-style: none; margin-top: 20px; background-color: #f5f5f5; padding: 10px; border-radius: 10px; margin-bottom: 10px;"
          >
            <div class="mr-1">
              <span v-if="event.extendedProps.groupId == 1" class="color-box" style="background-color: #82B1FF;"></span>
              <span v-if="event.extendedProps.groupId == 2" class="color-box" style="background-color: #FF8F00;"></span>
              <span v-if="event.extendedProps.groupId == 3" class="color-box" style="background-color: #FFF490;"></span>
            </div>
            <div class="mr-1" style="font-weight: 700;">{{ formatTime(event.start) }}</div>
            <div>{{ event.title }}</div>
          </div>
        </div>
        
        <div class="mt-3" v-else>
          주간 알림 일정이 없습니다.
        </div>
      </div>


    </div>

    <!-- 메인 캘린더 -->
    <v-container class="calendar-container">
      <div class="d-flex flex-row-reverse mb-5">
        <span class="color-box" style="background-color: #82B1FF;"><v-tooltip location="top" activator="parent">수업
            일정</v-tooltip></span>
        <span class="color-box" style="background-color: #FF8F00;"><v-tooltip location="top" activator="parent">과제
            일정</v-tooltip></span>
        <span class="color-box" style="background-color: #FFF490;"><v-tooltip location="top" activator="parent">개인
            일정</v-tooltip></span>
      </div>
      <FullCalendar ref="fullCalendar" :options="calendarOptions" class="full-calender" />

      <HandleScheduleModal v-if="isModalVisible" :selectedDate="selectedDate" :selectedSchedule="selectedEvent"
        :alertInfo="alertInfo" :canEdit="canEdit" @close="isModalVisible = false"
        @scheduleSaved="handleScheduleSubmitted" @scheduleDeleted="handleScheduleDeleted" @saveAlert="handleSaveAlert"
        @createAlert="handleCreateAlert" @cancelAlert="handleCancelAlert" />

      <v-snackbar v-model="snackbar" :timeout="3000" top>
        {{ snackbarMessage }}
      </v-snackbar>
    </v-container>
  </div>
</template>

<script>
import FullCalendar from '@fullcalendar/vue3';
import dayGridPlugin from "@fullcalendar/daygrid";
import interactionPlugin from '@fullcalendar/interaction';
import timeGridPlugin from "@fullcalendar/timegrid";
import koLocale from '@fullcalendar/core/locales/ko';
import axios from 'axios';
import HandleScheduleModal from './HandleScheduleModal.vue'; // 일정 처리 모달

export default {
  components: {
    FullCalendar,
    HandleScheduleModal,
  },
  data() {
    const self=this;
    return {
      isModalVisible: false, // 모달 표시 여부
      selectedDate: '', // 선택한 날짜
      selectedEvent: null, // 선택한 이벤트 데이터
      alertData: null, // 알림 정보 저장
      todayEvents: [], // 오늘의 일정
      weekAlertEvents: [], // 이번 주 알림 일정
      calendarOptions: {
        plugins: [dayGridPlugin, timeGridPlugin, interactionPlugin],
        initialView: 'dayGridMonth',
        locale: koLocale,
        events: [

        ],
        datesSet: this.handleDatesSet,
        height: 'auto',
        eventTimeFormat: {
          hour: 'numeric',
          minute: '2-digit',
          hour12: false,
        },
        selectable: true,
        select: this.handleDateSelect, // 날짜 선택 시 호출
        eventClick: this.handleEventClick, // 이벤트 클릭 시 호출
        eventDidMount: this.handleEventDidMount,
        headerToolbar: {
          left: "customprev,customnext,today",
          center: "title",
          right: "dayGridMonth,timeGridWeek,timeGridDay"
        },
        customButtons: {
          customprev: {
            text: '<',
            click: function () {
              self.$refs.fullCalendar.getApi().prev();
            }
          },
          customnext: {
            text: '>',
            click: function () {
              self.$refs.fullCalendar.getApi().next();
            }
          }
        }
      },
      snackbar: false, // 스낵바 표시 여부
      snackbarMessage: '', // 스낵바 메시지
    };
  },
  computed: {
    // todayEvents를 시간 기준으로 정렬
    sortedTodayEvents() {
      return [...this.todayEvents].sort((a, b) => new Date(a.start) - new Date(b.start));
    },
    sortedWeekAlertEvents() {
      return [...this.weekAlertEvents].sort((a, b) => new Date(a.start) - new Date(b.start));
    },
  },
  methods: {
    formatTime(dateString) {
      const date = new Date(dateString);
      return date.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit', hour12: false });
    },
    formatWeekAlertTime(dateString) {
      const date = new Date(dateString);
      const day = date.getDate();
      const hours = date.getHours().toString().padStart(2, '0'); // 시
      const minutes = date.getMinutes().toString().padStart(2, '0'); // 분
      return `${day}일 ${hours}:${minutes}`;
    },
    // 메인 캘린더에 일정 데이터 세팅
    async handleDatesSet(info) {
      this.$nextTick(async () => {
        if (!this.$refs.fullCalendar) {
          console.error('fullCalendar 참조가 없습니다.');
          return;
        }

        const year = info.view.currentStart.getFullYear();
        let month = (info.view.currentStart.getMonth() + 1).toString().padStart(2, '0');

        const calendarApi = this.$refs.fullCalendar.getApi();
        calendarApi.removeAllEventSources();

        await this.fetchHolidays(year, month); // 공휴일 데이터 가져오기
        await this.fetchSchedules(year, month); // 사용자 일정 데이터 가져오기
        this.setTodayEvents();
        this.setWeekAlertEvents();
      });
    },
    // 공휴일 데이터 가져오기
    async fetchHolidays(year, month) {
      try {
        const response = await axios.get(`${process.env.VUE_APP_API_BASE_URL}/member-service/get-holidays`, {
          params: { year, month },
        });

        const parser = new DOMParser();
        const xmlDoc = parser.parseFromString(response.data, 'text/xml');
        const items = xmlDoc.getElementsByTagName('item');

        const holidays = Array.from(items).map(item => ({
          title: item.getElementsByTagName('dateName')[0].textContent, // 공휴일 이름 (메인 캘린더에만 적용)
          start: item.getElementsByTagName('locdate')[0].textContent,
          allDay: true,
          classNames: ['holiday-event'],
        }));

        this.$refs.fullCalendar.getApi().addEventSource(holidays); // 메인 캘린더에 추가
      } catch (error) {
        console.error('공휴일 정보를 가져오는 중 오류가 발생했습니다.', error);
      }
    },

    // 사용자 일정 데이터 가져오기
    // async fetchSchedules(year, month) {
    //   try {
    //     const data = { year, month };
    //     const response = await axios.post(`${process.env.VUE_APP_API_BASE_URL}/member-service/schedule/monthly-schedules`, data);

    //     const events = response.data.result.map(schedule => {
    //       if (!schedule.schedulerDate || !schedule.schedulerTime) {
    //         console.error("Invalid schedule data:", schedule);
    //         return null;
    //       }

    //       const start = `${schedule.schedulerDate}T${schedule.schedulerTime}`;
    //       const end = `${schedule.schedulerDate}T${schedule.schedulerTime}`;

    //       const customClass = schedule.alertYn === "Y" ? "alert-event" : "";
    //       let groupId = 3;

    //       // 수업 일정
    //       if (schedule.lectureAssignmentId === null && schedule.lectureGroupId != null) {
    //         groupId = 1;
    //       } else if (schedule.lectureGroupId != null && schedule.lectureAssignmentId != null) {
    //         // 과제 일정
    //         groupId = 2;
    //       }

    //       return {
    //         id: schedule.id,
    //         title: schedule.title,
    //         start,
    //         end,
    //         description: schedule.content,
    //         classNames: [customClass],
    //         extendedProps: {
    //           groupId,
    //           alertYn: schedule.alertYn
    //         },
    //       };
    //     }).filter(event => event !== null);

    //     this.$refs.fullCalendar.getApi().addEventSource(events); // 메인 캘린더
    //   } catch (error) {
    //     console.error('스케줄을 가져오는 중 오류가 발생했습니다.', error);
    //   }
    // },
    async fetchSchedules(year, month) {
      try {
        const data = { year, month };
        const response = await axios.post(`${process.env.VUE_APP_API_BASE_URL}/member-service/schedule/monthly-schedules`, data);

        const uniqueEvents = []; // 중복 방지를 위한 배열
        response.data.result.forEach(schedule => {
          if (!schedule.schedulerDate || !schedule.schedulerTime) return;

          const start = `${schedule.schedulerDate}T${schedule.schedulerTime}`;
          const end = `${schedule.schedulerDate}T${schedule.schedulerTime}`;
          const customClass = schedule.alertYn === "Y" ? "alert-event" : "";
          let groupId = 3;

          // 수업 일정
          if (schedule.lectureAssignmentId === null && schedule.lectureGroupId != null) {
            groupId = 1;
          } else if (schedule.lectureGroupId != null && schedule.lectureAssignmentId != null) {
            // 과제 일정
            groupId = 2;
          }

          const event = {
            id: schedule.id,
            title: schedule.title,
            start,
            end,
            description: schedule.content,
            classNames: [customClass],
            extendedProps: {
              groupId,
              alertYn: schedule.alertYn
            },
          };

          // 중복된 id의 이벤트가 없는 경우에만 추가
          if (!uniqueEvents.some(e => e.id === event.id)) {
            uniqueEvents.push(event);
          }
        });

        this.$refs.fullCalendar.getApi().addEventSource(uniqueEvents); // 중복 방지 후 메인 캘린더에 추가
      } catch (error) {
        console.error('스케줄을 가져오는 중 오류가 발생했습니다.', error);
      }
    },


    // 오늘의 일정 가져오기
    setTodayEvents() {
      this.$nextTick(() => {
        if (!this.$refs.fullCalendar) {
          console.error('fullCalendar 참조가 없습니다.');
          return;
        }

        const today = new Date();
        today.setHours(0, 0, 0, 0); // 오늘 날짜의 시간을 00:00:00으로 설정 (로컬 타임존 기준)

        const allEvents = this.$refs.fullCalendar.getApi().getEvents();

        this.todayEvents = allEvents.filter(event => {
          console.log("오늘 이벤트", event)
          const eventDate = new Date(event.start);
          eventDate.setHours(0, 0, 0, 0); // 이벤트 날짜도 00:00:00으로 설정 (로컬 타임존 기준)
          return eventDate.getTime() === today.getTime(); // 날짜를 비교하여 일치하는 이벤트만 필터링
        });
      });
    },

    // 이번 주 알림 일정 가져오기
    setWeekAlertEvents() {
      const startOfWeek = new Date().getTime() - new Date().getDay() * 86400000;
      const endOfWeek = startOfWeek + 6 * 86400000;
      this.weekAlertEvents = this.$refs.fullCalendar.getApi().getEvents().filter(event => {
        const eventTime = new Date(event.startStr).getTime();
        return eventTime >= startOfWeek && eventTime <= endOfWeek && event.classNames.includes('alert-event');
      });
    },

    // 이벤트가 캘린더에 렌더링된 후 실행되는 메서드
    handleEventDidMount(info) {
      const eventElement = info.el;
      const groupId = info.event.extendedProps.groupId;

      // 그룹별 색상 설정
      if (groupId === 1) {
        eventElement.style.backgroundColor = '#82B1FF'; // 수업 그룹
      } else if (groupId === 2) {
        eventElement.style.backgroundColor = '#FF8F00'; // 과제 그룹
      } else {
        eventElement.style.backgroundColor = '#FFF490'; // 기타 그룹
      }

      if (info.event.classNames.includes('alert-event')) {
        const icon = document.createElement('span');
        icon.classList.add('mdi', 'mdi-bell-outline');
        icon.style.color = '#666';
        icon.style.marginRight = '4px';
        eventElement.querySelector('.fc-event-title').prepend(icon);
      }

      const dot = eventElement.querySelector('.fc-daygrid-event-dot');
      if (dot) {
        dot.remove();
      }
    },

    // 날짜 선택 시 일정 생성 모달 표시
    handleDateSelect(selectionInfo) {

      this.selectedDate = selectionInfo.startStr;
      this.selectedEvent = null; // 새 일정이므로 선택된 이벤트는 없음
      this.isModalVisible = true;
      this.alertData = null; // 새 일정이므로 알림 정보 초기화
    },

    // 이벤트 클릭 시 호출
    async handleEventClick(info) {
      const scheduleId = info.event.id;
      const groupId = info.event.extendedProps.groupId;

      // `canEdit`을 groupId에 따라 설정
      const canEdit = groupId === 3;

      // 클릭한 이벤트 정보를 모달에 전달
      this.selectedEvent = {
        id: scheduleId,
        title: info.event.title,
        schedulerDate: info.event.startStr.split('T')[0],
        schedulerTime: info.event.startStr.split('T')[1],
        content: info.event.extendedProps.description,
        alertYn: info.event.extendedProps.alertYn || false,
      };

      if (this.selectedEvent.alertYn === 'Y') {
        try {
          const response = await axios.post(`${process.env.VUE_APP_API_BASE_URL}/member-service/scheduler/get-alert/${scheduleId}`);
          if (response.data && response.data.result) {
            this.alertInfo = {
              id: response.data.result.id,
              reserveDay: response.data.result.reserveDay,
              reserveTime: response.data.result.reserveTime,
              schedulerId: response.data.result.schedulerId,
            };
          } else {
            this.alertInfo = { reserveDay: null, reserveTime: null };
          }
        } catch (error) {
          this.alertInfo = { reserveDay: null, reserveTime: null };
          console.error('알림 정보를 가져오는 중 오류가 발생했습니다:', error);
        }
      } else {
        this.alertInfo = { reserveDay: null, reserveTime: null };
      }

      // 모달 표시 및 `canEdit` 전달
      this.isModalVisible = true;
      this.canEdit = canEdit; // canEdit 상태 저장
    },

    // 일정 데이터가 모달에서 넘어왔을 때 처리
    async handleScheduleSubmitted({ scheduleData, alertData }) {
      try {
        console.log("AlertData Parent Component", this.dataToSend)
        let response;
        if (this.selectedEvent) {
          // 일정 수정 처리
          response = await axios.patch(`${process.env.VUE_APP_API_BASE_URL}/member-service/scheduler/${this.selectedEvent.id}/update`, scheduleData);
          this.showSnackbar("일정이 수정되었습니다.");
        } else {
          // 새 일정 등록 처리
          response = await axios.post(`${process.env.VUE_APP_API_BASE_URL}/member-service/scheduler/make`, scheduleData);
          this.showSnackbar("일정이 저장되었습니다.");
        }
        // 스케줄 저장 후 반환된 ID를 알림 데이터에 사용
        const savedScheduleId = response.data.result || this.selectedEvent.id;
        console.log(console.log("alertData", alertData))
        console.log("scheudleID", savedScheduleId)
        if (alertData) {
          alertData.scheduleId = savedScheduleId; // 알림 데이터에 스케줄 ID 설정
          console.log("alertData", alertData)
          await this.handleCreateAlert(alertData); // 알림 저장
        }

        this.isModalVisible = false;
        await this.refreshCalendarEvents(); // 일정 새로고침
      } catch (error) {
        console.error('Error during schedule saving process:', error);
      }
      this.scheduleData = null;
      this.refreshCalendarEvents(); // 일정 새로고침

    },

    // 알림 데이터를 서버에 저장하는 함수
    async handleCreateAlert(alertData) {
      try {
        const response = await axios.post(`${process.env.VUE_APP_API_BASE_URL}/member-service/scheduler/set-alert`, alertData);
        console.log("Alert created successfully:", response.data);
        this.isModalVisible = false;
        this.alertData = null;
        this.showSnackbar(response.data.status_message)
        await this.refreshCalendarEvents(); // 일정 새로고침
      } catch (error) {
        console.error('Error while creating alert:', error);
      }
    },

    async handleSaveAlert(alertData) {
      try {
        const response = await axios.patch(`${process.env.VUE_APP_API_BASE_URL}/member-service/scheduler/update-alert`, alertData);
        console.log("알림 수정 완료:", response.data);
        this.showSnackbar("알림이 저장되었습니다.");
        this.isModalVisible = false;
        // alert(response.data.status_message)
        await this.refreshCalendarEvents(); // 일정 새로고침
      } catch (error) {
        this.showSnackbar("알림이 생성되지 않았습니다.");
        console.log(error.response.data)
        // alert('알림 수정 중 오류 발생:', error);
      }
    },

    // 일정 삭제 처리
    async handleScheduleDeleted(scheduleId) {
      try {
        if (scheduleId) {
          const response = await axios.put(`${process.env.VUE_APP_API_BASE_URL}/member-service/scheduler/${scheduleId}/delete`);
          console.log(response.data);
          this.showSnackbar("일정이 삭제되었습니다.")
          // alert(response.data.status_message);
          await this.refreshCalendarEvents(); // 일정 삭제 후 캘린더 새로고침
        }
        this.isModalVisible = false;
      } catch (error) {
        console.error('일정 삭제 중 오류가 발생했습니다.', error);
      }
      this.refreshCalendarEvents(); // 일정 삭제 후 캘린더 새로고침

    },

    async handleCancelAlert(alertData) {
      try {
        const response = await axios.delete(`${process.env.VUE_APP_API_BASE_URL}/member-service/scheduler/cancel-alert/${alertData.alertId}`);
        console.log("Alert canceled successfully:", response.data);
        this.isModalVisible = false;
        this.showSnackbar("알림이 삭제되었습니다.")

        // alert(response.data.status_message)
        await this.refreshCalendarEvents(); // 캘린더 새로고침
      } catch (error) {
        this.showSnackbar("알림이 삭제되지 않았습니다.")

        console.error('Error while canceling alert:', error);
      }
    },
    showSnackbar(message) {
      this.snackbarMessage = message;
      this.snackbar = true;
    },
    // 캘린더 이벤트 새로고침
    async refreshCalendarEvents() {
      try {
        const calendarApi = this.$refs.fullCalendar.getApi();
        calendarApi.removeAllEvents(); // 기존 이벤트 제거

        // 현재 년도와 월을 가져옴
        const currentDate = new Date();
        const year = currentDate.getFullYear();
        let month = currentDate.getMonth() + 1; // 기본값으로 현재 월 설정
        month = month.toString().padStart(2, '0'); // 01, 02 형식으로 설정

        const response = await this.fetchSchedules(year, month); // fetchSchedules 호출
        if (response && response.data && response.data.result) {
          calendarApi.addEventSource(response.data.result); // 새로운 일정 데이터 추가
        }
      } catch (error) {
        this.showSnackbar(error.response.data)
      }
    },
  },
};
</script>

<style scoped>
.calendar-wrapper {
  display: flex;
  flex-direction: row;
  height: 100vh;
}

.sidebar {
  width: 350px;
  padding: 10px 0 0 40px;
  display: flex;
  flex-direction: column;
  justify-content: flex-start;
}

::v-deep .fc-media-screen fc-direction-ltr fc-theme-standard {
  height: 700px;
}

/* 메인 캘린더 */
::v-deep .calendar-container {
  margin-left: 5%;
  margin-top: 5%;
  max-width: 1250px;
  flex-grow: 0;
  padding: 10px;
  color: #000;
  height: 800px;
}

.today-schedule,
.week-alert-schedule {
  margin-top: 40%;
}

.group-color-info ul {
  list-style-type: none;
  padding: 0;
}

.color-box {
  display: inline-block;
  width: 15px;
  height: 15px;
  margin-right: 10px;
}

/* FullCalendar 내부 스타일에 영향 주기 위해 deep 사용 */
::v-deep .fc-event {
  font-size: 15px !important;
  color: #555 !important;
  cursor: pointer;
}

::v-deep .holiday-event {
  background-color: #FFCDD2 !important;
  border-color: #FFCDD2 !important;
  color: #B71C1C !important;
}

::v-deep .fc-prev-button,
::v-deep .fc-next-button {
  background: none;
  border: none !important;
  font-size: 14px;
  /* 버튼 텍스트 크기 줄이기 */
  cursor: pointer;
  color: #c2c2c2;
  padding: 4px 8px;
  /* 패딩 추가 */
  width: 24px;
  /* 버튼 너비 */
  height: 24px;
  /* 버튼 높이 */
}

::v-deep .fc-prev-button:hover,
::v-deep .fc-next-button:hover {
  background: none;
  border: none;
  color: #000;
  /* Change text color on hover */
}

::v-deep .fc-prev-button:active,
::v-deep .fc-next-button:active {
  background: none !important;
  color: #000 !important;
  box-shadow: none !important;
  border: none !important;
  outline: none !important;
}

::v-deep .fc-prev-button:focus,
::v-deep .fc-next-button:focus {
  outline: none !important;
  border: none !important;
}


::v-deep .fc-daygrid-day-number {
  color: #000;
  text-decoration: none;
}

::v-deep .fc-daygrid-day-events {
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis; /* 말줄임표 표시 */
}

::v-deep .fc-daygrid-event {
  max-width: 100%; /* 이벤트가 날짜 셀을 넘지 않도록 너비 제한 */
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

::v-deep .fc-col-header-cell-cushion {
  color: #000;
  text-decoration: none;
}

::v-deep .fc-customprev-button,
::v-deep .fc-customnext-button {
  background-color: transparent !important; /* 배경색 투명 */
  border: none !important; /* 테두리 없음 */
  color: #575555 !important; /* 텍스트 색상 */
  font-weight: bold;
  box-shadow: none !important; /* 그림자 제거 */
  outline: none !important; /* 포커스 outline 제거 */
}

/* Hover 및 클릭 시 스타일 */
::v-deep .fc-customprev-button:hover,
::v-deep .fc-customnext-button:hover,
::v-deep .fc-customprev-button:active,
::v-deep .fc-customnext-button:active {
  background-color: transparent !important; /* 배경색 변경 없음 */
  border: none !important; /* 테두리 없음 */
  color: #000 !important; /* 텍스트 색상만 변경 */
  box-shadow: none !important; /* 클릭 시 그림자 제거 */
  outline: none !important; /* 클릭 시 포커스 제거 */
}
</style>