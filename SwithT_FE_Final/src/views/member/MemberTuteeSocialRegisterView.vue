<template>
  <div class="container mt-15">
    <h3 class="text-center mb-4">회원 정보 입력</h3>
    <hr />
    <form @submit.prevent="addInfo">
      <!-- 이름 -->
      <div class="form-group row mb-3">
        <label for="name" class="col-sm-2 col-form-label">이름</label>
        <div class="col-sm-10">
          <input
            type="text"
            id="name"
            class="form-control"
            placeholder="이름을 입력하세요."
            v-model="name"
            required
          />
        </div>
      </div>

      <!-- 생년월일 -->
      <div class="form-group row mb-3">
        <label for="birthDate" class="col-sm-2 col-form-label">생년월일</label>
        <div class="col-sm-10">
          <input
            type="date"
            id="birthDate"
            class="form-control"
            v-model="birthday"
            required
          />
        </div>
      </div>

      <!-- 성별 -->
      <div class="form-group row mb-3">
        <label class="col-sm-2 col-form-label">성별</label>
        <div class="col-sm-10 text-left">
          <div class="form-check form-check-inline">
            <input
              class="form-check-input"
              type="radio"
              name="gender"
              id="male"
              value="남성"
              v-model="displayGender"
              required
            />
            <label class="form-check-label" for="male">남성</label>
          </div>
          <div class="form-check form-check-inline">
            <input
              class="form-check-input"
              type="radio"
              name="gender"
              id="female"
              value="여성"
              v-model="displayGender"
            />
            <label class="form-check-label" for="female">여성</label>
          </div>
        </div>
      </div>

      <!-- 핸드폰 -->
      <div class="form-group row mb-3">
        <label for="phoneNumber" class="col-sm-2 col-form-label"
          >휴대전화</label
        >
        <div class="col-sm-10">
          <input
            type="tel"
            id="phoneNumber"
            class="form-control"
            placeholder="숫자만 입력하세요."
            @input="phoneNumber = phoneNumber.replace(/[^0-9]/g, '')"
            v-model="phoneNumber"
            required
          />
        </div>
      </div>

      <!-- 주소 -->
      <div class="form-group row mb-3">
        <label for="address" class="col-sm-2 col-form-label">주소</label>

        <div class="col-sm-10 d-flex align-items-center">
          <v-btn
            style="border: 1px solid #ccc"
            variant="outlined"
            @click="updateAddress()"
          >
            <v-icon>mdi-map-search</v-icon> 주소 검색
          </v-btn>

          <div class="flex-grow-1 text-left ml-3" v-if="address != ''">
            {{ this.address }}
          </div>

          <input
            type="text"
            id="detailAddress"
            class="form-control ml-2"
            placeholder="상세주소를 입력하세요."
            v-model="detailAddress"
            style="width: 300px"
            required
          />
        </div>
      </div>

      <!-- 제출 버튼 -->
      <div class="form-group row mb-3 text-center mt-4">
        <div class="col-sm-12">
          <button
            type="submit"
            class="btn"
            style="background-color: #6c97fd; color: white; border: none"
          >
            완료
          </button>
        </div>
      </div>
    </form>
  </div>
</template>
<script>
/* global kakao */
import axios from "axios";
import Cookies from "js-cookie";
import { jwtDecode } from "jwt-decode";
export default {
  data() {
    return {
      id: "",
      name: "",
      birthday: "",
      phoneNumber: "",
      address: "",
      detailAddress: "",
      gender: "",
      displayGender: "",
      role: "TUTEE",
      profileImage: "",
    };
  },
  mounted() {
    // 쿠키에서 id와 role 값을 가져와서 셋팅
    this.id = Cookies.get("memberId");
    this.role = Cookies.get("role");
    console.log("role 출력!!!!!!!!!!!!!!!!!!!!!!!!!!!");
    console.log(this.role);
    console.log("토큰 출력!!!!!!!!!!!!!!!!!!!!!")
    console.log(Cookies.get("accessToken"));
    console.log(Cookies.get("refreshToken"));

    this.loadDaumPostcodeScript();
    this.loadKakaoMapScript();
  },
  methods: {
    async addInfo() {
      try {
        // 성별에 따라 프로필 이미지 설정

        if (this.displayGender === "남성") {
          this.profileImage = process.env.VUE_APP_DEFAULT_MALE_IMAGE;
        } else if (this.displayGender === "여성") {
          this.profileImage = process.env.VUE_APP_DEFAULT_FEMALE_IMAGE;
        }

        this.gender = this.displayGender === "남성" ? "MAN" : "WOMAN";

        const updateDate = {
          id: this.id,
          name: this.name,
          birthday: this.birthday,
          phoneNumber: this.phoneNumber,
          gender: this.gender,
          address: this.address,
          etailAddress: this.detailAddress,
          profileImage: this.profileImage,
          role: this.role,
        };

        const response = await axios.post(
          `${process.env.VUE_APP_API_BASE_URL}/member-service/member/AddInfoUpdate`,
          updateDate
        );
        console.log("this.id : "+this.id);
        console.log("추가 정보 입력 성공");

        const token = response.data.result.token;
        const refreshToken = response.data.result.refreshToken;
        const role = jwtDecode(token).role;
        const id = response.data.result.id;

        localStorage.setItem("token", token);
        localStorage.setItem("refreshToken", refreshToken);
        localStorage.setItem("role", role);
        localStorage.setItem("id", id);

        this.$router.push("/member/welcome");
      } catch (e) {
        const error_message =
          e.response?.data?.error_message ||
          "추가정보 입력 중 오류가 발생했습니다.";
        console.error(error_message);
      }
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
                console.log("위도 : " + result[0].y);
                console.log("경도 : " + result[0].x);

                // 지도에 마커를 추가하는 로직
                this.initMap(result[0].y, result[0].x);
              }
            });
          },
        }).open();
      } else {
        console.error("Daum Postcode 스크립트가 로드되지 않았습니다.");
      }
    },
    loadDaumPostcodeScript() {
      const script = document.createElement("script");
      script.src =
        "//t1.daumcdn.net/mapjsapi/bundle/postcode/prod/postcode.v2.js";
      script.onload = () => {
        this.isDaumScriptLoaded = true;
      };
      document.head.appendChild(script);
    },
    loadKakaoMapScript() {
      const script = document.createElement("script");
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
<style></style>
