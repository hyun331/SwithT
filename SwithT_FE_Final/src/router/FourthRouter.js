import SearchResultComponent from "@/components/search/SearchResultComponent.vue"
import MyPaymentList from "@/views/member/MyPaymentList.vue"

export const FourthRouter = [
    {
        path: '/search',
        name: 'SearchResult',
        component: SearchResultComponent
    },
    {
        path: '/payments/list',
        name: `MyPaymentList`,
        component: MyPaymentList
    }

]