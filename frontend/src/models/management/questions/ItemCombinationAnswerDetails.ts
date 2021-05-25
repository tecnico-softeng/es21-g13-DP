import Option from '@/models/management/Option';
import AnswerDetails from '@/models/management/questions/AnswerDetails';
import { QuestionTypes, convertToLetter } from '@/services/QuestionHelpers';
import Item from "@/models/management/Item";
import StatementLink from "@/models/statement/StatementLink";

export default class ItemCombinationAnswerDetails extends AnswerDetails {
    answerItems : Item[] = [];
    correctItems : Item[] = [];


    constructor(jsonObj?: ItemCombinationAnswerDetails) {
        super(QuestionTypes.ItemCombination);
        if (jsonObj) {
            if (jsonObj.answerItems) {
                this.answerItems = jsonObj.answerItems.map(
                    (item: Item) => new Item(item)
                );
            }
            if (jsonObj.correctItems) {
                this.correctItems = jsonObj.correctItems.map(
                    (item: Item) => new Item(item)
                );
            }
        }
    }

    isCorrect(): boolean {
        if(this.correctItems.length === 0 && this.answerItems.length === 0)
            return true;
        else if(!(this.correctItems.length === 0 || this.answerItems.length === 0)) {
            for (const item of this.correctItems){
                const answerItems = this.answerItems.filter(it => it.sequence === item.sequence);
                let answerItem = null;
                if ( answerItems.length === 0){
                    answerItem = answerItems[0];
                }
                if (answerItem === null) {
                    return false;
                }
                const correctLinks = item.itemSequenceSet.sort();
                const answerLinks = answerItem.itemSequenceSet.sort();
                if (!(correctLinks === answerLinks)) {
                    return false;
                }
            }
        }else
            return false;
        return true;
    }

    answerRepresentation(): string {
        return this.answerItems.map(it => it.itemSequenceSet).toString();
    }
}