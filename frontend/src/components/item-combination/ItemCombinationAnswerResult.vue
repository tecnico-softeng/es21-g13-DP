<template>
  <div class="item-combination-answer-result">
    <v-col>
      <ol>
        <li v-for="item in questionDetails.itemsGroup1" :key="item.sequence">
          <v-card
              outlined
              tile
          >
            <span
                v-html="
                convertMarkDown(item.content)"
            />
            <div v-if="answerDetails.itemsId.length > 0">
              <ul>
                <li v-for="linkedItem in answeredLinks(item)" :key="linkedItem.sequence">
                  <div v-if="correctAnswerDetails.correctLinksSequence[item.sequence.valueOf()].indexOf(linkedItem.sequence) !== -1">
                  <v-card
                      outlined
                      tile
                      color = "green"
                  >
                     <!-- :color = "this.correctAnswerDetails[item.sequence].indexOf(linkedItem.sequence) !== -1 ? 'green' : 'red'" -->

                    <span
                        v-html="convertMarkDown(linkedItem.content)"
                    />
                  </v-card>
                  </div>
                  <div v-else >
                    <v-card
                        outlined
                        tile
                        color = "red"
                    >
                    <span
                        v-html="convertMarkDown(linkedItem.content)"
                    />
                    </v-card>
                  </div>
                </li>
              </ul>
            </div>
            <ul>
              <li v-for="linkedItem in correctLinks(item)" :key="linkedItem.sequence">
                <div v-if="answerDetails.itemsId.filter(link => link.sourceItem.sequence === item.sequence).length > 0 &&
                  answerDetails.itemsId.filter(link => link.sourceItem.sequence === item.sequence)[0].linkedItems.indexOf(linkedItem) === -1">
                  <v-card
                      outlined
                      tile
                      color = "green"
                  >
                    <!-- :color = "this.correctAnswerDetails[item.sequence].indexOf(linkedItem.sequence) !== -1 ? 'green' : 'red'" -->

                    <span
                        v-html="convertMarkDown(linkedItem.content)"
                    />
                  </v-card>
                </div>
              </li>
            </ul>
          </v-card>
        </li>
      </ol>
    </v-col>
    <v-col>
      <span
        v-html="answerDetails" />
    </v-col>
  </div>
</template>

<script lang="ts">
import CodeOrderSlotStatementAnswerDetails from '@/models/statement/questions/CodeOrderSlotStatementAnswerDetails';
import CodeOrderStatementAnswerDetails from '@/models/statement/questions/CodeOrderStatementAnswerDetails';
import CodeOrderStatementCorrectAnswerDetails from '@/models/statement/questions/CodeOrderStatementCorrectAnswerDetails';
import CodeOrderStatementQuestionDetails from '@/models/statement/questions/CodeOrderStatementQuestionDetails';
import { Component, Prop, Vue } from 'vue-property-decorator';
import { convertMarkDown } from '@/services/ConvertMarkdownService';
import Image from '@/models/management/Image';
import BaseCodeEditor from '@/components/BaseCodeEditor.vue';
import ItemCombinationStatementCorrectAnswerDetails
  from '@/models/statement/questions/ItemCombinationStatementCorrectAnswerDetails';
import ItemCombinationStatementAnswerDetails
  from '@/models/statement/questions/ItemCombinationStatementAnswerDetails';
import ItemCombinationStatementQuestionDetails
  from "@/models/statement/questions/ItemCombinationStatementQuestionDetails";
import StatementItem from "@/models/statement/StatementItem";

@Component({
  components: {
    BaseCodeEditor,
  },
})
export default class ItemCombinationAnswerResult extends Vue {
  @Prop(ItemCombinationStatementQuestionDetails)
  readonly questionDetails!: ItemCombinationStatementQuestionDetails;
  @Prop(ItemCombinationStatementAnswerDetails)
  readonly answerDetails!: ItemCombinationStatementAnswerDetails;
  @Prop(ItemCombinationStatementCorrectAnswerDetails)
  readonly correctAnswerDetails!: ItemCombinationStatementCorrectAnswerDetails;

  answeredLinks(item: StatementItem){
    const answeredLinksList = this.answerDetails.itemsId.filter(link => link.sourceItem.sequence == item.sequence);
    if (answeredLinksList.length > 0){
      return answeredLinksList[0].linkedItems
    } else{
      return [];
    }
  }

  correctLinks(item : StatementItem){
    return this.correctAnswerDetails.correctLinksSequence[item.sequence].map(seq => this.questionDetails.itemsGroup2.filter(
        it => it.sequence === seq)[0]);
  }

  isCorrect(element: ItemCombinationStatementAnswerDetails, index: number) {

  }

  convertMarkDown(text: string, image: Image | null = null): string {
    return convertMarkDown(text, image);
  }
}
</script>

<style lang="scss">
.incorrect-question {
  .wrong {
    .option-content {
      background-color: #cf2323;
      color: rgb(255, 255, 255) !important;
    }

    .option-letter {
      background-color: #cf2323 !important;
      color: rgb(255, 255, 255) !important;
    }
  }
  .correct {
    .option-content {
      background-color: #333333;
      color: rgb(255, 255, 255) !important;
    }

    .option-letter {
      background-color: #333333 !important;
      color: rgb(255, 255, 255) !important;
    }
  }
}
</style>