import StatementCorrectAnswerDetails from '@/models/statement/questions/StatementCorrectAnswerDetails';
import { QuestionTypes } from '@/services/QuestionHelpers';
import OpenQuestionStatementQuestionDetails from "@/models/statement/questions/OpenQuestionStatementQuestionDetails";

export default class OpenQuestionStatementCorrectAnswerDetails extends StatementCorrectAnswerDetails {
    answer: string = '';

    constructor(jsonObj?: OpenQuestionStatementQuestionDetails) {
        super(QuestionTypes.OpenAnswer);
        if (jsonObj) {
            this.answer = jsonObj.answer || this.answer;
        }
    }
}