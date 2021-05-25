package pt.ulisboa.tecnico.socialsoftware.tutor.question.domain;

import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.*;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.EMPTY_ANSWER;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.Visitor;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.Updator;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.OpenQuestionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDetailsDto;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import java.util.List;

@Entity
@DiscriminatorValue(Question.QuestionTypes.OPEN_ANSWER_QUESTION)
public class OpenQuestion extends QuestionDetails {

    @Column(columnDefinition = "TEXT")
    private String answer;

    public OpenQuestion() {
        super();
    }

    public OpenQuestion(Question question, OpenQuestionDto openQuestionDto) {
        super(question);
        setAnswer(openQuestionDto.getAnswer());
    }

    public void setAnswer(String answer) {
        if (answer == null || !answer.matches(".*\\w.*"))
            throw new TutorException(EMPTY_ANSWER);
        else
            this.answer = answer;
    }

    public String getAnswer() { return ""; }

    @Override
    public CorrectAnswerDetailsDto getCorrectAnswerDetailsDto() {
        return new OpenCorrectAnswerDto(this);
    }

    @Override
    public StatementQuestionDetailsDto getStatementQuestionDetailsDto() {
        return new OpenStatementQuestionDetailsDto(this);
    }

    @Override
    public StatementAnswerDetailsDto getEmptyStatementAnswerDetailsDto() {
        return new OpenStatementAnswerDetailsDto();
    }

    @Override
    public AnswerDetailsDto getEmptyAnswerDetailsDto() {
        return new OpenAnswerDto();
    }

    @Override
    public QuestionDetailsDto getQuestionDetailsDto() {
        return new OpenQuestionDto(this);
    }

    @Override
    public void delete() {
        super.delete();
        this.answer = null;
    }

    @Override
    public String getCorrectAnswerRepresentation() {
        return getAnswer();
    }

    public void update(OpenQuestionDto questionDetails) {
        setAnswer(questionDetails.getAnswer());
    }

    @Override
    public void update(Updator updator) {
        updator.update(this);
    }

    public void accept(Visitor visitor) {
        visitor.visitQuestionDetails(this);
    }

    @Override
    public String getAnswerRepresentation(List<Integer> selectedIds) {
        return null;
    }

}
