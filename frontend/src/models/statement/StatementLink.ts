import StatementItem from "@/models/statement/StatementItem";

export default class StatementLink {
    sourceItem!: StatementItem;
    linkedItems: StatementItem[] = [];

    constructor(jsonObj?: StatementLink) {
        if (jsonObj) {
            this.sourceItem = jsonObj.sourceItem;
            this.linkedItems = jsonObj.linkedItems;
        }
    }
}