package pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.OpenQuestion;

public class OpenCorrectAnswerDto extends CorrectAnswerDetailsDto {
    private String correctAnswer;

    public OpenCorrectAnswerDto(OpenQuestion question) {
        this.correctAnswer = question.getAnswer();
    }

    public String getCorrectAnswer() { return correctAnswer; }

    public void setCorrectAnswer(String correctAnswer) { this.correctAnswer = correctAnswer; }

    @Override
    public String toString() {
        return "OpenQuestionCorrectAnswerDto{" +
                "correctAnswer=" + correctAnswer +
                '}';
    }
}
