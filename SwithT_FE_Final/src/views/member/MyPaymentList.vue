<template>
    <v-container width="70vw" style="margin-top: 60px;">
        <v-row justify="center">
            <v-col class="main-title" cols="12" align="center" style="font-size: 28px; font-weight: 700">
                <h1>신청 내역</h1>
            </v-col>
        </v-row>

        <v-row>
            <v-col>
                <h2 style="font-size: 18px; font-weight: 700; margin-top: 20px;">결제 내역</h2>
                <v-row class="header" style="font-weight: bold; margin-top: 10px;">
                    <v-col cols="1" class="d-flex align-center"></v-col>
                    <v-col cols="3" class="d-flex align-center">강의명</v-col>
                    <v-col cols="2" class="d-flex align-center">결제일</v-col>
                    <v-col cols="2" class="d-flex align-center">결제 금액</v-col>
                    <v-col cols="2" class="d-flex align-center">상태</v-col>
                    <v-col cols="1" class="d-flex align-center">결제 영수증</v-col>
                    <v-col cols="1" class="d-flex align-center justify-center">환불</v-col>
                </v-row>
            </v-col>
        </v-row>

        <v-row class="mb-10 d-flex" align-content="center">
            <v-col v-if="paymentList.length" align-content="center">
                <v-row v-for="(payment, index) in paymentList" :key="payment.id" class="item" style="margin-top: 10px; cursor: pointer;"
                    @click="gotoLectureGroup(payment.lectureGroupId)" cursor="pointer">
                    <v-col cols="1" class="d-flex align-center">{{ index + 1 }}</v-col>
                    <v-col cols="3" class="d-flex align-center">{{ payment.name }}</v-col>
                    <v-col cols="2" class="d-flex align-center">{{ formatDate(payment.paidAt) }}</v-col>
                    <v-col cols="2" class="d-flex align-center">{{ payment.amount }} 원</v-col>
                    <v-col cols="2" class="d-flex align-center">{{ formatStatus(payment.status) }}</v-col>
                    <v-col cols="1" @click.stop="gotoReceipt(payment.receiptUrl)">
                        <v-btn @click.stop="gotoReceipt(payment.receiptUrl)" small variant="outlined">영수증</v-btn>
                    </v-col>
                    <v-col @click.stop="openRefundModal(payment)" v-if="formatStatus(payment.status) === '결제 완료'" cols="1">
                        <v-btn @click.stop="openRefundModal(payment)" small variant="outlined" >환불</v-btn>
                    </v-col>
                    <v-col v-else cols="1"></v-col>
                </v-row>
            </v-col>
            <v-col v-else>
                <div style="margin: 20px 0">결제 요청이 없습니다.</div>
            </v-col>
        </v-row>

        <v-btn :disabled="currentPage === 1" @click="previousPage"
            style="background-color:white; color:#7A6C5B; border: 1px solid #7A6C5B; font-size: 15px; margin-right: 3px;"
            outlined>
            이전 페이지
        </v-btn>
        <v-btn :disabled="currentPage === totalPages" @click="nextPage"
            style="background-color:white; color:#7A6C5B; border: 1px solid #7A6C5B; font-size: 15px; margin-left: 3px;"
            outlined>
            다음 페이지
        </v-btn>

        <!-- Refund Modal -->
        <v-dialog v-model="showRefundModal" max-width="500px">
            <v-card>
                <v-card-title class="headline">환불 요청</v-card-title>

                <v-card-text>
                    <div v-if="selectedPayment">
                        <p>강의명: {{ selectedPayment.name }}</p>
                        <p>결제 금액: {{ selectedPayment.amount }} 원</p>
                        <p>결제일: {{ formatDate(selectedPayment.paidAt) }}</p>
                    </div>

                    <!-- 환불 사유 선택 -->
                    <!-- <v-radio-group v-model="selectedReason" @change="handleReasonChange">
                        <v-radio label="단순 변심" value="변심"></v-radio>
                        <v-radio label="서비스 불만" value="서비스 불만"></v-radio>
                        <v-radio label="강의 내용 불만" value="강의 내용 불만"></v-radio>
                        <v-radio label="스케줄 변경" value="스케줄 변경"></v-radio>
                        <v-radio label="직접 입력" value="custom"></v-radio>
                    </v-radio-group> -->
                    <v-radio-group v-model="selectedReason">
                        <v-radio label="단순 변심" value="변심"></v-radio>
                        <v-radio label="서비스 불만" value="서비스 불만"></v-radio>
                        <v-radio label="강의 내용 불만" value="강의 내용 불만"></v-radio>
                        <v-radio label="스케줄 변경" value="스케줄 변경"></v-radio>
                        <v-radio label="직접 입력" value="custom"></v-radio>
                    </v-radio-group>

                    <!-- 직접 입력 필드 -->
                    <v-text-field v-if="selectedReason === 'custom'" label="직접 입력" v-model="customReason" />
                </v-card-text>

                <v-card-actions>
                    <v-btn color="blue darken-1" text @click="showRefundModal = false">취소</v-btn>
                    <v-btn color="red darken-1" text @click="submitRefund">환불 요청</v-btn>
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

export default {
    components: {
        AlertModal
    },
    data() {
        return {
            currentPage: 1,
            itemsPerPage: 10,
            totalItems: 0,
            totalPages: 0,
            paymentList: [],
            thumbnailUrls: {},
            showRefundModal: false,
            selectedPayment: null,
            selectedReason: '',
            isCustomReason: false,
            customReason: '',
            alertModal: false,
            alertModalTitle: "",
            alertModalContents: "",
        };
    },
    created() {
        this.getReceipts();
    },
    methods: {
        gotoLectureGroup(id) {
            this.$router.push(`/lecture-home/${id}`)
        },
        gotoReceipt(url) {
            window.location.href = url;
        },
        async getReceipts() {
            const response = await axios.get(`${process.env.VUE_APP_API_BASE_URL}/payment-service/my-payments`, {
                params: {
                    page: this.currentPage - 1, // 서버는 0 기반 페이지 인덱스를 사용
                    size: this.itemsPerPage
                }
            });
            this.paymentList = response.data.content;
            this.totalItems = response.data.totalItems;
            this.totalPages = response.data.totalPages;
            console.log(this.paymentList)
            // 각 결제 항목에 대해 썸네일 이미지 URL을 비동기로 가져옴
            await Promise.all(this.paymentList.map(async (payment) => {
                if (payment.lectureGroupId !== null) {
                    this.thumbnailUrls[payment.id] = await this.getThumbnail(payment.lectureGroupId);
                }
            }));
        },
        previousPage() {
            if (this.currentPage > 1) {
                this.currentPage--;
                this.getReceipts();
            }
        },
        nextPage() {
            if (this.currentPage < this.totalPages) {
                this.currentPage++;
                this.getReceipts();
            }
        },
        formatDate(date) {
            if (!date) return 'N/A';
            const options = { year: 'numeric', month: '2-digit', day: '2-digit' };
            return new Date(date).toLocaleDateString(undefined, options);
        },
        formatStatus(status) {
            switch (status) {
                case 'paid':
                    return '결제 완료';
                case 'CANCELED':
                    return '환불된 강의';
                default:
                    return '';
            }
        },
        async getThumbnail(groupId) {
            const response = await axios.get(`${process.env.VUE_APP_API_BASE_URL}/lecture-service/lecture-group/get-image-and-title/${groupId}`);
            return response.data.result.image;
        },
        openRefundModal(payment) {
            this.selectedPayment = payment;
            this.showRefundModal = true;
            this.selectedReason = '';
            this.customReason = '';
            this.isCustomReason = false;
        },
        handleReasonChange(value) {
            this.isCustomReason = value === 'custom';
        },
        submitRefund() {
            let reason = this.selectedReason === 'custom' ? this.customReason : this.selectedReason;

            // '직접 입력'이 선택되었을 경우 customReason 사용
            if (this.selectedReason === 'custom' && !this.customReason) {
                this.alertModalTtile = "환불 사유 직접 입력";
                this.alertModalContents = "환불 사유 직접 입력을 선택하셨습니다. 사유를 입력하세요.";
                this.alertModal = true;
                return;
            }

            // 환불 요청 API 호출
            axios.post(`${process.env.VUE_APP_API_BASE_URL}/payment-service/refund/${this.selectedPayment.id}`, {
                cancelReason: reason
            })
                .then(() => {
                    this.alertModalTtile = "환불 요청 완료";
                    this.alertModalContents = "환불 요청이 완료되었습니다.";
                    this.alertModal = true;
                    this.showRefundModal = false; // 모달 닫기
                    this.customReason = ''; // 환불 사유 초기화
                })
                .catch((error) => {
                    console.error("환불 요청 실패:", error);
                    this.alertModalTtile = "환불 요청 실패";
                    this.alertModalContents = `${error.response.data.error_message}`;
                    this.alertModal = true;
                });
        }
    }
};
</script>
