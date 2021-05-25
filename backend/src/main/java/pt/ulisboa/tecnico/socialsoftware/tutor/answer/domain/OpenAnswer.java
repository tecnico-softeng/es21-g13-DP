package pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain;

import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.AnswerDetailsDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.OpenAnswerDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.OpenStatementAnswerDetailsDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.StatementAnswerDetailsDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.Visitor;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.OpenQuestion;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.INVALID_GRADE;

@Entity
@DiscriminatorValue(Question.QuestionTypes.OPEN_ANSWER_QUESTION)
public class OpenAnswer extends AnswerDetails {

    @Column(columnDefinition = "TEXT")
    private String answer;
    private Integer grade;

    public OpenAnswer() { super(); }

    public OpenAnswer(QuestionAnswer questionAnswer){
        super(questionAnswer);
    }

    public OpenAnswer(QuestionAnswer questionAnswer, String answerText){
        super(questionAnswer);
        answer = answerText;
    }

    public void setAnswer(OpenStatementAnswerDetailsDto openStatementAnswerDetailsDto) {
        this.answer = null;
        if (!openStatementAnswerDetailsDto.emptyAnswer())
            this.answer = openStatementAnswerDetailsDto.getAnswer();
    }

    public String getAnswer() {
        return answer;
    }

    public void setGrade(int value) {
        if (value < 0 || value > 1) throw new TutorException(INVALID_GRADE);
        else this.grade = value;
    }

    public int getGrade() { return grade; }

    @Override
    public boolean isCorrect() { return grade > 0.5; }

    @Override
    public void remove() {
        if (answer != null) {
            answer = null;
        }
        if (grade != null) {
            grade = null;
        }
    }

    @Override
    public AnswerDetailsDto getAnswerDetailsDto() {
        return new OpenAnswerDto(this);
    }

    @Override
    public boolean isAnswered() { return answer == null; }

    @Override
    public String getAnswerRepresentation() {
        return getAnswer();
    }

    @Override
    public StatementAnswerDetailsDto getStatementAnswerDetailsDto() {
        return new OpenStatementAnswerDetailsDto(this);
    }

    @Override
    public void accept(Visitor visitor) {
        //visitor.visitAnswerDetails(this);
    }
}
