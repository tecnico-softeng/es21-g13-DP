import AnswerDetails from '@/models/management/questions/AnswerDetails';
import {QuestionTypes} from "@/services/QuestionHelpers";

export default class OpenQuestionAnswerType extends AnswerDetails {

    answer: string = '';

    constructor(jsonObj?: OpenQuestionAnswerType) {
        super(QuestionTypes.OpenAnswer);
        if (jsonObj) {
            this.answer = jsonObj.answer || this.answer;
        }
    }
    isCorrect(): boolean {
        return false;
    }
    answerRepresentation(): string {
        return '';
    }
}