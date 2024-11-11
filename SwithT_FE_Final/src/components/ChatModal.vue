<template>
    <v-dialog v-model="noticeCreateModal">
        <v-card v-if="chatRoomList.length != 0"
            style="height:800px; width: 750px;  overflow: hidden;box-shadow: none; border: 2px solid #D9D9D9; border-radius: 8px;">
            <v-row justify="center">
                <v-col cols="4" class="chat-list-scroll">
                    <div>
                        <v-card-text>
                            <div v-if="chatRoomList.length == 0" class="chatRoomListEmpty" style="height:700px;">

                            </div>

                            <div v-else ref="chatListContainer" @scroll="onScrollDown">
                                <v-card v-for="chatRoom in chatRoomList" :key="chatRoom.id"
                                    :class="{ 'selected-chat-room': chatRoomId === chatRoom.chatRoomId, 'custom-border': true }"
                                    @click="changeChatRoom(chatRoom)">
                                    <v-card-title>
                                        <v-chip v-if="chatRoom.lectureType === 'LESSON'" color="primary"
                                            class="mr-2">과외</v-chip>
                                        <v-chip v-else color="primary" class="mr-2">강의</v-chip>
                                        {{ chatRoom.chatRoomTitle }}
                                    </v-card-title>
                                    <v-card-text>
                                        {{ chatRoom.memberName }}
                                    </v-card-text>
                                </v-card>

                            </div>
                        </v-card-text>
                    </div>
                </v-col>

                <v-col cols="8">
                    <div style="display: flex; justify-content: flex-end;">
                        <v-icon @click="closeModal"
                            style="cursor: pointer; margin-top:10px; margin-right:10px;">mdi-window-close</v-icon>
                    </div>
                    <v-card class="chat-card v-card-custom">
                        <v-card-title class="title-font-size">
                            {{ chatRoomTitle }}
                        </v-card-title>

                        <v-card-text class="chat-content">
                            <v-row class="chat-messages">

                                <div class="chat-history" ref="chatHistory" @scroll="onScrollUp">
                                    <div v-for="(msg, index) in chatHistory" :key="index" :class="getMessageClass(msg)">
                                        {{ msg.memberName }}
                                        <br>
                                        {{ msg.message }}
                                    </div>
                                </div>
                            </v-row>


                        </v-card-text>
                        <v-card-actions>
                            <v-row class="chat-input mt-auto">
                                <v-col cols="10" style="padding-right: 0px;">
                                    <input v-model="message" type="text" placeholder="..." class="w-100 custom-input"
                                        @keydown.enter="sendMessage" />
                                </v-col>
                                <v-col cols="2" style="padding-left:0px;">
                                    <v-btn @click="sendMessage"><v-icon style="font-size: 30px;"
                                            color="blue">mdi-send</v-icon></v-btn>
                                </v-col>
                            </v-row>
                        </v-card-actions>

                    </v-card>

                </v-col>
            </v-row>
        </v-card>
        <v-card v-else
            style="height:400px; width: 750px;  overflow: hidden;box-shadow: none; border: 2px solid #D9D9D9; border-radius: 8px;">
            <div style="display: flex; justify-content: flex-end;">
                <v-icon @click="closeModal"
                    style="cursor: pointer; margin-top:10px; margin-right:10px;">mdi-window-close</v-icon>
            </div>
            <div class="chatRoomListEmpty" style="height:700px;">
                
                <v-icon style="font-size: 40px; margin-bottom:20px;"
                    color="#11b69a">mdi-message-settings-outline</v-icon>
                <h5>채팅방이 없어요</h5>
                <div color="grey">
                    마음에 드는 과외/강의에<br /> 지원해보세요<br /><br />
                </div>

                <v-btn class="lectureLessonBtn" @click="clickHomeBtn()">과외/강의 확인하기</v-btn>

            </div>
        </v-card>

        <!-- <v-col cols="8" v-if="chatRoomList.length === 0">
                    <div style="display: flex; justify-content: flex-end;">
                        <v-icon @click="closeModal"  style="cursor: pointer; margin-top:10px; margin-right:10px;">mdi-window-close</v-icon>
                    </div>
                    <v-card class="chat-card v-card-custom">
                        <v-card-text class="chatRoomListEmpty">
                            <h5>채팅방을 선택해주세요</h5>
                            <div>채팅방이 없는 경우 진행중인 과외/강의에서 채팅을 시작해주세요</div>
                            <v-btn class="lectureLessonBtn">내 과외/강의 바로가기</v-btn>
                        </v-card-text>
                    </v-card>
                </v-col> -->

    </v-dialog>

</template>

<script>
import axios from 'axios';
import SockJS from 'sockjs-client';
import { Client as StompClient } from '@stomp/stompjs';
export default {
    props: {

        dialog: {
            type: Boolean,
            required: true
        },
        selectedChatRoomId: {
            type: String,
            required: false
        }
    },
    data() {
        return {
            noticeCreateModal: this.dialog,
            chatRoomId: this.selectedChatRoomId,
            chatRoomTitle: '',
            chatRoomList: [],

            connectionStatus: 'Disconnected',
            stompClient: null,
            message: '',
            chatHistory: [],
            lectureType: null,
            topic: '',

            size: 50,
            currentPage: 0,
            isLastPage: false,
            isLoading: false,
            isChange: false,

            currentChatListPage: 0, // Page number for pagination
            pageSize: 10, // Number of chat rooms to load per request
            isLoadingMore: false, // Prevents duplicate requests
            isEndOfList: false,

        };
    },
    watch: {
        dialog(val) {

            this.noticeCreateModal = val; // 부모 컴포넌트로부터 업데이트된 값을 반영
            
            this.chatRoomId = this.selectedChatRoomId;
            if (val) {
                this.chatRoomList = ['test'];
                this.showChatRoomList();
            }
        },
        noticeCreateModal(val) {
            this.$emit("update:dialog", val); // 부모 컴포넌트로 상태 변경을 반영
        }
    },
    mounted() {
        //채팅방 목록
        // this.showChatRoomList();
        
        //채팅 내역
        // if (this.chatRoomId != '') {
        // this.getChatRoomLogs();
        // }

        //websocket 연결
        // this.connectWebSocket();

    },

    methods: {

        async getChatRoomLogs() {
            try {
                const response = await axios.get(`${process.env.VUE_APP_API_BASE_URL}/lecture-service/room/chat/log/${this.chatRoomId}`, {
                    params: {
                        size: this.size,
                        page: this.currentPage
                    }
                });

                const olderMessages = response.data.result.content;
                console.log(olderMessages);

                if (olderMessages.length > 0) {
                    this.chatHistory = [...olderMessages, ...this.chatHistory];
                } else {
                    this.isLastPage = true;
                }
                const chatHistoryElement = this.$refs.chatHistory;
                const previousScrollHeight = chatHistoryElement.scrollHeight;
                this.currentPage += 1;
                this.isLoading = false;


                this.$nextTick(() => {
                    const newScrollHeight = chatHistoryElement.scrollHeight;
                    chatHistoryElement.scrollTop = newScrollHeight - previousScrollHeight;
                });



            } catch (error) {
                console.error("Failed to load more messages:", error);
            }




        },

        async onScrollUp() {
            const chatHistoryElement = this.$refs.chatHistory;

            if (!this.isChange && chatHistoryElement.scrollTop === 0 && !this.isLoading && !this.isLastPage) {
                this.isLoading = true;

                await this.getChatRoomLogs();

            }
            this.isChange = false;
        },


        connectWebSocket() {
            if (this.chatRoomId != '') {
                const socket = new SockJS(`${process.env.VUE_APP_API_BASE_URL}/lecture-service/ws/chat`);
                this.stompClient = new StompClient({
                    webSocketFactory: () => socket,
                    reconnectDelay: 5000,
                    onConnect: this.onConnected,
                    onStompError: this.onError
                });

                this.stompClient.activate();
            }

        },

        disconnectWebSocket() {
            this.chatHistory = [];
            if (this.stompClient && this.stompClient.connected) {
                this.stompClient.deactivate();
                this.connectionStatus = 'Disconnected';
                console.log('Disconnected from the WebSocket');
            }
        },



        async showChatRoomList() {
            if (this.isLoadingMore || this.isEndOfList) return;
            this.isLoadingMore = true;
            try {
                let params = {
                    size: 10,
                    page: this.currentChatListPage,
                    chatRoomId: this.chatRoomId,

                };
                const response = await axios.get(`${process.env.VUE_APP_API_BASE_URL}/lecture-service/room/list`, { params });

                const newRooms = response.data.result.content;

                if (newRooms.length < 10) {
                    this.isEndOfList = true; // No more pages to load
                }

                this.chatRoomList = [...this.chatRoomList, ...newRooms];
                this.currentPage += 1; // Increment page for the next load

                this.chatRoomList.shift();
                if (this.chatRoomId != '' || this.chatRoomList.length != 0) {
                    this.setChatRoom(this.chatRoomList[0]);
                }


            } catch (e) {
                console.log(e);
            }
        },
        // onScroll() {
        //     const container = this.$refs.chatListContainer;
        //     if (
        //         container.scrollTop + container.clientHeight >= container.scrollHeight - 10 && // Near bottom
        //         !this.isLoadingMore &&
        //         !this.isEndOfList
        //     ) {
        //         this.showChatRoomList(); // Load more chat rooms
        //     }
        // },


        setChatRoom(chatRoom) {
            this.chatRoomId = chatRoom.chatRoomId;
            console.log("set chat Room id " + this.chatRoomId);
            if (chatRoom.lectureType === 'LESSON') {
                this.chatRoomTitle = chatRoom.chatRoomTitle + ' - ' + chatRoom.memberName;
            } else {
                this.chatRoomTitle = chatRoom.chatRoomTitle;
            }
            this.changeChatRoom(chatRoom);
        },

        changeChatRoom(chatRoom) {
            this.disconnectWebSocket();
            this.currentPage = 0;
            this.isLastPage = false;
            this.isLoading = false;
            this.chatHistory = [];
            this.isChange = true;


            this.chatRoomId = chatRoom.chatRoomId;
            console.log("chatRoom = " + this.chatRoomId);
            if (chatRoom.lectureType === 'LESSON') {
                this.chatRoomTitle = chatRoom.chatRoomTitle + ' - ' + chatRoom.memberName;
            } else {
                this.chatRoomTitle = chatRoom.chatRoomTitle;
            }

            this.$nextTick(() => {
                const chatHistoryElement = this.$refs.chatHistory;
                if (chatHistoryElement) {
                    chatHistoryElement.scrollTop = chatHistoryElement.scrollHeight; // Reset to bottom
                }
            });


            this.getChatRoomLogs();
            this.connectWebSocket();


        },

        async onConnected(frame) {
            this.connectionStatus = 'Connected';
            console.log('Connected: ' + frame);

            this.topic = '/topic/chat-' + this.chatRoomId;


            //topic 구독
            this.stompClient.subscribe(this.topic, (message) => {
                console.log('Received message: ' + message.body);
                this.addToChatHistory(message.body);
            });

            // this.stompClient.publish({
            //     destination: `/pub/room/${this.chatRoomId}/entered`,
            //     body: JSON.stringify({ roomId: this.chatRoomId }),
            //     headers: {
            //         "Authorization": `Bearer ${localStorage.getItem('token')}`
            //     }
            // });
        },
        onError(error) {
            this.connectionStatus = 'Disconnected';
            console.error('Connection error: ' + error);
        },
        sendMessage() {
            if (this.stompClient && this.stompClient.connected && this.message.trim() !== '') {
                const chatMessage = {
                    chatRoomId: this.chatRoomId,
                    message: this.message,
                    memberId: localStorage.getItem('id'),
                    memberName: localStorage.getItem('name')
                };

                this.stompClient.publish({
                    destination: `/pub/room/${this.chatRoomId}`,
                    body: JSON.stringify(chatMessage),
                    headers: {
                        "Authorization": `Bearer ${localStorage.getItem('token')}`
                    }
                });

                this.message = '';
            }
        },
        addToChatHistory(message) {
            let parsedMessage;

            try {
                // 메시지가 문자열 형태로 들어왔을 때 파싱
                parsedMessage = typeof message === 'string' ? JSON.parse(message) : message;
            } catch (error) {
                console.error("메시지 파싱 오류:", message, error);
                return;
            }

            // 메시지를 채팅이력에 추가
            this.chatHistory.push({
                chatRoomId: parsedMessage.chatRoomId,
                memberName: parsedMessage.memberName,
                memberId: parsedMessage.memberId,
                message: parsedMessage.message
            });

            // 채팅 화면 스크롤을 자동으로 아래로
            this.$nextTick(() => {
                const chatHistoryElement = this.$refs.chatHistory;
                if (chatHistoryElement) {
                    chatHistoryElement.scrollTop = chatHistoryElement.scrollHeight;
                }
            });
        },
        getMessageClass(msg) {
            const currentUserId = localStorage.getItem('id');

            if (msg.memberId == currentUserId) {
                return 'chat-message sent';
            } else {
                return 'chat-message received';
            }
        },
        clickHomeBtn() {
            this.closeModal();
            this.$router.push("/home");
        },

        closeModal() {
            this.chatRoomList = [];
            this.isLoadingMore = false;
            this.isEndOfList = false;
            this.currentPage = 0;
            this.isLastPage = false;
            this.isLoading = false;
            this.chatHistory = [];
            this.$emit("update:dialog", false); // 모달을 닫을 때 부모에게 업데이트 알림
        },


    }


};
</script>
<style scoped>
.selected-chat-room {
    box-shadow: none !important;
    background-color: #6C97FD !important;
    color: white !important;
    margin-bottom: 10px;
    text-align: left;
}

.custom-border {
    border: 2px solid #D9D9D9;
    border-radius: 8px;
    box-shadow: none !important;
    background-color: #ffffff;
    margin-bottom: 10px;
    text-align: left;

}

.v-card-custom {
    border: 2px solid #D9D9D9;
    border-radius: 8px;
    box-shadow: none !important;
    margin-top: 10px;
}

.chat-card {

    height: 700px;
    width: 95%;
    display: flex;
    flex-direction: column;
    padding: 10px;
}

.chat-content {
    flex: 1;
    display: flex;
    flex-direction: column;
    justify-content: flex-end;
}

.chat-messages {
    flex-grow: 1;
    overflow-y: auto;
    max-height: 550px;
}



.chat-message {
    padding: 10px 15px;
    margin-bottom: 10px;
    border-radius: 20px;
    position: relative;
    max-width: 80%;
    word-wrap: break-word;
}

.sent {
    background-color: #6C97FD !important;
    color: white !important;
    align-self: flex-end;
    text-align: right;
    border-radius: 20px;
    position: relative;
    margin-left: auto;
}

.received {
    background-color: #f1f1f1;
    align-self: flex-start;
    text-align: left;
    border-radius: 20px;
    position: relative;
    margin-right: auto;
}

.sent::after {
    content: '';
    position: absolute;
    top: 10px;
    right: -8px;
    width: 0;
    height: 0;
    border-style: solid;
    border-width: 8px 0 8px 8px;
    border-color: transparent transparent transparent #6C97FD;
}

.received::after {
    content: '';
    position: absolute;
    top: 10px;
    left: -8px;
    width: 0;
    height: 0;
    border-style: solid;
    border-width: 8px 8px 8px 0;
    border-color: transparent #f1f1f1 transparent transparent;
}


.chat-history {
    width: 100%;
    display: flex;
    flex-direction: column;
    overflow-y: auto;
    max-height: 100%;
    padding: 10px;
    scrollbar-width: none;
}


.chat-message {
    padding: 5px 10px;
    margin-bottom: 5px;
    background-color: #f1f1f1;
    border-radius: 8px;
}


.chat-input {
    align-items: end;
}

.custom-input {
    height: 36px;
    border: 1px solid #ccc;
    padding: 10px;
    box-sizing: border-box;
    border-radius: 4px;
}

.chat-list-scroll {
    height: calc(800px - 12px);
    overflow-y: auto;
    padding: 10px;
    scrollbar-width: none;
    margin-top: 30px;

}


.title-font-size {
    font-size: 25px;
}

.chatRoomListEmpty {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    height: 100%;
    text-align: center;
}

.lectureLessonBtn {
    color: #4CAF50 !important;
    /* Customize green color */
    border-color: #4CAF50 !important;
}
</style>
