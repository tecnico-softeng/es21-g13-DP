package pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.CodeFillInOption;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Item;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Option;

import java.io.Serializable;

public class StatementItemDto implements Serializable {
    private Integer itemId;
    private String content;
    private Integer sequence;
    private Integer groupNumber;

    public StatementItemDto(){}

    public Integer getSequence() {
        return sequence;
    }

    public void setSequence(Integer sequence) {
        this.sequence = sequence;
    }

    public Integer getGroupNumber() {
        return groupNumber;
    }

    public void setGroupNumber(Integer groupNumber) {
        this.groupNumber = groupNumber;
    }

    public StatementItemDto(Item item) {
        this.sequence = item.getSequence();
        this.itemId = item.getId();
        this.content = item.getContent();
        this.groupNumber = item.getGroupNumber();
    }

    public Integer getItemId() {
        return itemId;
    }

    public void setItemId(Integer itemId) {
        this.itemId = itemId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "StatementOptionDto{" +
                "itemId=" + itemId +
                ", content='" + content + '\'' +
                '}';
    }
}
