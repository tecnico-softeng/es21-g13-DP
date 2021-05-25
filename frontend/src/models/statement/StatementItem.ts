export default class StatementItem {
    optionId!: number;
    content!: string;
    sequence!: number;
    groupNumber!: number;

    constructor(jsonObj?: StatementItem) {
        if (jsonObj) {
            this.optionId = jsonObj.optionId;
            this.content = jsonObj.content;
            this.sequence = jsonObj.sequence;
            this.groupNumber = jsonObj.groupNumber;
        }
    }
}