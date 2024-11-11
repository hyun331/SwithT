<template>
  <div>
    <Doughnut :data="data" :options="options" />
  </div>
</template>

<script>
import { defineComponent } from 'vue';
import { Doughnut } from 'vue-chartjs';
import {
  Chart as ChartJS,
  ArcElement,
  Tooltip,
  Legend
} from 'chart.js';

// Chart.js에서 필요한 요소들을 등록합니다.
ChartJS.register(ArcElement, Tooltip, Legend);

export default defineComponent({
  name: 'DoughnutChart',
  components: {
    Doughnut
  },
  props: {
    totalDayCount: {
      type: Number,
      required: true
    },
    pastDayCount: {
      type: Number,
      required: true
    }
  },
  data() {
    return {
      // 차트에 넣을 데이터와 설정입니다.
      data: {
        datasets: [
          {
            data: [this.pastDayCount, this.totalDayCount - this.pastDayCount], // 동적으로 데이터 설정
            // data: [11, 28 - 11], // 게이지 값과 나머지 값 설정
            backgroundColor: ['#6C97FD', '#E0E0E0'], // 색상 설정
            borderWidth: 0
          }
        ]
      },
      options: {
        rotation: -90, // 차트를 반으로 회전시켜서 반원을 만듦
        circumference: 180, // 반원만 그리도록 설정
        cutout: '70%', // 가운데 공간을 비워서 게이지 차트처럼 보이게
        plugins: {
          legend: {
            display: false,
          },
          tooltip: {
            enabled: true,
            callbacks: {
              label: (tooltipItem) => {
                // 커스텀 툴팁 내용: 들은 수업과 과목 수를 표시
                const label = tooltipItem.dataset.data[tooltipItem.dataIndex];
                if (tooltipItem.dataIndex === 0) {
                  return `들은 수업: ${label}`;
                } else {
                  return `남은 수업: ${label}`;
                }
              }
            } 
          },

        },
      }
    };
  },
  mounted() {
    // Chart.js에 커스텀 플러그인 등록
    ChartJS.register({
      id: 'centerTextPlugin',
      afterDraw: (chart) => {
        const { ctx, width, height } = chart;
        const fontSize = 2; // 텍스트 크기 조절
        ctx.save();
        ctx.font = `${fontSize}em Pretendard, sans-serif`;
        ctx.textBaseline = 'middle';
        const text = `${this.pastDayCount}/${this.totalDayCount}`;
        // const text = '11/28개';
        const textX = Math.round((width - ctx.measureText(text).width) / 2);

        // 텍스트를 중앙 하단에 위치시키기 위해 Y 위치를 높이의 3/4로 설정
        const textY = height / 1.4;

        ctx.fillText(text, textX, textY); // 차트 중앙에 텍스트 추가
        ctx.restore();
      }
    });
  }
});
</script>

<style scoped>
div {
  max-width: 300px;
  /* 차트 크기 조정 */
  margin: 0 auto;
}
</style>