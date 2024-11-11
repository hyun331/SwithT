<template>
  <v-dialog v-model="localDialog" max-width="500px" max-height="500px">
    <v-card style="border-radius: 20px">
      <div class="d-flex justify-center align-center mt-10">
        <h4><strong>힘이 되는 수강평을 남겨주세요!</strong></h4>
      </div>

      <v-card-text>
        <!-- 별점 선택 -->
        <div class="d-flex justify-center my-2 mb-3">
          <div class="rating-container">
            <v-icon v-for="star in 5" :key="star" :class="{ 'active-star': star <= rating }" @click="setRating(star)"
              large class="rating-star">
              mdi-star
            </v-icon>
          </div>
        </div>

        <!-- 제목과 내용 입력창은 별점을 선택하고 작성하기 버튼을 누르면 보이도록 v-if -->
        <v-form v-if="isFormVisible">
          <v-text-field v-model="reviewTitle" clearable variant="outlined" placeholder="제목" required
            class="mt-3"></v-text-field>

          <v-textarea v-model="reviewContent" clearable variant="outlined" placeholder="수강평을 작성해보세요!"
            required></v-textarea>
        </v-form>
        <v-card-actions class="d-flex justify-center">
          <!-- 별점 선택 전 -->
          <v-btn v-if="!isFormVisible" color="white" style="background-color: #6c97fd; width: 220px; height: 40px"
            @click="confirmClose">
            <b>취소</b>
          </v-btn>

          <!-- 별점 선택 후, 흰색 배경 취소 버튼 (검은색 테두리) -->
          <v-btn v-else color="black" style="
              background-color: white;
              width: 220px;
              height: 40px;
              border: 1px solid black;
            " @click="confirmClose">
            <b>취소</b>
          </v-btn>

          <!-- 작성하기 버튼 -->
          <v-btn style="background-color: #6c97fd; width: 220px; height: 40px" color="white" text @click="showForm"
            v-if="!isFormVisible">
            <b>작성하기</b>
          </v-btn>

          <!-- 작성 버튼 -->
          <v-btn style="background-color: #6c97fd; width: 220px; height: 40px" color="white" text v-if="isFormVisible"
            @click="submitReview" :loading="loading">
            <b>작성하기</b>
          </v-btn>
        </v-card-actions>
      </v-card-text>
    </v-card>
  </v-dialog>
</template>

<script>
import axios from "axios";

export default {
  props: {
    dialog: {
      type: Boolean,
      required: true
    },
    applyId: {
      type: Number,
      required: true
    },
    tutorId: {
      type: Number,
      required: true
    }
  },
  watch: {
    dialog(newVal) {
      this.localDialog = newVal;
    }
  },
  data() {
    return {
      // dialog: false,
      reviewTitle: "", // 제목 데이터
      reviewContent: "", // 내용 데이터
      rating: 0, // 선택된 별점
      isFormVisible: false, // 제목과 내용 입력창 표시 여부
      image: null,
      error: false,
      errorMessage: "",
      loading: false,
      localDialog: this.dialog,
    };
  },
  methods: {
    setRating(star) {
      this.rating = star; // 선택한 별점 설정
    },
    showForm() {
      // 별점이 선택되었는지 확인 후 제목과 내용 입력창을 표시
      if (this.rating > 0) {
        this.isFormVisible = true;
      } else {
        alert("별점을 먼저 선택해주세요.");
      }
    },
    confirmClose() {
      if (this.reviewTitle || this.reviewContent || this.rating) {
        if (confirm("작성 중인 내용이 사라집니다. 닫으시겠습니까?")) {
          this.close();
        }
      } else {
        this.close();
      }
    },
    close() {
      this.localDialog = false;
      this.$emit('update:dialog', false);
      this.clearForm();
    },

    clearForm() {
      this.reviewTitle = "";
      this.reviewContent = "";
      this.rating = 0; // 별점 초기화
      this.isFormVisible = false; // 입력창 숨기기
      this.image = null;
      this.showErrors = false; // 폼이 초기화되면 에러 메시지와 테두리 색상도 초기화
    },
    async submitReview() {
      if (!this.reviewTitle || !this.reviewContent || this.rating === 0) {
        alert("제목, 내용, 그리고 별점을 모두 입력해주세요.");
        return;
      }

      this.loading = true;
      //   ⭐⭐⭐⭐⭐⭐⭐⭐⭐⭐⭐리뷰 작성 테스트 화면으로 완료, 추후 화면 붙일때 tutorId랑 applyId를 동적으로 처리해야함, 
      //   그리고 ReviewStatus가 Y이면 더 이상 버튼이 활성화 되지않게 처리하는 것 추가해야한다⭐⭐⭐⭐⭐⭐⭐⭐⭐⭐⭐⭐⭐
      try {
        // 리뷰 작성 데이터 준비
        const reviewData = {
          tutorId: this.tutorId, // 실제 튜터 ID를 동적으로 설정해야 함 !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
          star: this.rating,
          applyId: this.applyId, // 실제 applyId 동적으로 를 설정해야 함
          title: this.reviewTitle,
          contents: this.reviewContent,
        };
        // 리뷰 작성 요청 보내기
        const reviewResponse = await axios.post(
          `${process.env.VUE_APP_API_BASE_URL}/member-service/review/create`,
          reviewData
        );
        if (reviewResponse.status === 201) {
          // 리뷰 작성 성공 후, 리뷰 상태 업데이트 요청
          const applyId = reviewData.applyId; // applyId 가져오기
          const statusResponse = await axios.put(
            `${process.env.VUE_APP_API_BASE_URL}/lecture-service/lecture-apply/review/status`,
            null, // PUT 요청에서 body가 없으므로 null로 설정
            { params: { applyId } } // applyId를 쿼리 파라미터로 전달
          );
          console.log("리뷰 status 변경",statusResponse)
          if (statusResponse.status === 200) {
            console.log("리뷰가 성공적으로 등록되었으며, 상태가 업데이트되었습니다.");
            this.close(); // 폼 초기화 및 다이얼로그 닫기

          }
        }
      } catch (error) {
        console.error("리뷰 등록 또는 상태 업데이트 중 오류 발생:", error);
        alert(
          "리뷰 등록 또는 상태 업데이트에 실패했습니다. 다시 시도해주세요."
        );
      } finally {
        this.loading = false;
      }
    },
  },
};
</script>

<style scoped>
.rating-container {
  display: flex;
  align-items: center;
}

.rating-star {
  font-size: 40px;
  /* 별 크기 조정 */
  color: lightgray;
  /* 기본 별 색상 */
  cursor: pointer;
}

.active-star {
  color: #ffeb3b;
  /* 선택된 별 색상 */
}
</style>
