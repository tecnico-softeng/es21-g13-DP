<template>
  <div>
    <v-row>
      <v-textarea
          v-model="sQuestionDetails.answer"
          label="Answer"
          :rules="[(v) => !!v || 'Answer is required']"
          auto-grow
          required
          data-cy="AnswerTextArea"
          rows="4"
          counter
          maxlength="1000"
      ></v-textarea>
    </v-row>
  </div>
</template>

<script lang="ts">
import { Component, Model, PropSync, Vue, Watch } from 'vue-property-decorator';
import OpenQuestionDetails from '@/models/management/questions/OpenQuestionDetails';

@Component
export default class OpenQuestionCreate extends Vue {
  @PropSync('questionDetails', { type: OpenQuestionDetails })
  sQuestionDetails!: OpenQuestionDetails;

}
</script>