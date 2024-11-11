<template>
    <v-row justify="center" class="mb-1">
      <v-col cols="auto" class="text-center mt-6">
        <h4 class="mt-16"><strong>사용자 총 평점</strong></h4>
        <v-row class="ml-2 mt-5">
          <v-col cols="1" v-for="n in 5" :key="n" class="pa-0 ml-4">
            <v-icon :color="n <= averageRating ? 'red' : 'grey'" size="30">
              {{ n <= averageRating ? "mdi-star" : "mdi-star-outline" }}
            </v-icon>
          </v-col>
        </v-row>
        <h3 class="mt-5">
          <strong>{{ averageRating }} / 5.0</strong>
        </h3>
      </v-col>

      <v-col cols="3" class="text-center mt-16  ">
        <h4 class="mt-5"><strong>전체 리뷰 수</strong></h4>
        <v-icon
          color="blue-grey-darken-3s"
          icon="mdi-message-text"
          style="font-size: 60px"
          class="mt-5"
        ></v-icon>
        <p class="text-h4">
          <strong>{{ totalReviews }}</strong>
        </p>
      </v-col>

      <v-col cols="4">
        <h4 class="text-center mb-3 mt-10"><strong>평점 비율</strong></h4>
        <v-row v-for="n in 5" :key="n" align="center" class="mb-1">
          <v-col cols="2">{{ n }}점</v-col>
          <v-col cols="8">
            <v-progress-linear
              :value="(starDistribution[n] / totalReviews) * 100"
              height="15"
              :style="getBarGradient(starDistribution[n] / totalReviews)"
            ></v-progress-linear>
          </v-col>
          <v-col cols="2" class="text-right">{{ starDistribution[n] }}개</v-col>
        </v-row>
      </v-col>
    </v-row>

    <v-divider></v-divider>

    <v-row justify="start" class="ml-8">
      <!-- 정렬 버튼 -->
      <v-btn-toggle v-model="selectedSort" class="mb-4">
        <v-btn @click="setSort('createdTime', 'DESC')" style="font-size: 15px"
          ><strong>최신순</strong></v-btn
        >
        <v-btn @click="setSort('createdTime', 'ASC')" style="font-size: 15px"
          ><strong>오래된순</strong></v-btn
        >
        <v-btn @click="setSort('star', 'DESC')" style="font-size: 15px"
          ><strong>평점 높은순</strong></v-btn
        >
        <v-btn @click="setSort('star', 'ASC')" style="font-size: 15px"
          ><strong>평점 낮은순</strong></v-btn
        >
      </v-btn-toggle>
    </v-row>

    <v-row class="mt-15 ml-10" justify="center">
      <v-row justify="center">
        <v-col
          cols="12"
          v-for="review in reviewList"
          :key="review.id"
          class="review-item"
          justify="center"
        >
          <v-row align="center">
            <v-col cols="2" class="d-flex flex-column align-center justify-center">
              <v-row class="d-flex justify-center">
                <v-avatar size="100" class="mb-5">
                  <v-img
                    :src="review.profileImage || 'https://via.placeholder.com/150'"
                    alt="Profile"
                  ></v-img>
                </v-avatar>
              </v-row>
              <v-row class="mb-3 d-flex justify-center">
                {{ review.name }}
              </v-row>
            </v-col>

            <v-col class="review-detail ml-0 mb-10" cols="8">
              <v-row no-gutters style="padding: 0">
                <v-col cols="auto" v-for="n in 5" :key="n">
                  <v-icon color="red" v-if="n <= review.star">mdi-star</v-icon>
                  <v-icon color="grey" v-else>mdi-star-outline</v-icon>
                </v-col>

                <span style="padding-left: 10px">
                  <strong>{{ review.star }}</strong>
                </span>

                <span
                  class="ml-5"
                  style="
                    font-weight: 500;
                    margin-left: 20px;
                    margin-bottom: 10px;
                  "
                >
                  {{ formatDate(review.createdTime) }}
                </span>
              </v-row>

              <v-row
                justify="start"
                class="text-sm-left"
                style="padding: 0 10px"
              >
                <span
                  class="ml-2"
                  style="
                    color: #000000;
                    font-size: 18px;
                    padding-right: 10px;
                    font-size: x-large;
                  "
                >
                  {{ review.title }}
                </span>
              </v-row>

              <br />

              <v-row
                class="ml-2 mt-1"
                justify="start"
                align="start"
                style="font-weight: 500; text-align: left"
              >
                {{ review.contents }}
              </v-row>
            </v-col>
            <v-col cols="12" class="text-right">
              <v-row justify="end" class="edit-delete-buttons">
               <!-- 로컬스토리지 데이터는 문자열로 가져오기 때문에 toString으로 비교해야한다.  -->
                <v-btn
                  v-if="review.writerId.toString() === currentUser.id" 
                  color="#82D691"
                  @click="openEditModal(review)"
                  class="edit-btn mb-6 mr-10"
                >
                  수정하기
                </v-btn>
              </v-row>
            </v-col>
          </v-row>
          <v-divider :thickness="3"></v-divider>
        </v-col>
        <v-pagination
          v-model="frontendPage"
          :length="totalPages"
          @click="handlePageChange"
        ></v-pagination>
      </v-row>
    </v-row>

    <!-- 수정 모달 -->
    <v-dialog v-model="editDialog" max-width="800px">
      <v-card class="bg-light text-dark" style="padding-top: 10px">
        <v-card-title
          class="d-flex justify-end"
          style="padding-top: 10px; margin-right: 10px"
        >
          <v-btn icon @click="closeEditModal" class="modal-close-btn">
            <v-icon>mdi-close</v-icon>
          </v-btn>
        </v-card-title>

        <v-card-text>
          <v-form @submit.prevent="updateReview">
            <!-- 평점 -->
            <v-row class="mb-3 ml-3" style="font-size: 25px" ><strong>평점</strong></v-row>
            
            <v-rating
            
              v-model="editedReview.rating"
              background-color="grey darken-3"
              color="pink"
              large
              dense
              :length="5"
              class="mb-3 "
            >
            </v-rating>

            <!-- 제목 -->
            <v-text-field
              label="제목"
              v-model="editedReview.title"
              outlined
              required
              class="mb-3"
            ></v-text-field>

            <!-- 내용 -->
            <v-textarea
              label="내용"
              v-model="editedReview.contents"
              outlined
              required
              class="mb-3"
            ></v-textarea>

            <v-row justify="center" class="mt-3">
              <v-btn type="submit" color="#6C97FD" justify="center" class="mb-6"
                >수정하기</v-btn
              >
            </v-row>
          </v-form>
        </v-card-text>
      </v-card>
    </v-dialog>
</template>

<script>
import axios from "axios";

export default {
  props: {
    tutorId: {
      type: Number,
      required: false, // 이 prop은 선택적임
    },
  },
  data() {
    return {
      currentUser: {
        id: localStorage.getItem("id"), // localStorage에서 사용자 ID 가져오기
      },

      reviewList: [],
      reviewId: null,
      editDialog: false,
      editedReview: {
        title: "",
        contents: "",
        rating: 0, // 평점 필드 추가
      },

      // 페이징 처리
      page: 0,
      size: 5,
      totalPages: 0,
      frontendPage: 1,

      selectedSort: "createdTime_DESC", // 기본값: 최신순
      sortBy: "createdTime", // 정렬 기준 필드 (createdTime or star)
      order: "DESC", // 정렬 순서 (ASC or DESC)

      averageRating: 0, // 평균 평점
      totalReviews: 0, // 총 리뷰 수
      starDistribution: { 5: 0, 4: 0, 3: 0, 2: 0, 1: 0 }, // 별점 분포
    };
  },
  methods: {
    getBarGradient(ratio) {
      const percentage = ratio * 100;
      return `background: linear-gradient(90deg, #6C97FD ${percentage}%, white ${percentage}%);`;
    },

    openEditModal(review) {
      this.reviewId = review.id; // 리뷰 ID 저장
      this.editedReview.title = review.title; // 기존 제목 불러오기
      this.editedReview.contents = review.contents; // 기존 내용 불러오기
      this.editedReview.rating = review.star; // 기존 평점 불러오기
      this.editDialog = true; // 모달 열기
    },
    setSort(sortBy, order) {
      this.sortBy = sortBy;
      this.order = order;
      this.fetchReviews(); // 정렬 기준에 맞춰 다시 리뷰 목록 조회
    },
    closeEditModal() {
      this.editDialog = false; // 모달 닫기
    },
    async fetchReviews() {
      console.log("현재 tutorId->" + this.tutorId);
      try {
        const response = await axios.get(
          `${process.env.VUE_APP_API_BASE_URL}/member-service/review/list`,
          {
            params: {
              tutorId: this.tutorId, // 예시로 tutorId를 1로 설정 추후에 동적으로 처리하도록 수정해야함.
              sortBy: this.sortBy, // 선택된 정렬 기준
              order: this.order, // 선택된 정렬 순서
              size: this.size,
              page: this.page,
            },
          }
        );

        this.totalPages = response.data.result.totalPages;
        this.reviewList = response.data.result.content;
      } catch (error) {
        console.error("리뷰를 불러오는 중 에러 발생:", error);
      }
    },
    handlePageChange() {
      this.page = this.frontendPage - 1;
      this.fetchReviews();
    },
    formatDate(date) {
      const options = { year: "numeric", month: "2-digit", day: "2-digit" };
      return new Date(date).toLocaleDateString(undefined, options);
    },
    async updateReview() {
      try {
        const response = await axios.post(
          `${process.env.VUE_APP_API_BASE_URL}/member-service/review/update/${this.reviewId}`,
          this.editedReview
        );

        console.log(response.data); // 응답 데이터 확인
        this.closeEditModal(); // 모달 닫기
        window.location.reload();
      } catch (error) {
        console.error("리뷰 업데이트 중 에러 발생:", error);
        // 에러 처리 로직 추가
      }
    },
    async fetchAllReviews() {
      try {
        // const response = await axios.get(`/member-service/review/all`);

        const response = await axios.get(
          `${process.env.VUE_APP_API_BASE_URL}/member-service/review/all`,
          {
            params: {
              tutorId: this.tutorId, // 예시로 tutorId를 1로 설정 추후에 동적으로 처리하도록 수정해야함.
            },
          }
        );

        console.log("전체 리뷰!!!!!!!!!!!!!!!");

        const reviews = response.data.result; // 전체 리뷰 데이터
        console.log(reviews + "전체리뷰!!!!!!!!!!!!!!!!!!!!!!!");
        this.totalReviews = reviews.length; // 전체 리뷰 수

        let totalStars = 0; // 총 평점
        const starCount = { 5: 0, 4: 0, 3: 0, 2: 0, 1: 0 }; // 별점 카운트 초기화

        reviews.forEach((review) => {
          totalStars += review.star; // 평점 합산
          starCount[review.star] += 1; // 별점 카운트
        });

        this.averageRating = (totalStars / this.totalReviews).toFixed(1) || 0; // 평균 평점 계산
        this.starDistribution = starCount; // 별점 분포 저장
      } catch (error) {
        console.error("Error fetching all reviews:", error);
      }
    },
  },
  created() {
    this.fetchReviews(); // 컴포넌트가 생성될 때 리뷰 데이터를 서버에서 가져옴
    this.fetchAllReviews();
  },
};
</script>

<style scoped>
.review-item {
  margin-bottom: 20px;
}
.review-image {
  width: 100px;
  height: 100px;
}
.edit-btn {
  margin-right: 10px;
}
</style>
