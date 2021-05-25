package pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.OpenQuestion;

public class OpenStatementQuestionDetailsDto extends StatementQuestionDetailsDto {
    private String answer;

    public OpenStatementQuestionDetailsDto(OpenQuestion question) {
        this.answer = question.getAnswer();
    }

    public String getAnswer() { return answer; }

    public  void setAnswer(String answer) { this.answer = answer; }

    @Override
    public String toString() {
        return "OpenStatementQuestionDetailsDto{" +
                "answer=" + answer +
                '}';
    }
}
