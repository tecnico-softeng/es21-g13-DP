package pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.AnswerDetails;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.MultipleChoiceAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuestionAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.MultipleChoiceQuestion;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.MultipleChoiceAnswerItem;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuestionAnswerItem;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Option;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.Transient;

import javax.persistence.Transient;

import javax.persistence.Transient;

public class MultipleChoiceStatementAnswerDetailsDto extends StatementAnswerDetailsDto {
    private List<Integer> optionsId;

    public MultipleChoiceStatementAnswerDetailsDto() {
        this.optionsId = new ArrayList<>();
    }

    public MultipleChoiceStatementAnswerDetailsDto(MultipleChoiceAnswer questionAnswer) {
        if (!questionAnswer.getOptions().isEmpty()) {
            this.optionsId = questionAnswer.getOptions()
                    .stream()
                    .map(Option::getId)
                    .collect(Collectors.toList());
        }
    }

    public List<Integer> getOptionsId() {
        return optionsId;
    }


    public void setOptionId(List<Integer> optionsId) {
        this.optionsId = optionsId;
    }

    //public void setOptionId(Integer optionsId) {
      //      this.optionsId.add(optionsId);
    //}

    @Transient
    private MultipleChoiceAnswer createdMultipleChoiceAnswer;

    @Override
    public AnswerDetails getAnswerDetails(QuestionAnswer questionAnswer) {
        createdMultipleChoiceAnswer = new MultipleChoiceAnswer(questionAnswer);
        questionAnswer.getQuestion().getQuestionDetails().update(this);
        return createdMultipleChoiceAnswer;
    }

    @Override
    public boolean emptyAnswer() {
        return optionsId == null;
    }

    @Override
    public QuestionAnswerItem getQuestionAnswerItem(String username, int quizId, StatementAnswerDto statementAnswerDto) {
        return new MultipleChoiceAnswerItem(username, quizId, statementAnswerDto, this);
    }

    @Override
    public void update(MultipleChoiceQuestion question) {
        createdMultipleChoiceAnswer.setOptions(question, this);
    }

    @Override
    public String toString() {
        return "MultipleChoiceStatementAnswerDto{" +
                "optionId=" + optionsId +
                '}';
    }
}
