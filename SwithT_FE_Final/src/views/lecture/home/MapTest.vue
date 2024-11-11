<template>
    <div>
        <input type="text" v-model="postcode" placeholder="우편번호">
        <!-- 스크립트 로딩 완료 후에만 버튼을 활성화합니다 -->
        <input type="button" @click="execDaumPostcode" value="우편번호 찾기" :disabled="!isDaumScriptLoaded || !isKakaoScriptLoaded"><br>
        <input type="text" v-model="roadAddress" placeholder="도로명주소">
        <input type="text" v-model="jibunAddress" placeholder="지번주소">
        <span id="guide" style="color:#999;display:none"></span>
        <input type="text" v-model="detailAddress" placeholder="상세주소">
        <input type="text" v-model="extraAddress" placeholder="참고항목">
    </div>
    <div>
        <div id="map" style="width:100%;height:350px;"></div>
    </div>
</template>

<script>
/* global kakao */
export default {
    data() {
        return {
            postcode: '',
            roadAddress: '',
            jibunAddress: '',
            detailAddress: '',
            extraAddress: '',
            isDaumScriptLoaded: false,
            isKakaoScriptLoaded: false,
        };
    },
    async mounted() {
        this.loadDaumPostcodeScript();
        this.loadKakaoMapScript();
    },
    methods: {
        loadDaumPostcodeScript() {
            const script = document.createElement('script');
            script.src = '//t1.daumcdn.net/mapjsapi/bundle/postcode/prod/postcode.v2.js';
            script.onload = () => {
                this.isDaumScriptLoaded = true;
            };
            document.head.appendChild(script);
        },
        loadKakaoMapScript() {
            const script = document.createElement('script');
            script.src = `//dapi.kakao.com/v2/maps/sdk.js?appkey=03a055c21377bee26ab1559dedf4af6f&libraries=services&autoload=false`;
            script.onload = () => {
                window.kakao.maps.load(() => {
                    this.isKakaoScriptLoaded = true;
                });
            };
            document.head.appendChild(script);
        },
        // 주소 검색 api
        execDaumPostcode() {
            if (window.daum && window.daum.Postcode) {
                // eslint-disable-next-line no-undef
                new daum.Postcode({
                    oncomplete: (data) => {
                        this.postcode = data?.zonecode;
                        this.roadAddress = data?.roadAddress;
                        this.jibunAddress = data?.jibunAddress || data?.autoJibunAddress;
                        this.detailAddress = data?.buildingName;

                        // 주소 검색한 거 기반으로 위도 경도
                        // 좌표 검색을 위해 Kakao 지도 Geocoder 사용
                        const geocoder = new kakao.maps.services.Geocoder();
                        geocoder.addressSearch(this.roadAddress, (result, status) => {
                            if (status === kakao.maps.services.Status.OK) {
                                console.log('위도 : ' + result[0].y);
                                console.log('경도 : ' + result[0].x);

                                // 지도에 마커를 추가하는 로직
                                this.initMap(result[0].y, result[0].x);
                            }
                        });
                    }
                }).open();
            } else {
                console.error("Daum Postcode 스크립트가 로드되지 않았습니다.");
            }
        },
        initMap(latitude, longitude) {
            const mapContainer = document.getElementById('map');
            const mapOption = {
                center: new kakao.maps.LatLng(latitude, longitude),
                level: 3
            };
            const map = new kakao.maps.Map(mapContainer, mapOption);

            const markerPosition = new kakao.maps.LatLng(latitude, longitude);
            const marker = new kakao.maps.Marker({
                position: markerPosition
            });
            marker.setMap(map);
        }
    }
}
</script>
