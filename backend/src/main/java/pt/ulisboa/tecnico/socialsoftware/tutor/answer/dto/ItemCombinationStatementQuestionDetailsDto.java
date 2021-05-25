package pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Item;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.ItemCombinationQuestion;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ItemCombinationStatementQuestionDetailsDto extends StatementQuestionDetailsDto{
    private List<StatementItemDto> itemsGroup1;
    private List<StatementItemDto> itemsGroup2;


    public ItemCombinationStatementQuestionDetailsDto(){}

    public ItemCombinationStatementQuestionDetailsDto(ItemCombinationQuestion question) {
        List<Item> allItemsGroup1 = new ArrayList<>(question.getGroup(1));

        List<Item> allItemsGroup2 = new ArrayList<>(question.getGroup(2));


        this.itemsGroup1 = allItemsGroup1.stream()
                .map(StatementItemDto::new)
                .collect(Collectors.toList());

        this.itemsGroup2 = allItemsGroup2.stream()
                .map(StatementItemDto::new)
                .collect(Collectors.toList());
    }

    public List<StatementItemDto> getItems(int id) {
        List<StatementItemDto> groupId = new ArrayList<>();
        if(id == 1)
            for(StatementItemDto item: itemsGroup1)
                    groupId.add(item);
        else
            for(StatementItemDto item: itemsGroup2)
                groupId.add(item);
        return groupId;
    }

    public List<StatementItemDto> getItemsGroup1() {
        return itemsGroup1;
    }

    public void setItemsGroup1(List<StatementItemDto> itemsGroup1) {
        this.itemsGroup1 = itemsGroup1;
    }


    public List<StatementItemDto> getItemsGroup2() {
        return itemsGroup2;
    }

    public void setItemsGroup2(List<StatementItemDto> itemsGroup2) {
        this.itemsGroup2 = itemsGroup2;
    }
}
