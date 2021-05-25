<template>
  <div>
    <v-row>
      <h4 class="open-answer-header"></h4>
      <v-textarea
          v-model="answerDetails.answer"
          label="Answer"
          :rules="[(v) => !!v || 'Answer is required']"
          auto-grow
          required
          data-cy="AnswerTextArea"
          counter
          maxlength="1000"
      ></v-textarea>
    </v-row>
  </div>
</template>

<script lang="ts">
import {Component, Model, Prop, PropSync, Vue, Watch} from 'vue-property-decorator';
import OpenQuestionDetails from '@/models/management/questions/OpenQuestionDetails';
import OpenQuestionAnswerType from "@/models/management/questions/OpenQuestionAnswerDetails";
import OpenQuestionStatementQuestionDetails
  from "@/models/statement/questions/OpenQuestionStatementQuestionDetails";
import OpenQuestionStatementAnswerDetails from "@/models/statement/questions/OpenQuestionStatementAnswerDetails";
import OpenQuestionStatementCorrectAnswerDetails
  from "@/models/statement/questions/OpenQuestionStatementCorrectAnswerDetails";

@Component
export default class OpenQuestionAnswer extends Vue {
  @PropSync('questionDetails', { type: OpenQuestionAnswerType })
  sQuestionDetails!: OpenQuestionDetails;
  @Prop(OpenQuestionStatementQuestionDetails)
  readonly questionDetails!: OpenQuestionStatementQuestionDetails;
  @Prop(OpenQuestionStatementAnswerDetails)
  answerDetails!: OpenQuestionStatementAnswerDetails;
  @Prop(OpenQuestionStatementCorrectAnswerDetails)
  readonly correctAnswerDetails?: OpenQuestionStatementCorrectAnswerDetails;


}
</script>

<style lang="scss">

.open-answer-header {
    padding: 8px;
    margin: 0px;
}

</style>