import StatementQuestionDetails from '@/models/statement/questions/StatementQuestionDetails';
import { QuestionTypes } from '@/services/QuestionHelpers';
import { _ } from 'vue-underscore';
import StatementLink from '@/models/statement/StatementLink';
import StatementAnswerDetails from '@/models/statement/questions/StatementAnswerDetails';

import ItemCombinationStatementCorrectAnswerDetails
    from '@/models/statement/questions/ItemCombinationStatementCorrectAnswerDetails';

export default class ItemCombinationStatementAnswerDetails extends StatementAnswerDetails {
    itemsId: StatementLink[] = [];
    links: string[][] = [];

    constructor(jsonObj?: ItemCombinationStatementAnswerDetails) {
        super(QuestionTypes.ItemCombination);
        if (jsonObj) {
            if (jsonObj.itemsId) {
                this.itemsId = jsonObj.itemsId.map(
                    (item: StatementLink) => new StatementLink(item)
                );
            }
        }
    }

    isAnswerCorrect(correctAnswerDetails: ItemCombinationStatementCorrectAnswerDetails): boolean {
        let counter = 0;
        for (const sequenceLinks of correctAnswerDetails.correctLinksSequence){
            const linkList = this.itemsId.filter(statementLink => statementLink.sourceItem.sequence.valueOf() === counter);
            if (linkList.length == 0 && sequenceLinks.length !== 0){
                return false;
            } else if (linkList.length !== 0){
                const link = linkList[0];
                counter++;
                const itemLinks = link.linkedItems.map(it => it.sequence);
                for (const sequenceLink of sequenceLinks){
                    if (!itemLinks.includes(sequenceLink)){
                        return false;
                    }
                }
            }
        }
        return true;
    }

    isQuestionAnswered(): boolean {
        return !(this.itemsId === []);
    }
}