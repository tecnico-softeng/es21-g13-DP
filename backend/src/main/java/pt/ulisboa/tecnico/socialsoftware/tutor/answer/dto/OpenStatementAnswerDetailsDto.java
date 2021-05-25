package pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto;

import org.apache.http.annotation.Obsolete;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.*;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.OpenQuestion;

import javax.persistence.Transient;

public class OpenStatementAnswerDetailsDto extends StatementAnswerDetailsDto {
    private String answer;

    public OpenStatementAnswerDetailsDto(){
    }

    public OpenStatementAnswerDetailsDto(OpenAnswer questionAnswer) {
        answer = questionAnswer.getAnswer();
    }

    public String getAnswer() { return answer; }

    public void setAnswer(String answer) {
        this.answer = answer;
    }



    @Transient
    private OpenAnswer createdOpenAnswer;

    @Override
    public AnswerDetails getAnswerDetails(QuestionAnswer questionAnswer) {
        createdOpenAnswer = new OpenAnswer(questionAnswer);
        questionAnswer.getQuestion().getQuestionDetails().update(this);
        return createdOpenAnswer;
    }

    @Override
    public QuestionAnswerItem getQuestionAnswerItem(String username, int quizId, StatementAnswerDto statementAnswerDto) {
        return new OpenAnswerItem(username, quizId, statementAnswerDto, this);
    }

    @Override
    public boolean emptyAnswer() { return answer == null || !answer.matches(".*\\w.*"); }

    @Override
    public void update(OpenQuestion question) { createdOpenAnswer.setAnswer(this); }

    @Override
    public String toString() {
        return "OpenStatementAnswerDetailsDto{" +
                "answer=" + answer +
                '}';
    }
}
