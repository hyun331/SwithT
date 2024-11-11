<template>
  <v-container>
    <h2 style="font-weight: bold">강의 개설</h2>
    <hr style="margin: 40px 0">
    <v-form>
      <!-- 수업 방식 -->
      <v-row class="form-group align-center">
        <v-col cols="3" class="d-flex align-center justify-center">
          <label>수업 방식</label>
        </v-col>
        <v-col cols="8">
          <div class="custom-select-box">
            <transition name="fade">
              <div style="display: flex; flex-wrap: wrap; gap: 8px">
                <div v-for="method in teachingMethods" :key="method.value"
                  :class="['custom-option', teachingMethod === method.value ? 'selected' : '']"
                  @click="teachingMethod = method.value">
                  {{ method.label }}
                </div>
              </div>
            </transition>
          </div>
        </v-col>
      </v-row>

      <!-- 분야 -->
      <v-row class="form-group align-center">
        <v-col cols="3" class="d-flex align-center justify-center">
          <label>분야</label>
        </v-col>
        <v-col cols="8">
          <div class="custom-select-box">
            <transition name="fade">
              <div style="display: flex; flex-wrap: wrap; gap: 8px;">
                <div v-for="option in fieldOptions" :key="option.value"
                  :class="['custom-option', field === option.value ? 'selected' : '']" @click="field = option.value">
                  {{ option.label }}
                </div>
              </div>
            </transition>

          </div>
        </v-col>
      </v-row>

      <!-- 강의명 -->
      <v-row class="form-group align-center">
        <v-col cols="3" class="d-flex align-center justify-center">
          <label>강의명</label><br />
        </v-col>
        <v-col cols="8">
          <input v-model="title" class="form-control" placeholder="강의명을 입력해주세요(최대 50자)" type="text" maxlength="50"/>
        </v-col>
      </v-row>

      <!-- 썸네일 -->
      <v-row class="form-group align-center">
        <v-col cols="3" class="d-flex align-center justify-center">
          <label>썸네일</label>
        </v-col>
        <v-col cols="8">
          <input type="file" id="thumbnail" @change="onFileChange" accept="image/*" class="form-control" />
        </v-col>
      </v-row>

      <!-- 강의 정보 -->
      <v-row class="form-group align-center">
        <v-col cols="3" class="d-flex align-center justify-center">
          <label>강의 소개</label>
        </v-col>
        <v-col cols="8">
          <textarea v-model="contents" class="form-control" placeholder="강의 소개를 입력해주세요" style="height: 30vh" />
        </v-col>
      </v-row>


    </v-form>


    <div class="section">
      <v-row style="margin-top: 100px;">
        <v-col cols="3" class="d-flex justify-center">
          <label style="margin-top: 40px;">강의 그룹</label>
        </v-col>
        <v-col cols="8">
          <div class="align-center" style="width: 100%;">
            <!-- 강의 그룹 표시 -->
            <div class="lecture-group-list">
              <div v-if="lectureGroups.length === 0" style="text-align: center; color: #888; margin: 20px;">
                생성한 강의 그룹이 없습니다
              </div>
              <div v-else>
                <div v-for="(lectureGroup, index) in lectureGroups" :key="lectureGroup.name" class="lecture-group">
                  <v-row>
                    <v-col cols="3" class="d-flex align-center justify-center">
                      <label>강의 그룹 {{ index + 1 }}</label> <!-- 강의 그룹 순서 표시 -->
                    </v-col>
                    <v-col cols="7">
                      <div v-if="this.teachingMethod === 'LECTURE'">
                        <v-row>
                          <v-col cols="4" class="d-flex align-center justify-center">
                            <label style="font-size: 13px; color: #555;">강의 위치</label>
                          </v-col>
                          <v-col class="d-flex align-center justify-start">
                            {{ lectureGroup.address }}{{ lectureGroup.detailAddress }}
                          </v-col>
                        </v-row>
                        <v-row>
                          <v-col cols="4" class="d-flex align-center justify-center">
                            <label style="font-size: 13px; color: #555;">강의기간</label>
                          </v-col>
                          <v-col class="d-flex align-center justify-start">
                            {{ lectureGroup.startDate }} ~ {{ lectureGroup.endDate }}
                          </v-col>
                        </v-row>
                      </div>

                      <v-row>
                        <v-col cols="4" class="d-flex align-center justify-center">
                          <label style="font-size: 13px; color: #555;">강의료/인원</label>
                        </v-col>
                        <v-col class="d-flex align-center justify-start">
                          {{ lectureGroup.fee }}원 (인원: {{ lectureGroup.capacity }})
                        </v-col>
                      </v-row>
                      <v-row>
                        <v-col cols="4" class="d-flex align-center justify-center">
                          <label style="font-size: 13px; color: #555;">강의 시간</label>
                        </v-col>
                        <v-col class="d-flex align-center justify-start">
                          <div>
                            <div v-for="(slot, slotIndex) in lectureGroup.timeSlots" :key="slotIndex">
                              {{ slot.day }}요일 {{ slot.startTime }} ~ {{ slot.endTime }}
                            </div>
                          </div>
                        </v-col>
                      </v-row>
                    </v-col>
                    <v-col cols="2" class="d-flex align-center justify-center">
                      <button @click="removeLectureGroup(index)" class="btn btn-danger btn-sm" style="margin: 0 10px;">
                        <span class="mdi mdi-trash-can-outline" style="font-size: 20px"></span>
                      </button>
                    </v-col>
                  </v-row>
                </div>
              </div>
            </div>
          </div>
          <hr />

          <v-row>
            <!-- 시간표 -->
            <v-col cols="4" class="d-flex">
              <table border="3" class="time-table">
                <thead>
                  <tr>
                    <th>시간</th>
                    <th>월</th>
                    <th>화</th>
                    <th>수</th>
                    <th>목</th>
                    <th>금</th>
                    <th>토</th>
                    <th>일</th>
                  </tr>
                </thead>
                <tbody>
                  <tr v-for="(hour, index) in hours" :key="index">
                    <td>{{ hour }}</td>
                    <td v-for="day in days" :key="day"
                      :style="{ backgroundColor: schedule[day] && schedule[day][index] ? schedule[day][index].color : '' }">
                      <div v-if="schedule[day] && schedule[day][index]" style="color: #fff; font-weight: bold;">
                        {{ schedule[day][index].index }}
                      </div>
                    </td>
                  </tr>
                </tbody>
              </table>
            </v-col>

            <v-col cols="8">
              <div v-if="!showForm" @click="addLectureGroup" class="time-form time-add"
                style="padding: 10px 0; display: flex; align-items: center; justify-content: center;">
                <span class="material-icons-outlined" style="margin-right: 5px;">
                  add_box
                </span>
                <span style="padding: 10px 0; display: flex; align-items: center; justify-content: center;">
                  강의 그룹 생성
                </span>
              </div>

              <!-- 강의 그룹 정보 입력 폼 -->
              <div v-if="showForm" class="form">

                <!-- 강의 주소, 시작일, 종료일 -->
                <v-row v-if="teachingMethod === 'LECTURE'" class="form-group align-center" style="margin-top: 10px">
                  <v-col cols="3" class="d-flex align-center justify-center">
                    <label style="font-size: 15px">강의 주소</label>
                  </v-col>
                  <v-col cols="9">
                    <v-row style="padding: 12px;" align="center">
                      <v-btn style="border: 1px solid #ccc;" variant="outlined"
                        @click="updateAddress()"><v-icon>mdi-map-search</v-icon> 주소 검색</v-btn>
                      <div style="margin-left: 10px; font-weight: 700;">{{ currentLecture.address }}</div>
                    </v-row>
                    <v-row style="padding: 0px 12px 12px;">
                      <input v-model="currentLecture.detailAddress" class="form-control" placeholder="상세주소를 입력해주세요"
                        type="text" />
                    </v-row>
                  </v-col>
                </v-row>

                <v-row v-if="teachingMethod === 'LECTURE'" class="form-group align-center">
                  <v-col cols="3" class="d-flex align-center justify-center">
                    <label style="font-size: 15px">강의 기간</label>
                  </v-col>
                  <v-col cols="4">
                    <input v-model="currentLecture.startDate" class="form-control" type="date" />
                  </v-col>
                  <v-col cols="1">
                    <span>~</span>
                  </v-col>
                  <v-col cols="4">
                    <input v-model="currentLecture.endDate" class="form-control" type="date" />
                  </v-col>
                </v-row>

                <v-row class="form-group align-center">
                  <v-col cols="3" class="d-flex align-center justify-center">
                    <label class="label-width" style="font-size: 15px">강의료</label>
                  </v-col>
                  <v-col cols="6">
                    <input v-model="currentLecture.fee" type="money" placeholder="강의료를 입력하세요"
                      class="form-control input-width" />
                  </v-col>
                </v-row>

                <v-row>
                  <v-col cols="3" class="d-flex align-center justify-center">
                    <label class="label-width" style="font-size: 15px">모집 인원</label>
                  </v-col>
                  <v-col cols="6">
                    <input v-model="currentLecture.capacity" :disabled="teachingMethod === 'LESSON'" type="number"
                      placeholder="모집 인원을 입력하세요" class="form-control input-width" />
                  </v-col>
                </v-row>

                <!-- 시간 및 요일 추가 -->
                <div v-for="(timeSlot, index) in currentLecture.timeSlots" :key="index" class="time-form">
                  <v-row>
                    <v-col cols="3" class="d-flex align-center justify-center">
                      <label style="margin: 0 10px; font-size: 15px">강의 시간</label>
                    </v-col>
                    <v-col>
                      <v-row>
                        <div v-for="day in days" :key="day"
                          :class="['custom-option-day', timeSlot.day === day ? 'selected' : '']"
                          @click="timeSlot.day = day" style="margin: 0 3px">
                          {{ day }}
                        </div>
                      </v-row>
                      <v-row style="margin-top: 30px;">
                        <select v-model="timeSlot.startTime" class="form-control" style="margin: 0 3px; width: 100px;">
                          <option value="" disabled selected>시작 시간</option>
                          <option v-for="hour in hours" :key="hour" :value="hour">{{ hour }}</option>
                        </select>
                        <span style="margin: 0 10px; font-weight: bold; font-size: 18px;">~</span>
                        <select v-model="timeSlot.endTime" class="form-control" style="margin: 0 3px; width: 100px;">
                          <option value="" disabled selected>종료 시간</option>
                          <option v-for="hour in hours" :key="hour" :value="hour">{{ hour }}</option>
                        </select>
                      </v-row>
                    </v-col>
                    <v-col cols="2" class="d-flex align-center justify-center minus-btn">
                      <span class="mdi mdi-minus-circle-outline" style="font-size: 25px; color: #666"
                        @click="removeTimeSlot(index)"></span>
                    </v-col>
                  </v-row>
                </div>

                <div @click="addTimeSlot" class="time-form time-add"
                  style="padding: 10px 0; display: flex; align-items: center; justify-content: center;">
                  <span class="material-icons-outlined" style="margin-right: 5px;">
                    add_box
                  </span>
                  <span style="padding: 10px 0; display: flex; align-items: center; justify-content: center;">
                    강의 시간 추가
                  </span>
                </div>
                <v-row class="justify-end" style="padding: 0 10px;">
                  <div @click="submitLectureGroup" class="submit-group">그룹 생성</div>
                </v-row>
              </div>
            </v-col>
          </v-row>
        </v-col>



      </v-row>
      <v-row class="justify-center" style="padding: 0 10px">
        <div @click="createLecture" class="create-lecture">개설 신청</div>
      </v-row>

      <v-snackbar v-model="snackbar.visible" :color="snackbar.color" timeout="5000">
        {{ snackbar.message }}
        <template v-slot:action="{ attrs }">
          <v-btn color="white" text v-bind="attrs" @click="snackbar.visible = false">닫기</v-btn>
        </template>
      </v-snackbar>
    </div>
    <AlertModal v-model="alertModal" @update:dialog="alertModal = $event" icon=mdi-alert-circle-outline
      :title=this.alertModalTitle :contents=this.alertModalContents />
  </v-container>


</template>

<script>
/* global kakao */

import 'bootstrap/dist/css/bootstrap.css'
import 'bootstrap-vue-3/dist/bootstrap-vue-3.css'
import AlertModal from "@/components/AlertModal.vue";

import axios from 'axios';

export default {
  components: {
    AlertModal
  },
  data() {
    return {
      isLogin: false,
      address: null,
      detailAddress: "",
      startDate: null,
      endDate: null,
      userName: null,
      title: '',
      teachingMethod: '', // Make sure this is defined
      field: '',
      teachingMethods: [
        { value: 'LECTURE', label: '강의' },
        { value: 'LESSON', label: '과외' },
      ],
      fieldOptions: [
        { value: 'ADMISSION', label: '입시' },
        { value: 'CAREER', label: '취업 & 직무' },
        { value: 'HOBBY', label: '취미' },
        { value: 'DEVELOPMENT', label: '자기계발' }
      ],
      thumbnail: null,
      showForm: false,
      currentLecture: {
        name: '',
        fee: 0,
        capacity: 0,
        address: '', // 주소 추가
        detailAddress: '', // 상세주소 추가
        startDate: null, // 시작일 추가
        endDate: null, // 종료일 추가
        timeSlots: [
          { day: '', startTime: '', endTime: '' }
        ]
      },
      lectureGroups: [], // 여러 강의를 저장
      days: ['월', '화', '수', '목', '금', '토', '일'],
      hours: [
        '06:00', '07:00', '08:00', '09:00', '10:00', '11:00',
        '12:00', '13:00', '14:00', '15:00', '16:00', '17:00',
        '18:00', '19:00', '20:00', '21:00', '22:00', '23:00', '24:00'
      ],
      schedule: {},
      snackbar: {
        visible: false,
        message: '',
        color: '#6C97FD'
      },
      alertModal: false,
      alertModalTitle: "",
      alertModalContents: "",
    };
  },
  mounted() {
    this.loadDaumPostcodeScript();
    this.loadKakaoMapScript();
  },
  async created() {
    const token = localStorage.getItem('token');
    if (token) {
      this.userName = localStorage.getItem('name');
    }
  },
  methods: {
    loadDaumPostcodeScript() {
      const script = document.createElement('script');
      script.src = '//t1.daumcdn.net/mapjsapi/bundle/postcode/prod/postcode.v2.js';
      script.onload = () => {
        this.isDaumScriptLoaded = true;
      };
      document.head.appendChild(script);
    },
    loadKakaoMapScript() {
      const script = document.createElement('script');
      script.src = `//dapi.kakao.com/v2/maps/sdk.js?appkey=03a055c21377bee26ab1559dedf4af6f&libraries=services&autoload=false`;
      script.onload = () => {
        window.kakao.maps.load(() => {
          this.isKakaoScriptLoaded = true;
        });
      };
      document.head.appendChild(script);
    },
    updateAddress() {
      if (window.daum && window.daum.Postcode) {
        // eslint-disable-next-line no-undef
        new daum.Postcode({
          oncomplete: (data) => {
            this.currentLecture.address = data?.roadAddress;

            // 주소 검색한 거 기반으로 위도 경도
            // 좌표 검색을 위해 Kakao 지도 Geocoder 사용
            const geocoder = new kakao.maps.services.Geocoder();
            geocoder.addressSearch(this.roadAddress, (result, status) => {
              if (status === kakao.maps.services.Status.OK) {
                console.log('위도 : ' + result[0].y);
                console.log('경도 : ' + result[0].x);

                // 지도에 마커를 추가하는 로직
                this.initMap(result[0].y, result[0].x);
              }
            });
          }
        }).open();
      } else {
        console.error("Daum Postcode 스크립트가 로드되지 않았습니다.");
      }
    },
    onFileChange(event) {
      const file = event.target.files[0];
      if (file) {
        this.thumbnail = file;
      }
    },
    submitForm() {
      // 여기서 폼 제출 로직 처리
      console.log({
        title: this.title,
        teachingMethod: this.teachingMethod,
        field: this.field,
        thumbnail: this.thumbnail
      });
    },
    addLectureGroup() {
      this.showForm = true;
      this.currentLecture = {
        name: '',
        fee: 0,
        capacity: 1,
        address: '', // 주소 추가
        detailAddress: '', // 상세주소 추가
        startDate: null, // 시작일 추가
        endDate: null, // 종료일 추가
        timeSlots: [{ day: '', startTime: '', endTime: '' }]
      };
    },
    addTimeSlot() {
      this.currentLecture.timeSlots.push({ day: '', startTime: '', endTime: '' });
    },
    getRandomColor() {
      const colors = [
        '#d0e2ff', '#9ec5fe', '#6ea8fe',
        '#3d8bfd', '#0d6efd', '#2f6fd4', '#bad2f8',
        '#abc3ea', '#7fa3dd', '#5982c4', '#426caf',

      ];
      const randomIndex = Math.floor(Math.random() * colors.length);
      return colors[randomIndex];
    },
    isOverlapping(day, startHour, endHour) {
      if (!this.schedule[day]) {
        return false;
      }

      for (let i = startHour; i < endHour; i++) {
        if (this.schedule[day][i]) {
          return true;
        }
      }

      return false;
    },
    submitLectureGroup() {
      // 모집 인원이 0인지 확인
      if (this.currentLecture.capacity <= 0) {
        this.showSnackbar('모집인원은 0명이 될 수 없습니다.');
        return; // 조건에 맞지 않으면 함수 종료
      }
      // 강의 시간이 올바르게 입력되었는지 확인
      for (const timeSlot of this.currentLecture.timeSlots) {
        if (!timeSlot.day || !timeSlot.startTime || !timeSlot.endTime) {
          this.showSnackbar('모든 정보를 입력해야 합니다.');
          return; // 조건에 맞지 않으면 함수 종료
        }
      }

      const color = this.getRandomColor();
      let hasOverlap = false; // 겹치는 강의 체크

      for (const timeSlot of this.currentLecture.timeSlots) {
        const startHour = this.hours.indexOf(timeSlot.startTime);
        const endHour = this.hours.indexOf(timeSlot.endTime);
        const day = timeSlot.day;

        // 겹치는 시간 확인
        if (this.isOverlapping(day, startHour, endHour)) {
          hasOverlap = true; // 겹치는 경우 플래그 설정
          break; // 더 이상 체크하지 않음
        }
      }

      if (hasOverlap) {
        this.alertModalTtile = "강의 신청 시간 중복";
        this.alertModalContents = "겹치는 강의 시간이 존재합니다. 다른 시간을 선택해주세요.";
        this.alertModal = true;
        return; // 겹치는 경우 강의 추가 중단
      }

      // 색상 추가: 모든 타임 슬롯에 대해 겹치지 않는 경우에만 색상 추가
      for (const timeSlot of this.currentLecture.timeSlots) {
        const startHour = this.hours.indexOf(timeSlot.startTime);
        const endHour = this.hours.indexOf(timeSlot.endTime);
        const day = timeSlot.day;

        if (!this.schedule[day]) {
          this.schedule[day] = {};
        }

        for (let i = startHour; i < endHour; i++) {
          this.schedule[day][i] = {
            name: this.currentLecture.name,
            color: color,
          };
        }
      }

      this.lectureGroups.push({ ...this.currentLecture }); // 강의 목록에 추가
      this.showForm = false; // 폼 닫기
    },
    formatCurrency(value) {
      return value.replace(/\B(?=(\d{3})+(?!\d))/g, ",");
    },

    // 한글 요일을 영어 요일로 변환하는 함수
    convertDayToEnglish(day) {
      const dayMapping = {
        '월': 'MONDAY',
        '화': 'TUESDAY',
        '수': 'WEDNESDAY',
        '목': 'THURSDAY',
        '금': 'FRIDAY',
        '토': 'SATURDAY',
        '일': 'SUNDAY'
      };
      return dayMapping[day];
    },

    // 날짜와 시간을 LocalDateTime 형식으로 변환하는 함수
    convertToLocalDateTime(day, startTime, date) {
      // date는 'YYYY-MM-DD' 형식으로 받아온다고 가정
      const dateObj = new Date(date); // 주어진 날짜를 기준으로 Date 객체 생성

      // startTime을 시간:분 형식으로 분리
      const [hour, minute] = startTime.split(':');
      dateObj.setHours(hour, minute, 0); // 시간을 설정

      // LocalDateTime 형식으로 변환 (YYYY-MM-DDTHH:mm:ss)
      const localDateTime = dateObj.toISOString().replace('Z', ''); // UTC 포맷을 제거하여 ISO 형식 유지
      return localDateTime;
    },

    async createLecture() {

      if (this.title === '' || this.contents === '' || this.field === '' || this.teachingMethod === '') {
        this.showSnackbar('강의 정보를 입력해야 합니다.');
        return; // 강의 그룹이 없을 경우 함수 종료
      }
      if (this.lectureGroups.length === 0) {
        this.showSnackbar('강의 그룹을 추가해야 합니다.');
        return; // 강의 그룹이 없을 경우 함수 종료
      }

      // 강의 생성 요청 객체 정의
      const lectureCreateReqDto = {
        lectureReqDto: {
          memberName: this.userName,
          title: this.title,
          contents: this.contents,
          status: "STANDBY",  // 기본값 설정
          category: this.field,
          lectureType: this.teachingMethod
        },
        lectureGroupReqDtos: this.lectureGroups.map(group => ({
          price: (group.fee && group.fee.replace(/,/g, '')) || '0',
          limitPeople: group.capacity,
          address: group.address,
          detailAddress: group.detailAddress,
          startDate: group.startDate,
          endDate: group.endDate,
          groupTimeReqDtos: group.timeSlots.map(slot => ({
            // 요일을 영어로 변환
            lectureDay: this.convertDayToEnglish(slot.day),
            // 시작 시간과 종료 시간을 LocalDateTime 형식으로 변환
            startTime: slot.startTime === "24:00" ? "00:00" : slot.startTime, // 24:00을 00:00으로 변환
            endTime: slot.endTime === "24:00" ? "00:00" : slot.endTime // 24:00을 00:00으로 변환
          }))
        }))
      };

      // FormData를 사용하여 데이터와 파일을 함께 전송
      const formData = new FormData();
      formData.append('data', new Blob([JSON.stringify(lectureCreateReqDto)], { type: 'application/json' }));

      if (this.thumbnail) {
        formData.append('file', this.thumbnail); // 이미지 파일 추가
      }

      try {
        const token = localStorage.getItem('token');
        const response = await axios.post(
          `${process.env.VUE_APP_API_BASE_URL}/lecture-service/create`,
          formData,
          {
            headers: {
              'Authorization': `Bearer ${token}`, // 토큰 설정
              'Content-Type': 'multipart/form-data' // FormData 전송 형식 설정
            }
          }
        );
        console.log(formData);
        if (response.status === 200) {
          this.showSnackbar('강의가 성공적으로 생성되었습니다!', 'success');

          window.location.href = "/tutor-applied-list";
        }
      } catch (e) {
        console.error('강의 생성 중 오류 발생:', e);
        this.showSnackbar('강의 생성에 실패했습니다. 다시 시도해주세요.', 'error');
      }
    },
    showSnackbar(message, color) {
      this.snackbar.message = message;
      this.snackbar.color = color;
      this.snackbar.visible = true;
    },
    removeLectureGroup(index) {
      const removedGroup = this.lectureGroups[index];

      // 스케줄에서 삭제된 강의 그룹의 시간 슬롯 제거
      removedGroup.timeSlots.forEach(slot => {
        const startHour = this.hours.indexOf(slot.startTime);
        const endHour = this.hours.indexOf(slot.endTime);
        const day = slot.day;

        // 해당 시간 슬롯을 null로 설정
        if (this.schedule[day]) {
          for (let i = startHour; i < endHour; i++) {
            if (this.schedule[day][i] && this.schedule[day][i].name === removedGroup.name) {
              delete this.schedule[day][i];
            }
          }
        }
      });

      // 강의 그룹 리스트에서 삭제
      this.lectureGroups.splice(index, 1);
    },
    removeTimeSlot(index) {
      this.currentLecture.timeSlots.splice(index, 1);
    },
  },

  watch: {
    'currentLecture.fee'(newValue) {
      if (typeof newValue === 'number' || typeof newValue === 'string') {
        // 숫자만 추출
        let numericValue = newValue.toString().replace(/[^0-9]/g, '');

        // 가격이 0으로 시작하면 그걸 방지하는 로직
        if (numericValue.length > 1 && numericValue.startsWith('0')) {
          numericValue = numericValue.replace(/^0+/, ''); // 선행 0 제거
        }

        // 쉼표 추가한 값을 업데이트
        this.currentLecture.fee = this.formatCurrency(numericValue);
      }
    },
  }
};
</script>

<style scoped>
.v-container {
  margin-top: 60px;
  width: 70vw;
}

.section {
  margin: 50px 0;
}

.time-table {
  position: sticky;
  top: 60px;
  border-collapse: collapse;
  height: 555px;
}

table {
  width: 20%;
  border-collapse: collapse;
  border: 2px solid #cdcdcd !important;
}

th,
td {
  padding: 5px;
  text-align: center;
  font-size: 11px;
  border: 1px solid #e6e4e4 !important;

}

.form {
  margin-top: 20px;
}

label {
  font-weight: bold;
  font-size: 17px;
}

td {
  height: 10px;

}

.lecture-group {
  border: 1px solid #cdcdcd;
  padding: 10px;
  margin-bottom: 20px;
  border-radius: 5px;
  background-color: #f9f9f9;
}

.time-form {
  border: 1px solid #e1e1e1;
  border-radius: 10px;
  margin: 30px 0;
  padding: 40px 0;
}

.time-form select {
  width: 120px;
  display: inline-block;
}

.time-add:hover {
  cursor: pointer;
}

.custom-select-box {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.custom-option {
  padding: 10px;
  border: 1px solid #ccc;
  border-radius: 4px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.custom-option:hover {
  cursor: pointer;
  transform: scale(1.05);
}

.custom-option.selected {
  border-color: #007bff;
  background-color: #e7f0ff;
  transform: scale(1.05);
}

.custom-option-day {
  padding: 7px;
  border: 1px solid #ccc;
  border-radius: 4px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.custom-option-day:hover {
  cursor: pointer;
  transform: scale(1.05);
}

.custom-option-day.selected {
  border-color: #007bff;
  background-color: #e7f0ff;
  transform: scale(1.05);
}

.label-width {
  width: 120px;
}

.input-width {
  width: 100px;
}

.submit-group {
  background-color: #78CB67;
  color: #f5f5f5;
  width: 100px;
  height: 40px;
  line-height: 40px;
  border-radius: 10px;
  right: 0;
  font-weight: bold;
}

.submit-group:hover {
  cursor: pointer;
}

.create-lecture {
  background-color: #0d6efd;
  color: #f5f5f5;
  font-size: 16px;
  font-weight: bold;
  height: 50px;
  width: 300px;
  border-radius: 10px;
  line-height: 50px;
  margin-top: 120px;
  transition: all 0.3s ease;
}

.create-lecture:hover {
  cursor: pointer;
  transform: scale(1.05);
}

.minus-btn:hover {
  cursor: pointer;
}
</style>