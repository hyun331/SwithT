<template>
  <v-container
    class="mt-15 d-flex justify-center align-center"
    style="height: 50vh;"
  >
    <div>
      <v-btn
      @click="showModal"
      style="background-color: #6c97fd; width: 200px; height: 50px; border: 1px solid #003366;" 
      color="white"
    >
      <v-icon left>mdi-pencil</v-icon>
      <b>리뷰 작성</b>
    </v-btn>
    
      <ReviewCreate ref="ReviewCreate" />
    </div>
  </v-container>
</template>
  
  <script>
  import ReviewCreate from '@/components/ReviewCreate.vue';
  
  export default {
    components: {
        ReviewCreate,
    },
    methods: {
      showModal() {
        this.$refs.ReviewCreate.dialog = true; // 모달 열기
      },
    },
  };
  </script>
  