<!--
Used on:
  - QuestionComponent.vue
  - ResultComponent.vue
-->

<template>
  <ul data-cy="optionList" class="option-list">
    <li
      v-for="(n, index) in questionDetails.options.length"
      :key="index"
      v-bind:class="['option', optionClass(index)]"
      @click="
        !isReadonly && selectOption(questionDetails.options[index].optionId)
      "
    >
      <span
        v-if="
          isReadonly &&
          correctAnswerDetails.correctOptionsId.indexOf(
            questionDetails.options[index].optionId
          ) > -1
        "
        class="option-letter"
        >{{
          correctAnswerDetails.correctOptionsId.indexOf(
            questionDetails.options[index].optionId
          ) + 1
        }}
      </span>
      <span
        v-else-if="
          isReadonly &&
          answerDetails.optionsId.indexOf(
            questionDetails.options[index].optionId
          ) > -1
        "
        class="fas fa-times option-letter"
      />
      <span v-else class="option-letter">{{
        String.fromCharCode(65 + index)
      }}</span>
      <span
        class="option-content"
        v-html="convertMarkDown(questionDetails.options[index].content)"
      />
      <span
        v-if="
          answerDetails.optionsId.indexOf(
            questionDetails.options[index].optionId
          ) > -1
        "
        class="selected-option"
        >{{
          answerDetails.optionsId.indexOf(
            questionDetails.options[index].optionId
          ) + 1
        }}</span
      >
    </li>
  </ul>
</template>

<script lang="ts">
import { Component, Vue, Prop, Model, Emit } from 'vue-property-decorator';
import MultipleChoiceStatementQuestionDetails from '@/models/statement/questions/MultipleChoiceStatementQuestionDetails';
import { convertMarkDown } from '@/services/ConvertMarkdownService';
import Image from '@/models/management/Image';
import MultipleChoiceStatementAnswerDetails from '@/models/statement/questions/MultipleChoiceStatementAnswerDetails';
import MultipleChoiceStatementCorrectAnswerDetails from '@/models/statement/questions/MultipleChoiceStatementCorrectAnswerDetails';

@Component
export default class MultipleChoiceAnswer extends Vue {
  @Prop(MultipleChoiceStatementQuestionDetails)
  readonly questionDetails!: MultipleChoiceStatementQuestionDetails;
  @Prop(MultipleChoiceStatementAnswerDetails)
  answerDetails!: MultipleChoiceStatementAnswerDetails;
  @Prop(MultipleChoiceStatementCorrectAnswerDetails)
  readonly correctAnswerDetails?: MultipleChoiceStatementCorrectAnswerDetails;

  get isReadonly() {
    return !!this.correctAnswerDetails;
  }

  optionClass(index: number) {
    let ansIdx = this.answerDetails.optionsId.indexOf(
      this.questionDetails.options[index].optionId
    );
    if (this.isReadonly) {
      let idx = -1;
      if (!!this.correctAnswerDetails)
        idx = this.correctAnswerDetails.correctOptionsId.indexOf(
          this.questionDetails.options[index].optionId
        );
      if (!!this.correctAnswerDetails && idx != -1 && idx == ansIdx) {
        return 'correct';
      } else if (ansIdx > -1) {
        return 'wrong';
      } else {
        return '';
      }
    } else {
      return ansIdx > -1 ? 'selected' : '';
    }
  }

  @Emit('question-answer-update')
  selectOption(optionId: number) {
    const idx = this.answerDetails.optionsId.indexOf(optionId);
    if (idx > -1) {
      this.answerDetails.optionsId.splice(idx, 1);
    } else {
      this.answerDetails.optionsId.push(optionId);
    }
    console.log(this.answerDetails.optionsId);
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
    .selected-option {
      color: rgb(255, 255, 255);
      cursor: pointer;
      float: left;
      text-align: center;
      text-decoration: none solid rgb(255, 255, 255);
      text-size-adjust: 100%;
      height: 70px;
      width: 70px;
      column-rule-color: rgb(255, 255, 255);
      perspective-origin: 35px 35px;
      transform-origin: 35px 35px;
      user-select: text;
      caret-color: rgb(255, 255, 255);
      background: rgb(41, 148, 85) none repeat scroll 0 0 / auto padding-box
        border-box;
      border: 0 none rgb(255, 255, 255);
      list-style: none outside none;
      outline: rgb(255, 255, 255) none 0;
      font-size: 44px;
      display: flex;
      justify-content: center;
      flex-direction: column;
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
    .selected-option {
      color: rgb(255, 255, 255);
      cursor: pointer;
      float: left;
      text-align: center;
      text-decoration: none solid rgb(255, 255, 255);
      text-size-adjust: 100%;
      height: 70px;
      width: 70px;
      column-rule-color: rgb(255, 255, 255);
      perspective-origin: 35px 35px;
      transform-origin: 35px 35px;
      user-select: text;
      caret-color: rgb(255, 255, 255);
      background: rgb(207, 35, 35) none repeat scroll 0 0 / auto padding-box
        border-box;
      border: 0 none rgb(255, 255, 255);
      list-style: none outside none;
      outline: rgb(255, 255, 255) none 0;
      font-size: 44px;
      display: flex;
      justify-content: center;
      flex-direction: column;
    }
  }
  .correct {
    .option-content {
      background-color: #299455;
      color: rgb(255, 255, 255) !important;
    }

    .option-letter {
      background-color: #299455 !important;
      color: rgb(255, 255, 255) !important;
    }
    .selected-option {
      color: rgb(255, 255, 255);
      cursor: pointer;
      float: left;
      text-align: center;
      text-decoration: none solid rgb(255, 255, 255);
      text-size-adjust: 100%;
      height: 70px;
      width: 70px;
      column-rule-color: rgb(255, 255, 255);
      perspective-origin: 35px 35px;
      transform-origin: 35px 35px;
      user-select: text;
      caret-color: rgb(255, 255, 255);
      background: rgb(41, 148, 85) none repeat scroll 0 0 / auto padding-box
        border-box;
      border: 0 none rgb(255, 255, 255);
      list-style: none outside none;
      outline: rgb(255, 255, 255) none 0;
      font-size: 44px;
      display: flex;
      justify-content: center;
      flex-direction: column;
    }
  }
}
</style>
