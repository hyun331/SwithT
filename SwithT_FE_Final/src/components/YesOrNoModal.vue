<template>
  <v-dialog v-model="noticeCreateModal" max-width="500px">
    <v-card>
      <v-card-title class="text-h5 text-center">
        <v-icon v-if="icon" color="red" large>{{ icon }}</v-icon>
        <br>
        {{ title }}
      </v-card-title>
      <v-card-text class="text-center">
        {{ contents }}
        <br /><br />

        <v-row class="d-flex justify-center">
          <v-col cols="6" class="text-right">
            <v-btn @click="closeModal" outlined color="grey">닫기</v-btn>
          </v-col>
          <v-col cols="6" class="text-left">
            <v-btn @click="clickYes" color="success">{{ yesBtnName }}</v-btn>
          </v-col>
        </v-row>
      </v-card-text>
    </v-card>
  </v-dialog>
</template>

<script>
export default {
  props: {
    icon: {
      type: String,
      default: null // 아이콘을 선택적으로 사용할 수 있도록 설정
    },
    title: {
      type: String,
      required: true
    },
    contents: {
      type: String,
      required: true
    },
    yesBtnName: {
      type: String,
      required: true
    },
    dialog: {
      type: Boolean,
      required: true
    }
  },
  data() {
    return {
      noticeCreateModal: this.dialog // dialog 상태를 로컬 데이터로 설정
    };
  },
  watch: {
    dialog(val) {
      this.noticeCreateModal = val; // 부모 컴포넌트로부터 업데이트된 값을 반영
    },
    noticeCreateModal(val) {
      this.$emit("update:dialog", val); // 부모 컴포넌트로 상태 변경을 반영
    }
  },
  methods: {
    clickYes() {
      this.$emit("confirmed"); // 결제 등 주요 동작을 위한 확인 이벤트
    },
    closeModal() {
      this.$emit("update:dialog", false); // 모달을 닫을 때 부모에게 업데이트 알림
    }
  }
};
</script>

<style scoped>
.small-text {
  color: grey;
  font-size: 12px;
}
</style>
