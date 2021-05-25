import Option from '@/models/management/Option';
import AnswerDetails from '@/models/management/questions/AnswerDetails';
import { QuestionTypes, convertToLetter } from '@/services/QuestionHelpers';

export default class MultipleChoiceAnswerType extends AnswerDetails {
  options!: Option[];

  constructor(jsonObj?: MultipleChoiceAnswerType) {
    super(QuestionTypes.MultipleChoice);
    console.log(jsonObj);
    if (jsonObj) {
      this.options = jsonObj.options.map(
        (option: Option) => new Option(option)
      );
    }
  }

  isCorrect(): boolean {
    return this.options.every(
      (elem, idx) => elem.correct && elem.relevance == idx + 1
    );
  }

  answerRepresentation(): string {
    return this.options
      .map(
        (elem, idx) =>
          convertToLetter(elem.sequence) + '(' + (idx + 1) + ')' + '|'
      )
      .join('')
      .slice(0, -1);
  }
}
