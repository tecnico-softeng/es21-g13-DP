package pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain;

import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.*;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.Visitor;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.MultipleChoiceQuestion;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Option;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question;

import javax.persistence.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.QUESTION_OPTION_MISMATCH;

@Entity
@DiscriminatorValue(Question.QuestionTypes.MULTIPLE_CHOICE_QUESTION)
public class MultipleChoiceAnswer extends AnswerDetails {
    @ManyToMany
    @JoinColumn(name = "option_id")
    private List<Option> options;

    public MultipleChoiceAnswer() {
        super();
    }

    public MultipleChoiceAnswer(QuestionAnswer questionAnswer){
        super(questionAnswer);
        options = new ArrayList<>();
    }

    public MultipleChoiceAnswer(QuestionAnswer questionAnswer, Option option){
        super(questionAnswer);
        options = new ArrayList<>();
        options.add(option);
    }

    public MultipleChoiceAnswer(QuestionAnswer questionAnswer, List<Option> options){
        super(questionAnswer);
        this.setOptions(options);
    }

    public List<Option> getOptions() {
        return options;
    }

    public void setOptions(List<Option> options) {
        this.options = options;

        for (Option option : options){
            option.addQuestionAnswer(this);
        }
    }

    public void setOptions(MultipleChoiceQuestion question, MultipleChoiceStatementAnswerDetailsDto multipleChoiceStatementAnswerDetailsDto) {
        if (!multipleChoiceStatementAnswerDetailsDto.getOptionsId().isEmpty()) {

            List<Option> options = multipleChoiceStatementAnswerDetailsDto.getOptionsId().stream()
                    .map(option1 -> question.getOptions().stream()
                            .filter(option2 -> option2.getId().equals(option1))
                            .findAny()
                            .orElseThrow(() -> new TutorException(QUESTION_OPTION_MISMATCH, option1)))
                    .collect(Collectors.toList());

            for (Option option : this.getOptions()){
                option.getQuestionAnswers().remove(this);
            }

            this.setOptions(options);
        } else {
            this.setOptions(Collections.emptyList());
        }
    }

    @Override
    public boolean isCorrect() {
        return !getOptions().isEmpty() && getOptions().stream().allMatch(x -> x.isCorrect());
    }


    public void remove() {
        for (Option option : options){
            option.getQuestionAnswers().remove(this);
        }
        options.clear();
    }

    @Override
    public AnswerDetailsDto getAnswerDetailsDto() {
        return new MultipleChoiceAnswerDto(this);
    }

    @Override
    public boolean isAnswered() {
        return !getOptions().isEmpty();
    }

    @Override
    public String getAnswerRepresentation() {
        var result = this.getOptions()
                .stream()
                .map(x -> MultipleChoiceQuestion.convertSequenceToLetter(x.getSequence()))
                .collect(Collectors.joining("|"));

        return !result.isEmpty() ? result : "-";
    }

    @Override
    public StatementAnswerDetailsDto getStatementAnswerDetailsDto() {
        return new MultipleChoiceStatementAnswerDetailsDto(this);
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visitAnswerDetails(this);
    }
}
