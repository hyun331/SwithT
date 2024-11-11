<template>
    <v-dialog  max-width="500px">
        <v-card>
            <v-card-title class="text-h5 text-center">

                {{title}}
            </v-card-title>
            <v-card-text class="text-center">
                {{contents}}
                <br><br>


                <v-row class="d-flex justify-center">
                    <v-col>
                        <v-btn @click="closeModal" outlined color="grey">닫기</v-btn>
                    </v-col>
                    
                </v-row>

                

            </v-card-text>
                

        </v-card>
    </v-dialog>


</template>
<script>
export default {
    props:['icon', 'title', 'contents'],
    data() {
        return {
        }
    },
    methods: {
        
        closeModal() {
            this.$emit('update:dialog', false)
        }
    },
}

</script>

<style scoped>
.small-text {
    color: grey;
    font-size: 12px;
}
</style>