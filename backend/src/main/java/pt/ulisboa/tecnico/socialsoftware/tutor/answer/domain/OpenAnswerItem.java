package pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain;

import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.OpenStatementAnswerDetailsDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.StatementAnswerDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.QuestionDetails;


import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue(Question.QuestionTypes.OPEN_ANSWER_QUESTION)
public class OpenAnswerItem extends QuestionAnswerItem {

    @Column(columnDefinition = "TEXT")
    String answer;

    public OpenAnswerItem() {
    }

    public OpenAnswerItem(String username, int quizId, StatementAnswerDto answer, OpenStatementAnswerDetailsDto detailsDto) {
        super(username, quizId, answer);
        this.answer = detailsDto.getAnswer();
    }

    @Override
    public  String getAnswerRepresentation(QuestionDetails questionDetails) {
        return null;
        //return this.getAnswer() != null ? questionDetails.getAnswerRepresentation(answer) : "-";
    }

    public String getAnswer() { return answer; }

    public void setAnswer(String answer) { this.answer = answer; }
}
