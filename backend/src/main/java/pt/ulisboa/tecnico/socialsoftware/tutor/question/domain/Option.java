package pt.ulisboa.tecnico.socialsoftware.tutor.question.domain;

import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.MultipleChoiceAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.DomainEntity;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.Visitor;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.OptionDto;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.INVALID_CONTENT_FOR_OPTION;
import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.INVALID_SEQUENCE_FOR_OPTION;
import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.INVALID_RELEVANCE_FOR_OPTION;

@Entity
@Table(name = "options")
public class Option implements DomainEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private Integer sequence;

    @Column(columnDefinition = "Integer default 0", nullable = false)
    private Integer relevance;

    @Column(nullable = false)
    private boolean correct;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "question_details_id")
    private MultipleChoiceQuestion questionDetails;

    //@ManyToMany(cascade = CascadeType.ALL, mappedBy = "options", fetch = FetchType.LAZY, orphanRemoval = true)
    @ManyToMany(cascade = CascadeType.ALL, mappedBy = "options", fetch = FetchType.LAZY)
    private final Set<MultipleChoiceAnswer> questionAnswers = new HashSet<>();

    public Option() {
        this.relevance = 0;
    }

    public Option(OptionDto option) {
        setSequence(option.getSequence());
        setContent(option.getContent());
        setRelevance(option.getRelevance());
        setCorrect(option.isCorrect());

    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visitOption(this);
    }

    public Integer getId() {
        return id;
    }

    public Integer getSequence() {
        return sequence;
    }

    public void setSequence(Integer sequence) {
        if (sequence == null || sequence < 0)
            throw new TutorException(INVALID_SEQUENCE_FOR_OPTION);

        this.sequence = sequence;
    }

    public void setRelevance(Integer relevance) {
        if (relevance == null || relevance < 0)
            throw new TutorException(INVALID_RELEVANCE_FOR_OPTION);

        this.relevance = relevance;
    }

    public Integer getRelevance() {
        return relevance;
    }

    public boolean isCorrect() {
        return true;
    }

    public void setCorrect(boolean correct) {
        this.correct = correct;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        if (content == null || content.isBlank())
            throw new TutorException(INVALID_CONTENT_FOR_OPTION);

        this.content = content;
    }

    public MultipleChoiceQuestion getQuestionDetails() {
        return questionDetails;
    }

    public void setQuestionDetails(MultipleChoiceQuestion question) {
        this.questionDetails = question;
        question.addOption(this);
    }

    public Set<MultipleChoiceAnswer> getQuestionAnswers() {
        return questionAnswers;
    }

    public void addQuestionAnswer(MultipleChoiceAnswer questionAnswer) {
        questionAnswers.add(questionAnswer);
    }

    @Override
    public String toString() {
        return "Option{" +
                "id=" + id +
                ", sequence=" + sequence +
                ", correct=" + this.isCorrect() +
                ", relevance=" + relevance +
                ", content='" + content + '\'' +
                ", question=" + questionDetails.getId() +
                ", questionAnswers=" + questionAnswers +
                '}';
    }

    public void remove() {
        this.questionDetails = null;
        this.questionAnswers.clear();
    }


}
