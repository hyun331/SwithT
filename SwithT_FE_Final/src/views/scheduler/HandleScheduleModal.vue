<template>
  <div class="modal-overlay" @click.self="close">
    <div class="modal-content">
      <button class="close-button" @click="close">&times;</button>

      <h2 class="modal-title">
        <span class="mdi mdi-calendar"></span> {{ selectedSchedule ? isEditable?  '일정 수정' : '일정 조회' : '일정 등록' }}
      </h2>

      <form @submit.prevent="handleSubmit" style="text-align: left">
        <!-- 제목 입력 -->
        <label for="title">제목</label>
        <input type="text" v-model="title" :readonly="isReadOnly" required />

        <!-- 날짜 선택 -->
        <label for="schedulerDate">일자</label>
        <input type="date" v-model="schedulerDate" :readonly="isReadOnly" required />

        <!-- 시간 선택 -->
        <label for="schedulerTime">시간</label>
        <input type="time" v-model="schedulerTime" :readonly="isReadOnly" required />

        <!-- 내용 입력 -->
        <label for="content">내용</label>
        <textarea v-model="content" :readonly="isReadOnly" class="content-textarea" required></textarea>

        <!-- 알림 여부 및 시간 설정 -->
        <div class="alert-setting">
          <label for="alertYn" class="alert-label">
            <!-- 알림 여부 문구와 Y/N 표시 -->
            <span>알림 여부:</span>
            <span class="alert-status">{{ alertInfo?.reserveDay ? 'Y' : 'N' }}</span>
            <!-- 알림 취소 버튼 (알림이 있을 때만 표시) -->
            <button v-if="alertInfo?.reserveDay && !isAlertEditable" 
              type="button" 
              @click="cancelAlert" 
              class="alert-remove-btn"
            >
            <span class="mdi mdi-clock-remove-outline"></span>
            </button>
            <!-- 생성 버튼 (알림이 없을 때만 표시, 오른쪽으로 이동) -->
            <button
              v-if="!alertInfo?.reserveDay && !isAlertEditable"
              type="button"
              @click="enableAlertEdit"
              class="alert-create-btn"
            >
              <span class="mdi mdi-clock-plus-outline"></span>
            </button>
          </label>
        
          <!-- 알림 일자 및 저장/수정 취소 버튼 -->
          <p v-if="alertInfo?.reserveDay" class="alert-info">
            알림 일자: {{ alertInfo.reserveDay || '설정되지 않음' }}
        
            <!-- 수정 버튼 -->
            <button
              type="button"
              @click="isAlertEditable ? saveAlertSettings() : enableAlertEdit()"
              :class="isAlertEditable ? 'alert-save-btn' : 'alert-edit-btn'"
            >
              <span v-if="!isAlertEditable" class="mdi mdi-clock-edit-outline"></span>
              <span v-else class="mdi mdi-content-save-check-outline"></span>
            </button>
          </p>
          <div class="alert-time-container">
            <p v-if="alertInfo?.reserveTime">알림 시간: {{ alertInfo.reserveTime || '설정되지 않음' }}</p>
            <button
              v-if="isAlertEditable"
              type="button"
              @click="cancelAlertEdit"
              class="alert-cancel-btn"
            >
              <span class="mdi mdi-arrow-left-bottom"></span>
            </button>
          </div>
  
          <!-- 알림 시간 설정 (수정 모드일 때만 표시) -->
          <div v-if="isAlertEditable" class="alert-time">
            <label><input type="radio" value="1시간 전" v-model="alertTime" /> 1시간 전</label>
            <label><input type="radio" value="10분 전" v-model="alertTime" /> 10분 전</label>
            <label><input type="radio" value="직접 입력" v-model="alertTime" /> 직접 입력</label>
            <input v-if="alertTime === '직접 입력'" type="time" v-model="customAlertTime" />
          </div>
        </div>

        <!-- 버튼들 -->
        <div class="modal-buttons">
          <button v-if="selectedSchedule && isEditable" type="submit">수정 완료</button>
          <button v-if="selectedSchedule && !isEditable && canEdit" type="button" @click="enableEdit">일정 수정하기</button>
          <button v-if="!selectedSchedule" type="submit">등록하기</button>
          <button v-if="selectedSchedule && canEdit" type="button" @click="$emit('scheduleDeleted', selectedSchedule.id)" class="delete-button">일정 삭제하기</button>
        
          <!-- 알림 생성 버튼 (알림이 없을 때만 표시) -->
          <button v-if="isAlertEditable && !alertInfo?.reserveDay" type="button" @click="createAlert">알림 생성</button>
        
          <!-- 알림 수정 버튼 (알림이 있을 때만 표시) -->
        </div>
      </form>
    </div>
  </div>
</template>

<script>
import dayjs from 'dayjs';
import { useToast } from "vue-toastification";

export default {
  props: {
    selectedDate: {
      type: String,
      required: true
    },
    selectedSchedule: {
      type: Object,
      default: null
    },
    alertInfo: {
      type: Object,
      default: () => ({ reserveDay: '', reserveTime: '' }) // 기본값 설정
    },
    canEdit:{
      type:Boolean,
      default:false
    }
  },
  data() {
    return {
      isEditable: false,
      title: this.selectedSchedule ? this.selectedSchedule.title : '',
      schedulerDate: this.selectedSchedule ? this.selectedSchedule.schedulerDate : this.selectedDate,
      schedulerTime: this.selectedSchedule ? this.formatTime(this.selectedSchedule.schedulerTime) : '',
      content: this.selectedSchedule ? this.selectedSchedule.content : '',
      alertYn: this.selectedSchedule ? this.selectedSchedule.alertYn === 'Y' : false, // 기본 알림 여부 설정
      alertTime: '',
      customAlertTime: '',
      isAlertEditable: false, // 알림 수정 모드 여부
      dataToSend: null,
    };
  },
  computed: {
    isReadOnly() {
      return this.selectedSchedule ? !this.isEditable : false;
    }
  },
  methods: {
    formatTime(timeString) {
      return timeString ? timeString.substring(0, 5) : '';
    },
    enableEdit() {
      this.isEditable = true;
    },
    enableAlertEdit() {
      this.isAlertEditable = true; // 알림 수정 또는 생성 모드 활성화
      this.alertYn = true;
    },
    cancelAlertEdit() {
      this.isAlertEditable = false; // 수정 모드 비활성화
    },
    async handleSubmit() {
      const scheduleData = {
        title: this.title,
        schedulerDate: this.schedulerDate,
        schedulerTime: this.schedulerTime,
        content: this.content,
      };

      const alertTimeInMinutes = this.calculateAlertTimeInMinutes();

      // `this.schedulerTime`을 `Date` 객체로 변환하고 `alertTimeInMinutes`을 빼는 계산
      const [hours, minutes] = this.schedulerTime.split(':').map(Number);
      const scheduleDateTime = new Date(this.schedulerDate);
      scheduleDateTime.setHours(hours, minutes);

      // alertTimeInMinutes를 빼기
      scheduleDateTime.setMinutes(scheduleDateTime.getMinutes() - alertTimeInMinutes);

      // 계산된 시간을 "HH:mm" 형식으로 변환
      const calculatedReserveTime = scheduleDateTime.toTimeString().slice(0, 5);

      // dataToSend 설정
      this.dataToSend = { 
        scheduleData,
        alertData: this.alertYn ? {
          scheduleId: this.selectedSchedule?.id || null,
          reserveDay: this.schedulerDate,
          reserveTime: calculatedReserveTime,
        } : null 
      };

      console.log('scheduleSaved', this.dataToSend);
      this.$emit('scheduleSaved', this.dataToSend); // 부모 컴포넌트에 저장된 일정 전송

      // dataToSend 초기화
      this.dataToSend = null;
    },
    calculateAlertTimeInMinutes() {
      if (this.alertTime === '1시간 전') {
        return 60;
      } else if (this.alertTime === '10분 전') {
        return 10;
      } else if (this.customAlertTime) {
        const [customHour, customMinute] = this.customAlertTime.split(':');
        const [scheduleHour, scheduleMinute] = this.schedulerTime.split(':');
        const alertTimeInMinutes = (scheduleHour - customHour) * 60 + (scheduleMinute - customMinute);
        console.log('alertTimeInMinutes:', alertTimeInMinutes);
        return alertTimeInMinutes;
      }
      return 0;
    },
        
    // 알림 생성 메서드
    async createAlert() {
      const toast = useToast(); // Toast 인스턴스 생성

      const alertTimeInMinutes = this.calculateAlertTimeInMinutes();
      const alertTimeValue = dayjs(`${this.schedulerDate}T${this.schedulerTime}`, 'YYYY-MM-DDTHH:mm')
        .subtract(alertTimeInMinutes, 'minute')
        .format('HH:mm');

      const reserveTime = alertTimeValue !== 'Invalid Date' ? alertTimeValue : this.schedulerTime;

      // 현재 시간과 reserveTime의 차이를 계산하여 10분 이내인지 확인
      const now = dayjs();
      const reserveTimeDifference = dayjs(`${this.schedulerDate}T${reserveTime}`, 'YYYY-MM-DDTHH:mm').diff(now, 'minute');
      
      if (reserveTimeDifference < 10) {
        // 예외 발생: 알림 시간이 현재 시간으로부터 10분 미만일 경우
        toast.error("알림 시간은 현재 시간으로부터 최소 10분 이상이어야 합니다.");
        return;
      }

      const alertData = {
        scheduleId: this.selectedSchedule?.id || null, // 스케줄 ID
        reserveDay: this.schedulerDate,                // 알림 날짜
        reserveTime: reserveTime                       // 계산된 알림 시간
      };

      console.log("Alert Create Data:", alertData);

      this.$emit('createAlert', alertData);  // 부모 컴포넌트로 알림 생성 데이터 전송
    },

    // 알림 수정 메서드
    async saveAlertSettings() {
      const toast = useToast(); // Toast 인스턴스 생성

      const alertTimeInMinutes = this.calculateAlertTimeInMinutes();
      const alertTimeValue = dayjs(`${this.schedulerDate}T${this.schedulerTime}`, 'YYYY-MM-DDTHH:mm')
        .subtract(alertTimeInMinutes, 'minute')
        .format('HH:mm');

      const reserveTime = alertTimeValue !== 'Invalid Date' ? alertTimeValue : this.schedulerTime;
      
      // 현재 시간과 reserveTime의 차이를 계산하여 10분 이내인지 확인
      const now = dayjs();
      const reserveTimeDifference = dayjs(`${this.schedulerDate}T${reserveTime}`, 'YYYY-MM-DDTHH:mm').diff(now, 'minute');
      
      if (reserveTimeDifference < 10) {
        // 예외 발생: 알림 시간이 현재 시간으로부터 10분 미만일 경우
        toast.error("알림 시간은 현재 시간으로부터 최소 10분 이상이어야 합니다.");
        return;
      }

      const alertData = {
        scheduleId: this.selectedSchedule?.id || null,  // 스케줄 ID
        newReserveDay: this.schedulerDate,              // 새로 선택된 알림 날짜
        newReserveTime: reserveTime                     // 계산된 알림 시간
      };

      console.log("Alert Update Data:", alertData);

      this.$emit('saveAlert', alertData); // 부모 컴포넌트로 알림 수정 데이터 전송
      this.isAlertEditable = false; // 저장 후 수정 모드 비활성화
    },
    async cancelAlert() {
      const alertData = { alertId: this.alertInfo?.id };
      if (alertData.alertId) {
        this.$emit('cancelAlert', alertData); // 부모 컴포넌트에 알림 취소 요청 전송
      }
    },
    close() {
      this.$emit('close'); // 모달 닫기 이벤트 발생
      this.isEditable = false;
      this.isAlertEditable = false;
    },
    async fetchAlertInfo() {
      console.log("Selected Schedule inside fetchAlertInfo:", this.selectedSchedule);
      if (this.selectedSchedule && (this.selectedSchedule.alertYn === 'Y' || this.selectedSchedule.alertYn === true)) {
        this.alertYn = true;
        console.log("schedulerDate!!!!", this.selectedSchedule);
      } else {
        console.log("No Alert Info Found: alertYn is", this.selectedSchedule.alertYn);
      }
    }
  },
  mounted() {
    console.log("Mounted Hook Called");
    if (this.selectedSchedule) {
      console.log("Selected Schedule on Mount:", this.selectedSchedule);
      this.fetchAlertInfo();
    }
  }
};
</script>

<style scoped>
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.6);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.modal-content {
  background-color: #fff;
  padding: 15px;
  border-radius: 10px;
  max-width: 400px;
  width: 90%;
  max-height: 80vh;
  overflow-y: auto;
  position: relative;
}

.modal-title {
  display: flex;
  align-items: center;
  font-size: 20px;
  margin-bottom: 15px;
}

.modal-title .mdi-calendar {
  font-size: 28px;
  margin-right: 8px;
}

label {
  display: block;
  margin-bottom: 6px;
}

input[type="text"],
input[type="date"],
input[type="time"],
textarea {
  width: calc(100% - 8px);
  padding: 8px;
  margin-bottom: 15px;
  border-radius: 5px;
  border: 1px solid #ccc;
}

textarea.content-textarea {
  height: 100px;
  max-height: 100px;
  resize: none;
}

.alert-setting {
  background: #ccc;
  padding: 15px;
  margin-bottom: 20px;
}

.alert-time {
  margin-top: 10px;
}

.alert-time label {
  display: inline-block;
  margin-right: 15px;
}

.modal-buttons {
  display: flex;
  justify-content: space-between;
}

.modal-buttons button {
  padding: 8px 12px;
  border: none;
  border-radius: 5px;
  background-color: #f0f0f0;
  cursor: pointer;
}

.modal-buttons button[type="submit"] {
  background-color: #007bff;
  color: white;
}

.modal-buttons button.delete-button {
  background-color: #dc3545;
  color: white;
}

.close-button {
  position: absolute;
  top: 10px;
  right: 10px;
  background: none;
  border: none;
  font-size: 24px;
  cursor: pointer;
}

.alert-label {
  display: flex;
  align-items: center;
}

.alert-status {
  font-weight: bold;
  margin-left: 5px;
}

.alert-create-btn {
  margin-left: auto; 
  font-size: 2.0em;
}

.alert-info {
  display: flex;
  align-items: center;
}

.alert-save-btn,
.alert-edit-btn {
  margin-left: auto;
  font-size: 2.0em;
}

.alert-remove-btn{
  margin-left: auto;
  font-size: 2.0em;
}

.alert-time-container {
  display: flex;
  align-items: center; /* 텍스트와 버튼을 수직 중앙에 정렬 */
  gap: 10px;
}

.alert-time-container p {
  margin: 0; /* 여백을 제거하여 높이 조정 */
  line-height: 1.5; /* 버튼 높이와 일치하도록 설정 */
  font-size: 16px; /* 버튼 텍스트 크기와 동일하게 조정 */
}

.alert-cancel-btn {
  padding: 5px 10px; /* 버튼 내부 여백 설정 */
  font-size: 2.0em;
  height: auto; /* 버튼 높이 자동 설정 */
  display: flex;
  margin-left: auto;
  align-items: center;
}

</style>