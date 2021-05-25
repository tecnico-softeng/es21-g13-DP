package pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.MultipleChoiceQuestion;

import java.util.List;

public class MultipleChoiceCorrectAnswerDto extends CorrectAnswerDetailsDto {
    private List<Integer> correctOptionsId;

    public MultipleChoiceCorrectAnswerDto(MultipleChoiceQuestion question) {
        this.correctOptionsId = question.getCorrectOptionsId();
    }

    public List<Integer> getCorrectOptionsId() {
        return this.correctOptionsId;
    }

    public void setCorrectOptionsId(List<Integer> correctOptionsId) {
        this.correctOptionsId = correctOptionsId;
    }

    @Override
    public String toString() {
        return "MultipleChoiceCorrectAnswerDto{" +
                "correctOptionsId=" + correctOptionsId +
                '}';
    }
}