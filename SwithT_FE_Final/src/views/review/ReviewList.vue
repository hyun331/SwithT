<template>

  <div class="mt-15" style=" background-color: white; color:black;">
      <v-container fluid v-if="isAuthenticated">
          <ReviewListComponent :isUserReviews="true" />
      </v-container>


      <!-- <v-container v-else>
          <v-row justify="center">
              <v-col cols="12" md="8">
                  <v-alert type="warning">로그인이 필요합니다. 로그인 후 이용해주세요.</v-alert>
              </v-col>
          </v-row>
      </v-container> -->

  </div>
</template>

<script>
import ReviewListComponent from '@/components/ReviewListComponent.vue';

export default {

  components: {
      ReviewListComponent,
  },
  data() {
      return {

          isAuthenticated: false,

      };
  },
  created() {
      this.checkAuthStatus();
  },
  methods: {

      checkAuthStatus() {
          const token = localStorage.getItem('token');
          this.isAuthenticated = !!token;
          console.log('Authenticated:', this.isAuthenticated);
      },
  },
};
</script>

<style scoped></style>