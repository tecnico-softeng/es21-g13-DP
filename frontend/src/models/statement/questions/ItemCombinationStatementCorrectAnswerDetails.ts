import StatementQuestionDetails from '@/models/statement/questions/StatementQuestionDetails';
import { QuestionTypes } from '@/services/QuestionHelpers';
import StatementCorrectAnswerDetails from "@/models/statement/questions/StatementCorrectAnswerDetails";

export default class ItemCombinationStatementCorrectAnswerDetails extends StatementCorrectAnswerDetails {
    correctLinksSequence: number[][] = [];

    constructor(jsonObj?: ItemCombinationStatementCorrectAnswerDetails) {
        super(QuestionTypes.ItemCombination);
        if (jsonObj) {
            if (jsonObj.correctLinksSequence) {
                this.correctLinksSequence = jsonObj.correctLinksSequence;
            }
        }
    }
}