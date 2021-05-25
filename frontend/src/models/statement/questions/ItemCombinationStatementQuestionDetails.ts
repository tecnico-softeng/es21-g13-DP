import StatementQuestionDetails from '@/models/statement/questions/StatementQuestionDetails';
import { QuestionTypes } from '@/services/QuestionHelpers';
import StatementItem from '@/models/statement/StatementItem';
import { _ } from 'vue-underscore';

export default class ItemCombinationStatementQuestionDetails extends StatementQuestionDetails {
    itemsGroup1: StatementItem[] = [];
    itemsGroup2: StatementItem[] = [];

    constructor(jsonObj?: ItemCombinationStatementQuestionDetails) {
        super(QuestionTypes.ItemCombination);
        if (jsonObj) {
            if (jsonObj.itemsGroup1) {
                this.itemsGroup1 = jsonObj.itemsGroup1.map(
                    (item: StatementItem) => new StatementItem(item)
                );
            }
            if (jsonObj.itemsGroup2) {
                this.itemsGroup2 = jsonObj.itemsGroup2.map(
                    (item: StatementItem) => new StatementItem(item)
                );
            }
        }
    }
}