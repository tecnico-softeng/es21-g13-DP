package pt.ulisboa.tecnico.socialsoftware.tutor.question.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.OpenQuestion;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.QuestionDetails;


public class OpenQuestionDto extends QuestionDetailsDto{

    private String answer;

    public OpenQuestionDto() {
    }

    public OpenQuestionDto(OpenQuestion question) {
        this.answer = question.getAnswer();
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getAnswer() {
        return answer;
    }

    @Override
    public QuestionDetails getQuestionDetails(Question question) {
        return new OpenQuestion(question , this);
    }

    @Override
    public void update(OpenQuestion question) {
        question.update(this);
    }

    @Override
    public String toString() {
        return "OpenQuestionDto{" +
                "answer=" + answer +
                '}';
    }
}
