<template>
  <!-- 자동 변경 배너 -->
  <v-carousel style="margin-top: 60px" cycle interval="4000" hide-delimiters :show-arrows="false" height="400px">
    <v-carousel-item v-for="(image, index) in bannerImages" :key="index">
      <img :src="image" class="full-width-banner" />
    </v-carousel-item>
  </v-carousel>

  <v-container style="margin-top: 10px">
    <div class="search-container" ref="searchBar" style="margin-top: 60px">
      <input v-model="searchValue" dense hide-details="true" class="search-bar" placeholder="SwithT와 함께 배우고, 나누고, 성장하세요"
        @input="fetchSuggestions" @keyup.enter="performSearch" />
      <span @click="performSearch" class="search-btn mdi mdi-magnify"></span>
      <!-- 추천 검색어 표시 -->
      <ul v-if="suggestions.length > 0" class="suggestions-list" :style="{ top: suggestionsTop + 'px' }">
        <li v-for="(suggestion, index) in suggestions" :key="index" @click="selectSuggestion(suggestion)">
          {{ suggestion }}
        </li>
      </ul>
    </div>

    <section class="menu mt-5">
      <div class="menu-list" @click="performCategorySearch('CAREER')">
        <img src="@/assets/manager_2704454.png" class="menu-icon" />
        <div class="menu-title">취업/직무</div>
      </div>
      <div class="menu-list" @click="performCategorySearch('DEVELOPMENT')">
        <img src="@/assets/target_2656366.png" class="menu-icon" />
        <div class="menu-title">자기계발</div>
      </div>
      <div class="menu-list" @click="performCategorySearch('ADMISSION')">
        <img src="@/assets/mortarboard_2655764.png" class="menu-icon" />
        <div class="menu-title">입시</div>
      </div>
      <div class="menu-list" @click="performCategorySearch('HOBBY')">
        <img src="@/assets/painting_2655642.png" class="menu-icon" />
        <div class="menu-title">취미</div>
      </div>
      <div class="menu-list" @click="performTypeSearch('LECTURE')">
        <img src="@/assets/meeting-room_2645420.png" class="menu-icon" />
        <div class="menu-title">강의</div>
      </div>
      <div class="menu-list" @click="performTypeSearch('LESSON')">
        <img src="@/assets/conversation_2821731.png" class="menu-icon" />
        <div class="menu-title">과외</div>
      </div>
    </section>

    <hr />
    <div class="title text-start ml-16">✨ 재능 기부</div>

    <section class="recommendation mr-15">
      <!-- freeLectures가 로드되기 전에는 로딩 메시지를 표시 -->
      <div v-if="freeLectures.length === 0" class="loading-message">
        데이터를 불러오는 중입니다...
      </div>

      <!-- freeLectures가 로드된 후에만 v-carousel을 렌더링 -->
      <v-carousel v-else :show-arrows="true" hide-delimiters height="450px" style="padding: 0 55px;">
        <v-carousel-item v-for="index in Math.ceil(freeLectures.length / 3)" :key="index">
          <div style="display: flex; justify-content: center; gap: 0px;">
            <!-- 세 개씩 보여주기 위해 배열을 slice -->
            <v-card v-for="lecture in freeLectures.slice((index - 1) * 3, index * 3)" :key="lecture.id"
              class="component" @click="goToLecture(lecture.id)"
              style="flex: 1; max-width: 33%; border-radius: 5px; box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);"
              elevation="1">
              <v-img :src="getlectureImage(lecture)" alt="강의 썸네일" height="300px" class="white--text align-end"
                style="border-radius: 0px;" cover></v-img>

              <v-card-text>
                <span v-if="lecture.lectureType === 'LECTURE'" class="tag lecture-tag">강의</span>
                <span v-if="lecture.lectureType === 'LESSON'" class="tag lesson-tag">과외</span>
                <span class="lecture-category">{{ getCategoryText(lecture.category) }}</span>
                <div class="lecture-title">{{ lecture.title }}</div>
                <div class="lecture-tutor">{{ lecture.memberName }} 튜터</div>
              </v-card-text>
            </v-card>
          </div>
        </v-carousel-item>
      </v-carousel>
    </section>


    <!-- <div class="ml-3">
      <v-card
      class="grow-card mb-8 ml-16 mr-5"
      style="margin: 0 auto; margin-top: 10px; border-radius: 10px; background-color: white; max-width: 1585px; border-color: #E0E0E0; box-shadow: 0 4px 6px rgba(0, 0, 0, 0.09);"
    >
      <v-row no-gutters justify="center" align="center">
        <v-col cols="12" md="8">
          <v-card-text>
            <h2 style="font-size: 30px; font-weight: bold; color: #333;">
              🎓 스위티에서 학습하고 성장하세요! 다양한 수업을 통해 나만의 목표를 달성하세요!
            </h2>
          </v-card-text>
        </v-col>
        <v-col cols="12" md="4" style="display: flex; justify-content: center; align-items: center;">
          <v-img
            src="@/assets/learning_emoji.png"
            alt="학습 이모지"
            height="150px"
            contain
            style="padding: 10px;"
          ></v-img>
        </v-col>
      </v-row>
    </v-card>
    </div>
      새로운 v-card 추가 -->

    <div class="title text-start ml-16" style="margin-top: 40px">🩷 많이 검색된 강의</div>

    <div class="lecture-list mr-15" style="display: flex; flex-wrap: wrap; gap: 10px; justify-content: flex-center;">
      <div class="component" v-for="lecture in popularLectures" :key="lecture.id" @click="goToLecture(lecture.id)"
        style="width: 375px; border-radius: 10px; box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1); margin: 8px;">
        <img :src="getlectureImage(lecture)" alt="강의 썸네일" class="lecture-image"
          style="border-radius: 15px 15px 0 0; width: 100%; height: 220px; object-fit: cover;" />
        <div class="lecture-info">
          <span v-if="lecture.lectureType === 'LECTURE'" class="tag lecture-tag">강의</span>
          <span v-if="lecture.lectureType === 'LESSON'" class="tag lesson-tag">과외</span>
          <span class="lecture-category">{{ getCategoryText(lecture.category) }}</span>
          <div class="lecture-title" style="font-size: 20px; font-weight: bold;">
            {{ lecture.title }}
          </div>
          <div class="lecture-tutor" style="font-size: 16px;">
            {{ lecture.memberName }} 튜터
          </div>
        </div>
      </div>
    </div>

    <div class="title text-start ml-16" style="margin-top: 40px">👀 요즘 뜨는 강의</div>

    <div class="lecture-list mr-15" style="display: flex; flex-wrap: wrap; gap: 10px; justify-content: flex-center;">
      <div class="component" v-for="lecture in latestLectures" :key="lecture.id" @click="goToLecture(lecture.id)"
        style="width: 375px; border-radius: 10px; box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1); margin: 8px;">
        <img :src="getlectureImage(lecture)" alt="강의 썸네일" class="lecture-image"
          style="border-radius: 15px 15px 0 0; width: 100%; height: 220px; object-fit: cover;" />
        <div class="lecture-info">
          <span v-if="lecture.lectureType === 'LECTURE'" class="tag lecture-tag">강의</span>
          <span v-if="lecture.lectureType === 'LESSON'" class="tag lesson-tag">과외</span>
          <span class="lecture-category">{{ getCategoryText(lecture.category) }}</span>
          <div class="lecture-title" style="font-size: 20px; font-weight: bold;">
            {{ lecture.title }}
          </div>
          <div class="lecture-tutor" style="font-size: 16px;">
            {{ lecture.memberName }} 튜터
          </div>
        </div>
      </div>
    </div>


  </v-container>
</template>

<script>
import axios from "axios";

export default {
  data() {
    return {
      bannerImages: [
        require("@/assets/banner1.png"),
        require("@/assets/테스트3.png"),
        // require("@/assets/테스트4.png"),
        // require("@/assets/테스트7.png"),
        // require("@/assets/테스트6.png"),
      ],
      searchValue: "",
      suggestions: [],
      latestLectures: [],
      popularLectures: [],
      freeLectures: [],
      suggestionsTop: 0,
    };
  },
  methods: {
    async fetchSuggestions() {
      if (this.searchValue.length > 0) {
        try {
          const response = await axios.post(
            `${process.env.VUE_APP_API_BASE_URL}/lecture-service/lecture/recommend`,
            null,
            { params: { keyword: this.searchValue } }
          );
          this.suggestions = response.data;

          this.updateSuggestionsPosition();

        } catch (error) {
          console.error("추천 검색어 가져오기 실패:", error);
        }
      } else {
        this.suggestions = [];
      }
    },
    async fetchLatestLectures() {
      try {
        const response = await axios.get(
          `${process.env.VUE_APP_API_BASE_URL}/lecture-service/lectures/latest`
        );
        this.latestLectures = response.data.result;
        console.log(response.data.result);
      } catch (error) {
        console.error("Failed to fetch latest lectures:", error);
      }
    },
    async fetchPopularLectures() {
      try {
        const response = await axios.get(
          `${process.env.VUE_APP_API_BASE_URL}/lecture-service/lectures/popular`
        );
        this.popularLectures = response.data.result;
        console.log(response.data.result);
      } catch (error) {
        console.error("Failed to fetch popular lectures:", error);
      }
    },
    async fetchFreeLectures() {
      try {
        const response = await axios.get(
          `${process.env.VUE_APP_API_BASE_URL}/lecture-service/lectures/free`
        );
        this.freeLectures = response.data.result;
        console.log(response.data.result);
      } catch (error) {
        console.error("Failed to fetch free lectures:", error);
      }
    },
    goToLecture(lectureId) {
      this.$router.push({ name: "LectureDetail", params: { id: lectureId } });
    },
    getlectureImage(lecture) {
      return lecture.image;
    },
    getCategoryText(category) {
      switch (category) {
        case "CAREER":
          return "취업/직무";
        case "HOBBY":
          return "취미";
        case "ADMISSION":
          return "입시";
        case "DEVELOPMENT":
          return "자기계발";
        default:
          return category;
      }
    },
    performSearch() {
      const requestData = {
        searchTitle: this.searchValue,
        category: "",
        status: "ADMIT",
        lectureType: "",
      };
      this.$router.push({
        name: "SearchResult",
        query: requestData,
      });
    },
    performCategorySearch(category) {
      const requestData = {
        searchTitle: "",
        category: category,
        status: "ADMIT",
        lectureType: "",
      };
      this.$router.push({
        name: "SearchResult",
        query: requestData,
      });
    },
    // 검색 결과 가져오기
    async fetchSearchResults() {
      this.searchResult = []; // 이전 검색 결과 초기화
      const requestData = this.$route.query;
      try {
        const response = await axios.post(
          `${process.env.VUE_APP_API_BASE_URL}/lecture-service/lecture/search`,
          requestData
        );
        this.searchResult = response.data.result.content;
        if (requestData.category) {
          this.selectedCategory = requestData.category;
        } else if (requestData.lectureType) {
          this.selectedCategory = requestData.lectureType;
        } else {
          this.selectedCategory = "";
        }
      } catch (error) {
        console.error("Failed to fetch search results:", error);
      }
    },
    performTypeSearch(type) {
        this.selectedCategory = type;  // 선택된 강의 유형 설정
        const requestData = {
            searchTitle: "",
            category: "",  
            status: "ADMIT",
            lectureType: type
        };
        this.searchValue = "";
        this.$router.push({ name: 'SearchResult', query: requestData }).then(() => {
            this.fetchSearchResults();  // 라우터 변경 후 검색 결과 갱신
        });
    },
    selectSuggestion(suggestion) {
      this.searchValue = suggestion;
      this.performSearch();
      this.suggestions = [];
    },
    updateSuggestionsPosition() {
      // search-bar의 위치를 계산하여 suggestionsTop에 반영
      const searchBar = this.$refs.searchBar;
      if (searchBar) {
        const rect = searchBar.getBoundingClientRect();
        // console.log(rect)
        this.suggestionsTop = rect.height;
        // console.log("Updated suggestionsTop:", this.suggestionsTop);
      }
    },
  },
  mounted() {
    this.updateSuggestionsPosition();
    window.addEventListener("resize", this.updateSuggestionsPosition);
  },
  updated() {
    this.updateSuggestionsPosition();
  },
  beforeUnmount() {
    window.removeEventListener("resize", this.updateSuggestionsPosition);
  },
  created() {
    this.fetchLatestLectures();
    this.fetchFreeLectures();
    this.fetchPopularLectures();
  },
};
</script>

<style scoped>
.v-container {
  color: #333;
  padding: none;
}

.banner {
  background-color: rgb(220, 219, 219);
}

.temporary-banner {
  height: 250px;
  width: auto;
  background-color: #999;
}

.menu {
  margin: 20px 0;
}

.menu-list {
  width: 80px;
  height: 100px;
  margin: 0 10px;
  display: inline-block;
}

.menu-list:hover {
  background-color: #eee;
  border-radius: 10px;
  cursor: pointer;
}

.menu-icon {
  font-size: 60px;
  width: 60px;
  height: 60px;
  margin: 5px;
}

.menu-title {
  font-weight: 700;
}

.recommendation {
  margin-top: 20px;
}

.title {
  font-size: 32px;
  font-weight: 700;
  text-align: left;
  margin: 0 0 15px 10px;
}

.lecture-list {
  display: flex;
  flex-wrap: wrap;
  justify-content: center;
  gap: 10px;
}

.component {
  cursor: pointer;
  margin: 20px;
  width: 250px;
  text-align: center;
  overflow: hidden;
  transition: transform 0.1s;
}

.component:hover {
  transform: scale(1.03);
}

.lecture-image {
  width: 250px;
  height: 200px;
  object-fit: cover;
}

.lecture-info {
  text-align: left;
  padding: 15px 20px 20px;
}

.tag {
  padding: 3px 8px;
  border-radius: 10px;
  color: white;
  font-size: 12px;
  font-weight: bold;
  margin-right: 5px;
}

.lecture-tag {
  background-color: #007bff;
}

.lesson-tag {
  background-color: #28a745;
}

.lecture-category {
  font-size: 15px;
}

.lecture-title {
  font-size: 18px;
  font-weight: 700;
  padding: 3px;
}

.lecture-tutor {
  padding: 0 3px 0;
}

.search-container {
  display: flex;
  justify-content: center;
  align-items: center;
  margin-top: 50px;
  position: relative;
}

.search-bar {
  width: 40vw;
  height: 50px;
  border-radius: 50px;
  background-color: #d1e4fb;
  border: 1px solid #a7caef;
  box-shadow: 0 2px 4px 0 rgba(0, 0, 0, 0.1);
  padding: 14px 20px;
  border-radius: 28px;
  height: auto;
  transition: all 0.2s ease;
}

.search-bar:focus {
  outline: unset;
  box-shadow: 0 4px 8px 0 rgba(0, 0, 0, 0.2);
  border: 1px solid #dedede;
  background-color: #fff;
}

.search-btn {
  color: #555;
  margin-left: 10px;
  font-size: 30px;
  transition: all 0.2s ease;
}

.search-btn:hover {
  cursor: pointer;
  font-size: 35px;
}

.suggestions-list {
  list-style: none;
  padding: 0;
  position: absolute;
  background-color: white;
  width: 39vw;
  box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1);
  z-index: 1000;
  border-radius: 28px;
  height: auto;
  left: 28%;
  overflow: auto;
}

.suggestions-list li {
  padding: 5px;
  margin: 0px;
  width: 38.9vw;
  border-radius: 28px;
  cursor: pointer;
  text-align: left;
}

.suggestions-list li:hover {
  background-color: #eee;
}

.full-width-banner {
  width: 100vw;
  height: 400px;
  object-fit: cover;
  object-position: center;
}
</style>
