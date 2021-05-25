import StatementQuestionDetails from '@/models/statement/questions/StatementQuestionDetails';
import { QuestionTypes } from '@/services/QuestionHelpers';

export default class OpenQuestionStatementQuestionDetails extends StatementQuestionDetails {
    answer: string = '';

    constructor(jsonObj?: OpenQuestionStatementQuestionDetails) {
        super(QuestionTypes.OpenAnswer);
        if (jsonObj) {
            this.answer = jsonObj.answer || this.answer;
        }
    }
}