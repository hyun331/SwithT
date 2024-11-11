<template>
  <v-container>
    <!-- 검색창 및 추천 검색어 목록 -->
    <div class="search-container" ref="searchBar" style="margin-top: 80px">
      <input
        v-model="searchValue"
        dense
        hide-details="true"
        class="search-bar"
        placeholder="SwithT와 함께 배우고, 나누고, 성장하세요"
        @input="fetchSuggestions"
        @keyup.enter="performSearch"
      />
      <span @click="performSearch" class="search-btn mdi mdi-magnify"></span>
      <!-- 추천 검색어 표시 -->
      <ul v-if="suggestions.length > 0" class="suggestions-list" :style="{ top: suggestionsTop + 'px' }">
        <li
          v-for="(suggestion, index) in suggestions"
          :key="index"
          @click="selectSuggestion(suggestion)"
        >
          {{ suggestion }}
        </li>
      </ul>
    </div>

    <!-- 카테고리 메뉴 -->
    <section class="menu mt-5" style="margin-top: 20px">
      <div class="menu-list" :class="{ 'highlighted': selectedCategory === 'CAREER' }" @click="performCategorySearch('CAREER')">
        <img src="@/assets/manager_2704454.png" class="menu-icon">
        <div class="menu-title">취업/직무</div>
      </div>
      <div class="menu-list" :class="{ 'highlighted': selectedCategory === 'DEVELOPMENT' }" @click="performCategorySearch('DEVELOPMENT')">
        <img src="@/assets/target_2656366.png" class="menu-icon">
        <div class="menu-title">자기계발</div>
      </div>
      <div class="menu-list" :class="{ 'highlighted': selectedCategory === 'ADMISSION' }" @click="performCategorySearch('ADMISSION')">
        <img src="@/assets/mortarboard_2655764.png" class="menu-icon">
        <div class="menu-title">입시</div>
      </div>
      <div class="menu-list" :class="{ 'highlighted': selectedCategory === 'HOBBY' }" @click="performCategorySearch('HOBBY')">
        <img src="@/assets/painting_2655642.png" class="menu-icon">
        <div class="menu-title">취미</div>
      </div>
      <div class="menu-list" :class="{ 'highlighted': selectedCategory === 'LECTURE' }" @click="performTypeSearch('LECTURE')">
        <img src="@/assets/meeting-room_2645420.png" class="menu-icon">
        <div class="menu-title">강의</div>
      </div>
      <div class="menu-list" :class="{ 'highlighted': selectedCategory === 'LESSON' }" @click="performTypeSearch('LESSON')">
        <img src="@/assets/conversation_2821731.png" class="menu-icon">
        <div class="menu-title">과외</div>
      </div>
    </section>

    <br>
    <br>
    <!-- 검색 결과 -->
    <div v-if="searchResult.length === 0">검색 결과가 없습니다.</div>
    <div v-else class="lecture-list mr-15" style="display: flex; flex-wrap: wrap; gap: 10px; justify-content: center;">
      <div
        v-for="lecture in searchResult"
        :key="lecture.id"
        class="component"
        @click="goToLecture(lecture.id)"
        style="width: 320px; border-radius: 10px; box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1); margin: 8px;"
      >
        <img
          :src="getlectureImage(lecture)"
          alt="강의 썸네일"
          class="lecture-image"
          style="border-radius: 15px 15px 0 0; width: 100%; height: 220px; object-fit: cover;"
        />
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
import axios from 'axios';

export default {
  data() {
    return {
      searchValue: "",
      suggestions: [],  // 추천 검색어 저장 배열
      searchResult: [], // 검색 결과 저장할 배열
      selectedCategory: "", // 선택된 카테고리를 저장
      suggestionsTop: 0,
    };
  },
  async mounted() {
    // 초기 검색 요청
    this.fetchSearchResults();
    this.updateSuggestionsPosition();
    window.addEventListener("resize", this.updateSuggestionsPosition);
  },
  watch: {
    // 라우터의 쿼리 파라미터가 변경될 때마다 검색 결과 갱신
    '$route.query': 'fetchSearchResults',
  },
  methods: {
    // 강의 이미지 가져오기
    getlectureImage(lecture) {
      return lecture.image;
    },
    // 추천 검색어 가져오기
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
    // 검색 버튼 클릭 시 실행
    performSearch() {
      const requestData = {
        searchTitle: this.searchValue,
        category: "",  
        status: "ADMIT",
        lectureType: ""
      };
      this.$router.push({ name: 'SearchResult', query: requestData });
    },
    // 카테고리별 검색
    performCategorySearch(category) {
        this.selectedCategory = category;  // 선택된 카테고리 설정
        const requestData = {
            searchTitle: "",
            category: category,  
            status: "ADMIT",
            lectureType: ""
        };
        this.searchValue = "";
        this.$router.push({ name: 'SearchResult', query: requestData }).then(() => {
            this.fetchSearchResults();  // 라우터 변경 후 검색 결과 갱신
        });
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
    // 추천 검색어 클릭 시 실행
    selectSuggestion(suggestion) {
      this.searchValue = suggestion;
      this.performSearch();
      this.suggestions = [];
    },
    // 강의 상세 페이지로 이동
    goToLecture(lectureId) {
      this.$router.push({ name: "LectureDetail", params: { id: lectureId } });
    },
    // 카테고리 텍스트 변환
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
  updated() {
    this.updateSuggestionsPosition();
  },
  beforeUnmount() {
    window.removeEventListener("resize", this.updateSuggestionsPosition);
  },
};
</script>

<style scoped>
.v-container {
    margin: 0 auto;
    max-width: 90%; /* 전체 폭을 최대한 꽉 차게 설정 */
    color: #333;
}

.menu {
    margin: 20px 0;
    display: flex;
    justify-content: center;
    gap: 15px;
}

.menu-list {
    width: 80px;
    height: 110px;
    text-align: center;
}

.menu-list:hover {
    background-color: #EEE;
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

.highlighted {
    background-color: #EEE;
    border-radius: 10px;
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
  left: 26.5%;
  width: 38vw;
  box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1);
  z-index: 1000;
  border-radius: 28px;
  height: auto;
  overflow-x: hidden; /* 좌우 스크롤 비활성화 */
  overflow-y: auto;   /* 상하 스크롤 활성화 */
  white-space: nowrap; /* 텍스트가 줄 바꿈되지 않도록 설정 */
}
.suggestions-list li {
  padding: 5px;
  margin: 0px;
  width: 37w;
  border-radius: 28px;
  cursor: pointer;
  text-align: left;
}
.suggestions-list li:hover {
  background-color: #eee;
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
    width: 100%;
    height: 220px;
    object-fit: cover;
    border-radius: 15px 15px 0 0;
}

.lecture-info {
    text-align: left;
    padding: 10px 15px;
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
    padding: 3px 0;
}

.lecture-tutor {
    font-size: 16px;
}
</style>
