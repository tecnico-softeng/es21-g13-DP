import QuestionDetails from '@/models/management/questions/QuestionDetails';
import { QuestionTypes } from '@/services/QuestionHelpers';

export default class OpenQuestionDetails extends QuestionDetails {
    answer: string = '';

    constructor(jsonObj?: OpenQuestionDetails) {
        super(QuestionTypes.OpenAnswer);
        if (jsonObj) {
            this.answer = jsonObj.answer || this.answer;
        }
    }

    setAsNew() : void {}
}