import StatementAnswerDetails from '@/models/statement/questions/StatementAnswerDetails';
import { QuestionTypes } from '@/services/QuestionHelpers';
import OpenQuestionStatementCorrectAnswerDetails from '@/models/statement/questions/OpenQuestionStatementCorrectAnswerDetails';

export default class OpenQuestionStatementAnswerDetails extends StatementAnswerDetails {
    public answer: string | null = null;

    constructor(jsonObj?: OpenQuestionStatementAnswerDetails) {
        super(QuestionTypes.OpenAnswer);
        if (jsonObj) {
            this.answer = jsonObj.answer;
        }
    }

    isQuestionAnswered(): boolean {
        return this.answer != null;
    }

    isAnswerCorrect(
        correctAnswerDetails: OpenQuestionStatementCorrectAnswerDetails
    ): boolean {
        if (correctAnswerDetails.answer === this.answer)
            return true;
        return false;
    }
}