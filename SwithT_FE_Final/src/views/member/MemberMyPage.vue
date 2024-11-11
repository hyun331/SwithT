<template>
  <div class="container" style="margin-top: 105px;">
    <div class="row">

      <div class="col-md-4">
        <div class="card">
          <div class="card-body text-center" :style="{ minHeight: cardMinHeight }">
            <img :src="memberInfo.profileImage || 'https://via.placeholder.com/150'" class="rounded-circle mb-3"
              alt="Profile" style="width: 150px" />
            <!-- 여기 나중에 수정하는거 고려해보기 -->
            <h4>{{ memberInfo.name }}</h4>
            <p>{{ "반갑습니다 " + memberInfo.name + "님" }}</p>
            <hr />
            <div class="text-left">
            </div>
            <a href="#" class="btn btn-block mt-0 ml-5" :style="{ backgroundColor: '#6C97FD', color: 'white' }"
              @click="selectImage">프로필 이미지 수정</a>
            <input type="file" ref="fileInput" @change="onFileChange" style="display: none" accept="image/*" />
          </div>
        </div>
      </div>

      <div class="col-md-8">
        <div class="card">
          <div class="card-body">
            <h5 class="card-title">회원 정보</h5>
            <ul class="nav nav-tabs mb-3">
              <li class="nav-item">
                <a class="nav-link active" href="#">계정 정보</a>
              </li>
            </ul>

            <form @submit.prevent="updateProfile">
              <div class="row">
                <div class="col-md-12 mb-3 d-flex align-items-center">
                  <label for="name" class="mb-0 me-3" style="width: 100px;">이름</label>
                  <input type="text" class="form-control" id="name" v-model="memberInfo.name" :style="{
                    width: '300px',
                    backgroundColor: 'white',
                    color: '#000',
                    borderRadius: '0',
                    border: isEditing ? '1px solid #6C97FD' : 'none' // 수정 모드에서 테두리 표시
                  }" :disabled="!isEditing" required />
                </div>
                <div class="col-md-12 mb-3 d-flex align-items-center">
                  <label for="phoneNumber" class="mb-0 me-3" style="width: 100px;">전화번호</label>
                  <input type="tel" class="form-control" id="phoneNumber" v-model="memberInfo.phoneNumber"
                    :disabled="!isEditing" :style="{
                      width: '300px',
                      backgroundColor: 'white',
                      color: '#000',
                      borderRadius: '0',
                      border: isEditing ? '1px solid #6C97FD' : 'none' // 수정 모드에서 테두리 표시
                    }" required />
                </div>
                <div class="col-md-12 mb-3 d-flex align-items-center">
                  <label for="email" class="mb-0 me-3" style="width: 100px;">이메일</label>
                  <input type="email" class="form-control" id="email" v-model="memberInfo.email" :style="{
                    width: '300px',
                    backgroundColor: 'white',
                    color: '#000',
                    borderRadius: '0',
                    border: 'none'
                  }" disabled required />
                </div>
                <div class="col-md-12 mb-3 d-flex align-items-center">
                  <label for="address" class="mb-0 me-3" style="width: 100px;">주소</label>
                  <div v-if="!isEditing" class="ml-3 mr-1">{{ this.memberInfo.address }}</div>
                  <div v-if="!isEditing">{{ this.memberInfo.detailAddress }}</div>
                  <v-btn v-if="isEditing" style="border: 1px solid #ccc;" variant="outlined" @click="updateAddress()">
                    <v-icon>mdi-map-search</v-icon> 주소 검색
                  </v-btn>
                  <input type="text" class="form-control" id="address" v-model="memberInfo.address" v-if="isEditing"
                    disabled :style="{
                      width: '300px',
                      backgroundColor: 'white',
                      color: '#000',
                      borderRadius: '0',
                      border: 'none'
                    }" required />

                </div>
                <div class="col-md-12 mb-3 d-flex align-items-center" v-if="isEditing">
                  <label for="address" class="mb-0 me-3" style="width: 100px;"> 상세 주소</label>
                  <input type="text" class="form-control" id="detailAddress" v-model="memberInfo.detailAddress"
                    :disabled="!isEditing" v-if="isEditing" :style="{
                      width: '300px',
                      backgroundColor: 'white',
                      color: '#000',
                      borderRadius: '0',
                      border: isEditing ? '1px solid #6C97FD' : 'none' // 수정 모드에서 테두리 표시
                    }" required />
                </div>
                <div class="col-md-12 mb-3 d-flex align-items-center" v-if="role === 'TUTOR'">
                  <label for="education" class="mb-0 me-3" style="width: 100px;">학력</label>
                  <input type="text" class="form-control" id="education" v-model="memberInfo.education"
                    :disabled="!isEditing" :style="{
                      width: '300px',
                      backgroundColor: 'white',
                      color: '#000',
                      borderRadius: '0',
                      border: isEditing ? '1px solid #6C97FD' : 'none' // 수정 모드에서 테두리 표시
                    }" required />
                </div>
                <div class="col-md-12 mb-3 d-flex align-items-center">
                  <label for="gender" class="mb-0 me-3" style="width: 100px;">성별</label>
                  <input type="text" class="form-control" id="gender" v-model="memberInfo.gender" :style="{
                    width: '300px',
                    backgroundColor: 'white',
                    color: '#000',
                    borderRadius: '0',
                    border: 'none'
                  }" disabled required />
                </div>
                <div class="col-md-12 mb-3 d-flex align-items-center">
                  <label for="birthday" class="mb-0 me-3" style="width: 100px;">생년월일</label>
                  <input type="date" class="form-control" id="birthday" v-model="memberInfo.birthday"
                    :disabled="!isEditing" :style="{
                      width: '300px',
                      backgroundColor: 'white',
                      color: '#000',
                      borderRadius: '0',
                      border: isEditing ? '1px solid #6C97FD' : 'none' // 수정 모드에서 테두리 표시
                    }" required />
                </div>
                <div class="col-md-12 mb-3 d-flex align-items-center" v-if="role === 'TUTOR'">
                  <label for="introduce" class="mb-0 me-3" style="width: 100px;">자기 소개</label>
                  <input type="text" class="form-control" id="introduce" v-model="memberInfo.introduce"
                    :disabled="!isEditing" :style="{
                      width: '300px',
                      backgroundColor: 'white',
                      color: '#000',
                      borderRadius: '0',
                      border: isEditing ? '1px solid #6C97FD' : 'none' // 수정 모드에서 테두리 표시
                    }" required />
                </div>
              </div>
              <br />
              <button v-if="!isEditing" type="button" class="btn"
                :style="{ backgroundColor: '#6C97FD', color: 'white' }" @click="isEditing = true">
                수정하기
              </button>
              <button type="submit" class="btn ml-3" v-if="isEditing"
                :style="{ backgroundColor: '#82D691', color: 'white' }">
                수정 완료
              </button>
            </form>
          </div>
        </div>
      </div>
    </div>
  </div>
  <AlertModal v-model="alertModal" @update:dialog="alertModal = $event" icon=mdi-alert-circle-outline
    :title=this.alertModalTitle :contents=this.alertModalContents />
</template>

<script>
/* global kakao */
import AlertModal from "@/components/AlertModal.vue";

import axios from "axios";
export default {
  components: {
    AlertModal
  },
  data() {
    return {
      memberInfo: {},
      role: localStorage.getItem("role"),
      isEditing: false, // 수정 모드 상태 추가
      alertModal: false,
      alertModalTitle: "",
      alertModalContents: "",
    };
  },
  computed: {
    cardMinHeight() {
      if (this.role === 'TUTOR') {
        return '566px';
      } else if (this.role === 'TUTEE') {
        return '470px';
      }
      return '470px';
    }
  },
  async created() {
    const response = await axios.get(
      `${process.env.VUE_APP_API_BASE_URL}/member-service/infoGet`
    );
    this.memberInfo = response.data.result;
  },
  mounted() {
    this.loadDaumPostcodeScript();
    this.loadKakaoMapScript();
  },
  methods: {
    async updateProfile() {
      try {
        const updatedInfo = {
          id: this.memberInfo.id,
          name: this.memberInfo.name, // 여기서 name이 null이 아닌지 확인
          birthday: this.memberInfo.birthday,
          gender: this.memberInfo.gender, // 이 필드를 추가해 주어야 함
          address: this.memberInfo.address,
          detailAddress: this.memberInfo.detailAddress,
          phoneNumber: this.memberInfo.phoneNumber,
          education: this.memberInfo.education,
          introduce: this.memberInfo.introduce,
        };

        await axios.post(
          `${process.env.VUE_APP_API_BASE_URL}/member-service/infoUpdate`,
          updatedInfo
        );
        this.isEditing = false; // 수정 완료 후 수정 모드 종료
        this.alertModalTtile = "수정 완료";
        this.alertModalContents = "회원 정보가 업데이트 되었습니다";
        this.alertModal = true;
      } catch (error) {
        console.error("회원 정보 업데이트 중 오류 발생:", error);
        this.alertModalTtile = "회원 정보 수정 실패";
        this.alertModalContents = "회원 정보 수정이 실패되었습니다";
        this.alertModal = true;
      }
    },
    selectImage() {
      this.$refs.fileInput.click();
    },
    onFileChange(event) {
      const file = event.target.files[0];
      if (file) {
        const formData = new FormData();
        formData.append("file", file);
        axios.post(`${process.env.VUE_APP_API_BASE_URL}/member-service/imageUpdate`, formData)
          .then(response => {
            this.memberInfo.profileImage = response.data.url;
            window.location.reload();
            this.alertModalTtile = "프로필 이미지 업로드";
            this.alertModalContents = "프로필 이미지가 업로드 되었습니다.";
            this.alertModal = true;
          })
          .catch(error => {
            console.error("이미지 업로드 중 오류 발생:", error);
            this.alertModalTtile = "프로필 이미지 업로드 실패";
            this.alertModalContents = "프로필 이미지가 업로드 실패되었습니다.";
            this.alertModal = true;
          });
      }
    },
    updateAddress() {
      if (window.daum && window.daum.Postcode) {
        // eslint-disable-next-line no-undef
        new daum.Postcode({
          oncomplete: (data) => {
            this.memberInfo.address = data?.roadAddress;

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
  },
};
</script>

<style scoped>
.container {
  max-width: 1200px;
}

.card {
  margin-bottom: 20px;
}

.rounded-circle {
  border-radius: 50%;
}

.mt-16 {
  margin-top: 16px;
}
</style>
