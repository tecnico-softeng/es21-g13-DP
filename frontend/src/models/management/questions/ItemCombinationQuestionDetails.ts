import Item from '@/models/management/Item';
import QuestionDetails from '@/models/management/questions/QuestionDetails';
import { QuestionTypes } from '@/services/QuestionHelpers';

export default class ItemCombinationQuestionDetails extends QuestionDetails {
    firstGroup: Item[] = [new Item()];
    secondGroup: Item[] = [new Item()];

    constructor(jsonObj?: ItemCombinationQuestionDetails) {
        super(QuestionTypes.ItemCombination);
        if (jsonObj) {
            this.firstGroup = jsonObj.firstGroup.map(
                (item: Item) => new Item(item)
            );
            this.firstGroup.sort((item1,item2) => {
                if (item1.sequence > item2.sequence) {
                    return 1;
                }
                if (item1.sequence < item2.sequence) {
                    return -1;
                }
                return 0;
            });
            this.secondGroup = jsonObj.secondGroup.map(
                (item: Item) => new Item(item)
            );
            this.secondGroup.sort((item1,item2) => {
                if (item1.sequence > item2.sequence) {
                    return 1;
                }
                if (item1.sequence < item2.sequence) {
                    return -1;
                }
                return 0;
            });
            if (this.firstGroup.length > 0){
                for (const item of this.firstGroup){
                    let links:string[] = [];
                    if (item.itemSequenceSet.length > 0){
                        for (const link of item.itemSequenceSet){
                            if (this.secondGroup.length > link){
                                links.push(this.secondGroup[link].content);
                            }
                        }
                    }
                    item.linked = links;
                }
            }

        }
    }

    setAsNew(): void {
        this.firstGroup.forEach((item) => {
            item.id = null;
        });
        this.secondGroup.forEach((item) => {
            item.id = null;
        });
    }

    getLinks(item: Item): any{
        this.firstGroup.forEach((itemAux) => {
            if(item.id == itemAux.id){
                return itemAux.itemSequenceSet;
            }
        });
    }
}