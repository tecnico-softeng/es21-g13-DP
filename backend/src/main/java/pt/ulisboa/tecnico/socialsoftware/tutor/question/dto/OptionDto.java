package pt.ulisboa.tecnico.socialsoftware.tutor.question.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.CodeFillInOption;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Option;

import java.io.Serializable;

public class OptionDto implements Serializable {
    private Integer id;
    private Integer sequence;
    private Integer relevance;
    private boolean correct;
    private String content;

    public OptionDto() {
        this.relevance = 0;
    }

    public OptionDto(Option option) {
        this.id = option.getId();
        this.sequence = option.getSequence();
        this.correct = option.isCorrect();
        this.content = option.getContent();
        this.relevance = option.getRelevance();
    }

    public OptionDto(CodeFillInOption option) {
        this.id = option.getId();
        this.sequence = option.getSequence();
        this.content = option.getContent();
        this.relevance = 0;
        this.correct = option.isCorrect();
    }

    public Integer getId() {
        return id;
    }

    public Integer getSequence() {
        return sequence;
    }

    public Integer getRelevance() { return relevance; }

    public void setRelevance(Integer relevance) { this.relevance = relevance; }

    public void setSequence(Integer sequence) {
        this.sequence = sequence;
    }

    public boolean isCorrect() {
        return correct;
    }

    public void setCorrect(boolean correct) {
        this.correct = correct;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "OptionDto{" +
                "id=" + id +
                ", correct=" + this.isCorrect() +
                ", relevance=" + relevance +
                ", content='" + content + '\'' +
                '}';
    }
}