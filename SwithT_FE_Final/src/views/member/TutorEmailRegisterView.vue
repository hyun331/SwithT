<template>
  <div class="container mt-15">
    <h3 class="text-center mb-4">TUTOR 회원 가입</h3>
    <hr />
    <form @submit.prevent="handleSubmit">
      <div class="form-group row mb-3">
        <label for="email" class="col-sm-2 col-form-label">이메일</label>
        <div class="col-sm-10">
          <input type="email" id="email" class="form-control" placeholder="이메일을 입력하세요." v-model="email" required />
        </div>
      </div>

      <div class="form-group row mb-3">
        <label for="verificationCode" class="col-sm-2 col-form-label">인증 코드</label>
        <div class="col-sm-10 d-flex align-items-center">
          <input type="text" id="verificationCode" class="form-control me-2" placeholder="인증 코드를 입력하세요." v-model="otp"
            required />
          <button v-if="!codeSent" @click="requestVerificationCode" class="btn" type="button"
            style="background-color: #6C97FD; color: white; border: none; width: 100px;">
            코드 전송
          </button>
          <button v-else-if="!verificationSuccess" @click="verifyCode" class="btn" type="button"
            style="background-color: #82D691; color: white; border: none; width: 100px;">
            코드 인증
          </button>
          <button v-else class="btn btn-secondary" disabled style="width: 120px;">
            인증 완료
          </button>
        </div>
      </div>


      <div class="form-group row mb-3">
        <label for="password" class="col-sm-2 col-form-label">비밀번호</label>
        <div class="col-sm-10">
          <input type="password" id="password" class="form-control" placeholder="비밀번호를 입력하세요." v-model="password"
            required />
        </div>
      </div>

      <div class="form-group row mb-3">
        <label for="passwordConfirm" class="col-sm-2 col-form-label">비밀번호 확인</label>
        <div class="col-sm-10">
          <input type="password" id="passwordConfirm" class="form-control" placeholder="비밀번호를 다시 입력하세요."
            v-model="passwordConfirm" required />
        </div>
      </div>

      <div class="form-group row mb-3">
        <label for="name" class="col-sm-2 col-form-label">이름</label>
        <div class="col-sm-10">
          <input type="text" id="name" class="form-control" placeholder="이름을 입력하세요." v-model="name" required />
        </div>
      </div>

      <div class="form-group row mb-3">
        <label class="col-sm-2 col-form-label">성별</label>
        <div class="col-sm-10 text-left">
          <div class="form-check form-check-inline">
            <input class="form-check-input" type="radio" name="gender" id="male" value="남성" v-model="displayGender"
              required />
            <label class="form-check-label" for="male">남성</label>
          </div>
          <div class="form-check form-check-inline">
            <input class="form-check-input" type="radio" name="gender" id="female" value="여성" v-model="displayGender" />
            <label class="form-check-label" for="female">여성</label>
          </div>
        </div>
      </div>

      <div class="form-group row mb-3">
        <label for="birthDate" class="col-sm-2 col-form-label">생년월일</label>
        <div class="col-sm-10">
          <input type="date" id="birthDate" class="form-control" v-model="birthday" required />
        </div>
      </div>

      <div class="form-group row mb-3">
        <label for="address" class="col-sm-2 col-form-label">주소</label>

        <div class="col-sm-10 d-flex align-items-center">
          <v-btn style="border: 1px solid #ccc;" variant="outlined" @click="updateAddress()">
            <v-icon>mdi-map-search</v-icon> 주소 검색
          </v-btn>

          <div class="flex-grow-1 text-left ml-3" v-if="address != ''">
            {{ this.address }}
          </div>

          <input type="text" id="detailAddress" class="form-control ml-2" placeholder="상세주소를 입력하세요."
            v-model="detailAddress" style="width: 300px;" required />
        </div>
      </div>

      <div class="form-group row mb-3">
        <label for="phoneNumber" class="col-sm-2 col-form-label">휴대전화</label>
        <div class="col-sm-10">
          <input type="tel" id="phoneNumber" class="form-control" placeholder="숫자만 입력하세요." v-model="phoneNumber"
            @input="phoneNumber = phoneNumber.replace(/[^0-9]/g, '')" required />
        </div>
      </div>

      <div class="form-group row mb-3">
        <label for="introduce" class="col-sm-2 col-form-label">자기소개</label>
        <div class="col-sm-10">
          <textarea id="introduce" class="form-control" placeholder="자기소개를 입력하세요." v-model="introduce"
            required></textarea>
        </div>
      </div>

      <div class="form-group row mb-3">
        <label class="col-sm-2 col-form-label">학력</label>
        <div class="col-sm-10 text-left">
          <div class="form-check form-check-inline">
            <input class="form-check-input" type="radio" name="education" id="bachelor" value="학사" v-model="education"
              required />
            <label class="form-check-label" for="bachelor">학사</label>
          </div>
          <div class="form-check form-check-inline">
            <input class="form-check-input" type="radio" name="education" id="master" value="석사" v-model="education" />
            <label class="form-check-label" for="master">석사</label>
          </div>
          <div class="form-check form-check-inline">
            <input class="form-check-input" type="radio" name="education" id="phd" value="박사" v-model="education" />
            <label class="form-check-label" for="phd">박사</label>
          </div>
        </div>
      </div>

      <div class="form-group row mb-3">
        <label for="profileImage" class="col-sm-2 col-form-label">프로필 이미지</label>
        <div class="col-sm-10">
          <input type="file" id="profileImage" class="form-control file-input" @change="fileUpdate" accept="image/*" />
        </div>
      </div>


      <div class="form-group row mb-3 text-center mt-4">
        <div class="col-sm-12">
          <button type="submit" class="btn"
            style="background-color: #6C97FD; color: white; border: none; width: 200px;">가입 하기</button>
        </div>
      </div>
    </form>
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
      // 회원가입 form data
      name: "",
      email: "",
      password: "",
      birthday: "",
      phoneNumber: "",
      introduce: "",
      address: "",
      detailAddress: "",
      education: "",
      gender: "",
      displayGender: "",
      role: "TUTOR", // 튜터 회원가입 페이지이므로 TUTOR로 명시할 것.
      otp: "",
      profileImage: null, // 프로필 이미지 초기화

      // 인증 코드 관련 상태
      codeSent: false,
      verificationSuccess: false,

      alertModal: false,
      alertModalTitle: "",
      alertModalContents: "",
    };
  },
  mounted() {
    this.loadDaumPostcodeScript();
    this.loadKakaoMapScript();
  },
  methods: {
    async handleSubmit() {
      // 회원 가입 폼 제출 처리
      try {
        // 성별 값 설정
        this.gender = this.displayGender === "남성" ? "MAN" : "WOMAN";

        let registerData = new FormData();
        const data = {
          name: this.name,
          email: this.email,
          password: this.password,
          birthday: this.birthday,
          phoneNumber: this.phoneNumber,
          introduce: this.introduce,
          education: this.education,
          gender: this.gender,
          address: this.address,
          detailAddress: this.detailAddress,
          role: this.role,
        };

        registerData.append(
          "data",
          new Blob([JSON.stringify(data)], { type: "application/json" })
        );
        registerData.append("file", this.profileImage); // 이미지 파일 추가

        await axios.post(
          `${process.env.VUE_APP_API_BASE_URL}/member-service/member/create`,
          registerData
        );
        this.alertModalTtile = "회원 가입 성공";
        this.alertModalContents = "회원 가입 성공했습니다.";
        this.alertModal = true;
        this.$router.push("/member/email/login");
      } catch (e) {
        const error_message =
          e.response?.data?.error_message || "회원가입 중 오류가 발생했습니다.";
        console.error(error_message);
        this.alertModalTtile = "회원 가입 실패";
        this.alertModalContents = error_message;
        this.alertModal = true;
      }
    },
    async requestVerificationCode() {
      try {

        this.codeSent = true;

        const response = await axios.post(
          `${process.env.VUE_APP_API_BASE_URL}/member-service/email/requestCode?email=${this.email}`
        );
        this.alertModalTtile = "이메일 인증 코드 전송";
        this.alertModalContents = response.data.status_message;
        this.alertModal = true;
      } catch (error) {
        console.error(error);
        this.codeSent = false;
        this.alertModalTtile = "이메일 인증 코드 전송 실패";
        this.alertModalContents = "이메일 인증 코드 전송에 실패했습니다.";
        this.alertModal = true;
      }
    },
    async verifyCode() {
      try {
        const response = await axios.get(
          `${process.env.VUE_APP_API_BASE_URL}/member-service/email/requestCode?email=${this.email}&code=${this.otp}`
        );
        this.alertModalTtile = "이메일 인증 성공";
        this.alertModalContents = response.data.status_message;
        this.alertModal = true;
        this.verificationSuccess = true;
      } catch (error) {
        console.error(error);
        this.verificationSuccess = false;
        this.alertModalTtile = "이메일 인증 실패";
        this.alertModalContents = "인증 코드가 올바르지 않습니다.";
        this.alertModal = true;
      }
    },
    fileUpdate(event) {
      this.profileImage = event.target.files[0]; // 파일 선택 시 profileImage에 저장
    },
    updateAddress() {
      if (window.daum && window.daum.Postcode) {
        // eslint-disable-next-line no-undef
        new daum.Postcode({
          oncomplete: (data) => {
            this.address = data?.roadAddress;

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
.form-group {
  width: 75%;
  margin: 0 auto;
}

.file-input {
  width: 100%;
  padding: 0.375rem 0.75rem;
  font-size: 1rem;
  line-height: 1.5;
  border-radius: 0.25rem;
}
</style>
