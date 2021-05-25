import StatementAnswerDetails from '@/models/statement/questions/StatementAnswerDetails';
import { QuestionTypes } from '@/services/QuestionHelpers';
import MultipleChoiceStatementCorrectAnswerDetails from '@/models/statement/questions/MultipleChoiceStatementCorrectAnswerDetails';

export default class MultipleChoiceStatementAnswerDetails extends StatementAnswerDetails {
  public optionsId: number[] = [];

  constructor(jsonObj?: MultipleChoiceStatementAnswerDetails) {
    super(QuestionTypes.MultipleChoice);
    console.log(jsonObj)
    if (jsonObj) {
      this.optionsId = jsonObj.optionsId;
    }
  }

  isQuestionAnswered(): boolean {
    return this.optionsId.length > 0;
  }

  isAnswerCorrect(
    correctAnswerDetails: MultipleChoiceStatementCorrectAnswerDetails
  ): boolean {
    return (
      !!correctAnswerDetails &&
        JSON.stringify(this.optionsId)==JSON.stringify(correctAnswerDetails.correctOptionsId)
    );
  }
}
