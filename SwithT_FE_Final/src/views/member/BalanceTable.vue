<template>
  <!-- <v-container fluid class="py-4 px-2 mt-15" :style="{
    maxWidth: '1650px',
    maxHeight: '1400px',
    display: 'flex',
    flexDirection: 'column',
  }"> -->
  <v-container width="70vw" style="margin-top: 60px;">
    <v-row justify="center">
      <v-col class="main-title" cols="12" align="center" style="font-size: 28px; font-weight: 700">
        <p>정산금</p>
      </v-col>
    </v-row>
    <!-- 상단 출금 가능 금액과 예상 수익금 -->
    <v-row>
      <v-col cols="6">
        <v-card class="pa-4 d-flex" elevation="3" :style="{ borderRadius: '12px' }" min-height="132px">
          <v-row>
            <!-- 출금 가능 금액 -->
            <v-col class="text-center">
              <v-row align="center" justify="center">
                <v-col cols="auto" class="text-h6" :style="{ color: '#1A237E', marginRight: '1px' }" align="center">
                  <strong>출금 가능 금액</strong>
                </v-col>
              </v-row>
              <div class="text-h4 font-weight-bold mb-1">
                {{ formatMoney(memberInfo.availableMoney) }}원
              </div>
              <v-btn color="primary" outlined @click="showModal = true" style="padding: 4px 12px;">
                출금
              </v-btn>
            </v-col>
          </v-row>
        </v-card>
      </v-col>
      <v-col cols="6">
        <v-card class="pa-4 d-flex" elevation="3" :style="{ borderRadius: '12px' }" min-height="132px">
          <v-row>
            <v-col class="text-center">
              <div class="text-h6" :style="{ color: '#1A237E' }">
                <strong>예상 수익금</strong>
              </div>
              <div class="text-h4 font-weight-bold">{{ formatMoney(expectedBalance) }}원</div>
            </v-col>
            <v-col cols="auto">
            </v-col>
          </v-row>
        </v-card>
      </v-col>
    </v-row>

    <!-- 차트 -->
    <v-row>
      <v-col>
        <v-card class="pa-4 mb-4" elevation="3" :style="{ borderRadius: '12px' }">
          <div class="text-h5 mb-4" :style="{ color: 'black' }">
            <strong>매출 차트</strong>
          </div>
          <div class="d-flex flex-row-reverse">
            <v-btn @click="months = 12" variant="text">1Y</v-btn>

            <v-btn @click="months = 6" variant="text">6M</v-btn>
          </div>
          <div class="chart-container"
            style="display: flex; justify-content: center; position: relative; height: 35vh; width: 100%;">
            <canvas id="earningsChart" style="width: 80%"></canvas>
          </div>
        </v-card>
      </v-col>
    </v-row>
    <v-row>
      <v-col cols="6">
        <v-card elevation="3" min-height="600px">
          <v-row>
            <!-- 출금 가능 금액 -->
            <v-col class="text-center">
              <v-row align="center" justify="center" class="mt-5 mb-5" :style="{ color: '#1A237E' }">
                <h5><strong>수익 목록</strong></h5>
              </v-row>
            </v-col>
          </v-row>
          <v-row align="center" justify="center">
            <v-col cols="10" class="text-center" justify="center" :style="{ textAlign: 'center' }">
              <!-- 테이블 헤더 -->
              <div class="text-center" justify="center">
                <v-row class="header text-center" justify="center">
                  <v-col cols="3"><strong>날짜</strong></v-col>
                  <v-col cols="6"><strong>항목</strong></v-col>
                  <v-col cols="3"><strong>금액</strong></v-col>
                </v-row>

                <!-- 데이터 리스트 -->
                <v-row v-for="(item, index) in incomeData" :key="index" class="item text-center" justify="center"
                  :style="{ textAlign: 'center' }">
                  <v-col cols="3">{{ item.date }}</v-col>
                  <v-col cols="6">{{ item.description }}</v-col>
                  <v-col cols="3">{{ item.amount }}</v-col>
                </v-row>
              </div>

            </v-col>
          </v-row>
          <v-row justify="center" class="mt-4" :style="{ position: 'absolute', bottom: '20px', width: '100%' }">
            <v-pagination v-model="incomePage" :length="incomeTotalPages"
              @click="handleIncomePageChange"></v-pagination>
          </v-row>
        </v-card>
      </v-col>
      <v-col cols="6">
        <v-card elevation="3" min-height="600px">
          <v-row>
            <!-- 출금 가능 금액 -->
            <v-col class="text-center">
              <v-row align="center" justify="center" class="mt-5 mb-5" :style="{ color: '#1A237E' }">
                <h5><strong>출금 목록</strong></h5>
              </v-row>
            </v-col>
          </v-row>
          <v-row align="center" justify="center" class="text-center justify-center">
            <v-col cols="10" class="justify-center">
              <!-- 테이블 헤더 -->
              <v-row class="header">
                <v-col cols="3"><strong>날짜</strong></v-col>
                <v-col cols="6"><strong>항목</strong></v-col>
                <v-col cols="3"><strong>금액</strong></v-col>
              </v-row>

              <!-- 데이터 리스트 -->
              <v-row v-for="(item, index) in withdrawalData" :key="index" class="item">
                <v-col cols="3">{{ item.date }}</v-col>
                <v-col cols="6">{{ item.description }}</v-col>
                <v-col cols="3">{{ item.amount }}</v-col>
              </v-row>

              <!-- 데이터가 없을 때 -->
              <!-- <v-row v-if="filteredData.length==0" class="text-center">
                <v-col>
                  <v-card class="pa-4 mb-3" min-height="200px">
                    <h3>등록된 내역이 없습니다</h3>
                  </v-card>
                </v-col>
              </v-row> -->
            </v-col>
          </v-row>
          <v-row justify="center" class="mt-4" :style="{ position: 'absolute', bottom: '20px', width: '100%' }">

            <v-pagination v-model="withdrawalPage" :length="withdrawalTotalPages"
              @click="handleWithdrawalPageChange"></v-pagination>
          </v-row>
        </v-card>
      </v-col>
    </v-row>

    <!-- 출금 모달 -->
    <v-dialog v-model="showModal" max-width="500px">
      <v-card style="border-radius: 12px">
        <!-- 제목에 색상 추가 -->
        <v-card-title class="d-flex justify-center text-h5"
          style="font-size: 1.4rem; font-weight: bold; color: white; background-color: #1a237e; padding: 12px; border-radius: 0px;">
          출금 요청
        </v-card-title>

        <v-card-text>
          <v-form ref="withdrawalForm">
            <!-- 출금 가능 금액 (v-text-field 모양, 읽기 전용) -->
            <v-row class="mb-1" align="center">
              <v-col class="text-left mt-15" cols="12">
                <v-text-field label="출금 가능 금액" v-model="formattedAvailableMoney" readonly variant="outlined" outlined
                  style="border-radius: 8px; width: 100%;"></v-text-field>
              </v-col>
            </v-row>

            <!-- 출금 요청 금액 입력 -->
            <v-row align="center">
              <v-col class="text-left" cols="12">
                <v-text-field label="출금할 금액" v-model="withdrawAmount" required variant="outlined" clearable
                  @input="updateRemainingMoney" style="border-radius: 8px; width: 100%;"></v-text-field>
              </v-col>
            </v-row>

            <!-- 출금 예정 금액 (v-text-field 모양, 읽기 전용) -->
            <v-row class="mb-10" align="center">
              <v-col class="text-left" cols="12">
                <v-text-field label="출금 예정 금액" v-model="formattedRemainingMoney" readonly outlined
                  style="border-radius: 8px; width: 100%;" variant="outlined"></v-text-field>
              </v-col>
            </v-row>
          </v-form>
        </v-card-text>

        <!-- 확인/취소 버튼 -->
        <v-card-actions class="d-flex justify-center mt-4">
          <v-btn style="background-color: #6c97fd; width: 100px; height: 40px; border-radius: 8px; color: white;"
            @click="handleWithdrawal">
            확인
          </v-btn>
          <v-btn
            style="background-color: #82d691; width: 100px; height: 40px; border-radius: 8px; color: white; margin-left: 16px;"
            @click="showModal = false">
            취소
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
    <AlertModal v-model="alertModal" @update:dialog="alertModal = $event" icon=mdi-alert-circle-outline
      :title=this.alertModalTitle :contents=this.alertModalContents />
  </v-container>
</template>

<script>
import axios from "axios";
import AlertModal from "@/components/AlertModal.vue";

import {
  Chart,
  CategoryScale,
  LinearScale,
  BarElement,
  Title,
  Tooltip,
  Legend,
  BarController
} from "chart.js";

Chart.register(CategoryScale, LinearScale, BarElement, Title, Tooltip, Legend , BarController);

export default {
  components: {
    AlertModal
  },
  async created() {
    try {
      const response = await axios.get(
        `${process.env.VUE_APP_API_BASE_URL}/member-service/infoGet`
      );
      // withdrawal : 출금 , balance : 정산
      this.memberInfo = response.data.result;
      console.log(this.memberInfo)

      // 출금 차트 데이터 가져오기
      const chartWithdrawalResponse = await axios.get(
        `${process.env.VUE_APP_API_BASE_URL}/member-service/chart/withdrawal?months=${this.months}`
      );
      this.labels = chartWithdrawalResponse.data.result.labels;
      this.withdrawalChart = chartWithdrawalResponse.data.result.data;
      const chartBalanceResponse = await axios.get(`${process.env.VUE_APP_API_BASE_URL}/payment-service/chart/balance?months=${this.months}`);
      this.balanceChart = chartBalanceResponse.data.result.data
      const ExpectedBalanceResponse = await axios.get(`${process.env.VUE_APP_API_BASE_URL}/payment-service/balanceExpected`);
      this.expectedBalance = ExpectedBalanceResponse.data.result;
      console.log("chart" + JSON.stringify(this.withdrawalChart))
      console.log("balanceChart" + JSON.stringify(chartBalanceResponse.data.result.data))

      // 차트 데이터를 불러온 후 drawChart 메서드 호출
      this.drawChart();

      const BalanceListResponse = await axios.get(`${process.env.VUE_APP_API_BASE_URL}/payment-service/list/balance?page=0&size=5`);
      console.log("BalanceListResponse", BalanceListResponse)
      const withDrawalListResponse = await axios.get(`${process.env.VUE_APP_API_BASE_URL}/member-service/list/withdrawal?page=0&size=5`);
      console.log("withDrawalListResponse", withDrawalListResponse)

      // 출금 리스트 데이터 처리
      this.withdrawalData = withDrawalListResponse.data.result.content.map((data) => ({
        date: data.requestTime.split("T")[0],
        description: data.description,
        amount: data.requestAmount.toLocaleString(),
      }));
      this.incomeData = BalanceListResponse.data.result.content.map((data) => ({
        date: data.createdTime.split("T")[0],
        description: data.description,
        amount: data.incomeAmount
      }))
      this.incomeTotalPages = BalanceListResponse.data.result.totalPages;
    } catch (error) {
      console.error("데이터 로드 중 오류 발생:", error);
    }
    if (typeof window !== 'undefined') {
      const resizeObserverErr = (e) => {
        if (e.message === 'ResizeObserver loop limit exceeded') {
          // 경고 무시
        } else {
          throw e;
        }
      };

      window.addEventListener('error', resizeObserverErr);
    }
  },
  watch: {
    months: async function () {
      await this.fetchChartData();
    },
  },
  methods: {
    // async incomeList() {
    //   try {
    //     let params = {
    //       size: this.size,
    //       page: this.incomePage 
    //     }
    //     const BalanceListResponse = await axios.get(`${process.env.VUE_APP_API_BASE_URL}/payment-service/list/balance`, { params });
    //     this.incomeData = BalanceListResponse.data.result.content.map((data) => ({
    //       date: data.createdTime.split("T")[0],
    //       description: data.description,
    //       amount: data.incomeAmount
    //     }))
    //     this.incomeTotalPages = BalanceListResponse.data.result.totalPages;
    //   }
    //   catch (e) {
    //     console.log(e.response.data.error_message);
    //   }
    // },
    // handleIncomePageChange() {
    //   this.page = this.incomePage - 1;
    //   this.incomeList();
    // },

    // handleWithdrawalPageChange() {
    //   this.page = this.incomePage - 1;
    //   this.incomeList();
    // },
    // async withdrawalList() {
    //   try {
    //     let params = {
    //       size: this.size,
    //       page: this.page
    //     }
    //     const withDrawalListResponse = await axios.get(`${process.env.VUE_APP_API_BASE_URL}/member-service/list/withdrawal`, { params });
    //     this.withdrawalData = withDrawalListResponse.data.result.content.map((data) => ({
    //       date: data.requestTime.split("T")[0],
    //       description: data.description,
    //       amount: data.requestAmount.toLocaleString(),
    //     }))
    //     this.withdrawalTotalPages = withDrawalListResponse.data.result.totalPages;
    //   }
    //   catch (e) {
    //     console.log(e.response.data.error_message);
    //   }
    // },
    async incomeList() {
      try {
        let params = {
          size: this.pageSize,
          page: this.incomePage - 1 // incomePage를 사용
        };
        const BalanceListResponse = await axios.get(`${process.env.VUE_APP_API_BASE_URL}/payment-service/list/balance`, { params });
        this.incomeData = BalanceListResponse.data.result.content.map((data) => ({
          date: data.createdTime.split("T")[0],
          description: data.description,
          amount: data.incomeAmount
        }));
        this.incomeTotalPages = BalanceListResponse.data.result.totalPages;
      } catch (e) {
        console.log(e.response.data.error_message);
      }
    },

    handleIncomePageChange() {
      this.incomeList();
    },

    async withdrawalList() {
      try {
        let params = {
          size: this.pageSize,
          page: this.withdrawalPage - 1 // withdrawalPage를 사용
        };
        const withDrawalListResponse = await axios.get(`${process.env.VUE_APP_API_BASE_URL}/member-service/list/withdrawal`, { params });
        this.withdrawalData = withDrawalListResponse.data.result.content.map((data) => ({
          date: data.requestTime.split("T")[0],
          description: data.description,
          amount: data.requestAmount.toLocaleString(),
        }));
        this.withdrawalTotalPages = withDrawalListResponse.data.result.totalPages;
      } catch (e) {
        console.log(e.response.data.error_message);
      }
    },

    handleWithdrawalPageChange() {
      this.withdrawalList();
    },
    async fetchChartData() {
      try {
        // 출금 차트 데이터 가져오기
        const chartWithdrawalResponse = await axios.get(
          `${process.env.VUE_APP_API_BASE_URL}/member-service/chart/withdrawal?months=${this.months}`
        );
        this.labels = chartWithdrawalResponse.data.result.labels;
        this.withdrawalChart = chartWithdrawalResponse.data.result.data;
        console.log("this.withdrawalChart", this.withdrawalChart)

        const chartBalanceResponse = await axios.get(
          `${process.env.VUE_APP_API_BASE_URL}/payment-service/chart/balance?months=${this.months}`
        );
        this.balanceChart = chartBalanceResponse.data.result.data;
        console.log("this.balanceChart", this.balanceChart)

        // 차트 데이터를 불러온 후 drawChart 메서드 호출
        this.drawChart();
      } catch (error) {
        console.error("차트 데이터를 가져오는 중 오류 발생:", error);
      }
    },
    drawChart() {
      if (this.chart) {
        this.chart.destroy();
      }
      const ctx = document.getElementById("earningsChart").getContext("2d");
      this.chart = new Chart(ctx, {
        type: "bar",
        data: {
          labels: this.labels,  // this.labels 사용
          datasets: [
            {
              label: "수익",
              backgroundColor: "#3949AB",
              data: this.balanceChart, // 예시 데이터 (실제 수익 데이터로 교체 필요)
              barThickness: 25,
            },
            {
              label: "출금",
              backgroundColor: "#FF1744",
              data: this.withdrawalChart, // 예시 데이터 (실제 출금 데이터로 교체 필요)
              barThickness: 25,
            },
          ],
        },
        options: {
          responsive: true,
          maintainAspectRatio: false,
          scales: {
            y: {
              beginAtZero: true,
            },
          },
        },
      });
    },

    formatMoney(value) {
      return value ? value.toLocaleString() : "0";
    },

    updateRemainingMoney() {
      const requestedAmount = this.withdrawAmount || 0;
      this.remainingMoney = requestedAmount * 0.9;
    },

    async handleWithdrawal() {
      if (!this.withdrawAmount || this.withdrawAmount <= 0) {
        this.alertModalTtile = "출금 금액 재입력 요청";
        this.alertModalContents = "유효한 출금 금액을 입력하세요.";
        this.alertModal = true;
        return;
      }

      try {
        const dto = {
          amount: this.withdrawAmount,
          requestTime: new Date().toISOString(),
        };

        await axios.post(
          `${process.env.VUE_APP_API_BASE_URL}/member-service/withdrawal`,
          dto
        );

        this.showModal = false;
        this.alertModalTtile = "출금 요청 완료";
        this.alertModalContents = "출금 요청 완료되었습니다.";
        this.alertModal = true;
        window.location.reload();
      } catch (error) {
        console.error(error);
        this.alertModalTtile = "출금 요청 실패";
        this.alertModalContents = "출금 요청이 실패되었습니다.";
        this.alertModal = true;
      }
    },
  },
  computed: {
    filteredData() {
      // tab에 따라 수익 또는 출금 데이터를 반환
      return this.tab === 0 ? this.incomeData : this.withdrawalData;
    },

    formattedAvailableMoney() {
      return this.formatMoney(this.memberInfo.availableMoney);
    },

    formattedRemainingMoney() {
      return this.formatMoney(this.remainingMoney);
    },
  },
  data() {
    return {
      page: 0,
      incomePage: 0,
      incomeTotalPages: 1,
      withdrawalPage: 0,
      withdrawalTotalPages: 1,
      pageSize: 5,
      months: 6,
      memberInfo: {},
      labels: [],
      withdrawalChart: [],
      balanceChart: [],
      tab: 0,
      showModal: false,
      withdrawAmount: "",
      expectedBalance: 0,
      remainingMoney: 0,
      headers: [
        { text: "날짜", value: "date" },
        { text: "수익/출금 항목", value: "description" },
        { text: "금액", value: "amount" },
      ],
      withdrawalData: [],
      incomeData: [
      ],


      alertModal: false,
      alertModalTitle: "",
      alertModalContents: "",
    };
  },
};
</script>

<style scoped>
.text-h4 {
  font-size: 1.6rem;
}

.chart-container {
  max-width: 100%;
}
</style>
