<!--
Used on:
  - QuestionComponent.vue
  - ResultComponent.vue
-->

<template>
  <div>
  <v-row>
    <v-col>
      <v-card
          outlined
          tile
      >
        <span
            v-html="convertMarkDown('First Group Items:')"
        />
      </v-card>
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
          </v-card>
        </li>
      </ol>
    </v-col>
      <v-col>
        <v-card
            outlined
            tile
        >
          <span
              v-html="convertMarkDown('Second Group Items:')"
          />
        </v-card>
      <ol>
        <li v-for="item in questionDetails.itemsGroup2" :key="item.sequence">
          <v-card
          outlined
          tile
          >
            <span
                v-html="convertMarkDown(item.content)"
            />
          </v-card>

        </li>
      </ol>
    </v-col>
  </v-row>
  <!-- LINKS AQUI -->
  <v-row>
  <div class="item-combination-links">
    <v-row v-for="(item, index) in questionDetails.itemsGroup1"
           :key="index">
      <v-col
          cols="12"
          sm="5"
      >
        <v-subheader v-text="'Item ' + (index+1) + ' links:'"></v-subheader>
      </v-col>
      <v-col
          cols="12"
          sm="6"
      >
        <v-select
            v-model="answerDetails.links[index]"
            :items="questionDetails.itemsGroup2"
            item-text="content"
            item-value="content"
            label="Select"
            multiple
            :data-cy="`addLinksItemCombination${index+1}`"
            chips
            hint="What are the links"
            persistent-hint
        ></v-select>

      </v-col>
    </v-row>

  </div>
  <div>
    <v-row>
      <v-col>
        <v-btn
            class="ma-auto"
            color="blue darken-1"
            @click="storeLinks"
            data-cy="saveLinksItemCombination"
        >Store Links</v-btn
        >
      </v-col>
    </v-row>
  </div>
  </v-row>
    <v-col>
      <span
        v-html="this.answerDetails">

      </span>
    </v-col>
  </div>
</template>

<script lang="ts">
import { Component, Vue, Prop, Model, Emit } from 'vue-property-decorator';
import { convertMarkDown } from '@/services/ConvertMarkdownService';
import Image from '@/models/management/Image';
import ItemCombinationStatementCorrectAnswerDetails from '@/models/statement/questions/ItemCombinationStatementCorrectAnswerDetails';
import ItemCombinationStatementAnswerDetails from '@/models/statement/questions/ItemCombinationStatementAnswerDetails';
import ItemCombinationStatementQuestionDetails from '@/models/statement/questions/ItemCombinationStatementQuestionDetails';
import StatementLink from '@/models/statement/StatementLink';

@Component
export default class ItemCombinationAnswer extends Vue {
  @Prop(ItemCombinationStatementQuestionDetails)
  readonly questionDetails!: ItemCombinationStatementQuestionDetails;
  @Prop(ItemCombinationStatementAnswerDetails)
  answerDetails!: ItemCombinationStatementAnswerDetails;
  @Prop(ItemCombinationStatementCorrectAnswerDetails)
  readonly correctAnswerDetails?: ItemCombinationStatementCorrectAnswerDetails;

  storeLinks(){
    this.answerDetails.itemsId = [];
    let counter = 0;
    for (let itemLinks of this.answerDetails.links){
      if (itemLinks.length > 0){
        const group1Item = this.questionDetails.itemsGroup1[counter];
        counter++;
        let link : StatementLink = new StatementLink();
        link.sourceItem = group1Item;
        for (let linkedContent of itemLinks){
          console.log(linkedContent);
          let id:number = this.questionDetails.itemsGroup2.indexOf(this.questionDetails.itemsGroup2.filter(it => it.content === linkedContent)[0]);
          id === -1? id = 0 : link.linkedItems.push(this.questionDetails.itemsGroup2[id]);
        }
        this.answerDetails.itemsId.push(link);
      }
    }
    this.$emit('question-answer-update');
  }

  convertMarkDown(text: string, image: Image | null = null): string {
    return convertMarkDown(text, image);
  }
}
</script>

<style lang="scss" scoped>
.unanswered {
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

.correct-question {
  .correct {
    .option-content {
      background-color: #299455;
      color: rgb(255, 255, 255) !important;
    }

    .option-letter {
      background-color: #299455 !important;
      color: rgb(255, 255, 255) !important;
    }
  }
}

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