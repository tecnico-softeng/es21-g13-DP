export default class Item {
    id: number | null = null;
    sequence!: number;
    content: string = '';
    itemSequenceSet: number[] = [];
    linked: string[] = [];


    constructor(jsonObj?: Item) {
        if (jsonObj) {
            this.id = jsonObj.id;
            this.sequence = jsonObj.sequence;
            this.content = jsonObj.content;
            this.itemSequenceSet = jsonObj.itemSequenceSet;
            this.linked = jsonObj.linked;
        }
    }

    setLinks(items:number[]){
        this.itemSequenceSet = items;
    }
}