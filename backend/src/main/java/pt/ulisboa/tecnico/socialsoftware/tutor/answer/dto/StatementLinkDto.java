package pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto;

import java.util.List;

public class StatementLinkDto {

    //Estes ints correspondem aos sequence numbers
    private StatementItemDto sourceItem;
    private List<StatementItemDto> linkedItems;

    public StatementLinkDto(){}

    public StatementLinkDto(StatementItemDto sourceItem, List<StatementItemDto> linkedItems){
        this.sourceItem = sourceItem;
        this.linkedItems = linkedItems;
    }

    public StatementItemDto getSourceItem() {
        return sourceItem;
    }

    public void setSourceItem(StatementItemDto sourceItem) {
        this.sourceItem = sourceItem;
    }


    public List<StatementItemDto> getLinkedItems() {
        return linkedItems;
    }

    public void setLinkedItems(List<StatementItemDto> linkedItems) {
        this.linkedItems = linkedItems;
    }
}
