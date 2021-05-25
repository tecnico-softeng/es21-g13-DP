package pt.ulisboa.tecnico.socialsoftware.tutor.question.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Item;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.ItemCombinationQuestion;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.QuestionDetails;

import java.util.*;
import java.util.stream.Collectors;


public class ItemCombinationQuestionDto extends QuestionDetailsDto{
    private Set<ItemDto> firstGroup = new LinkedHashSet<>();
    private Set<ItemDto> secondGroup = new LinkedHashSet<>();

    public Set<ItemDto> getFirstGroup() {
        sortGroups(1);
        return firstGroup;
    }
    public Set<ItemDto> getSecondGroup() {
        sortGroups(2);
        return secondGroup;
    }



    public ItemCombinationQuestionDto() {
    }

    public ItemCombinationQuestionDto(ItemCombinationQuestion question){
        this.firstGroup = question.getGroup(1).stream().map(ItemDto::new).collect(Collectors.toSet());
        Set<ItemDto> set = new LinkedHashSet<>();
        for (Item item : question.getGroup(2)) {
            ItemDto itemDto = new ItemDto(item);
            set.add(itemDto);
        }
        this.secondGroup = set;
    }

    public Set<ItemDto> getGroup(Integer group) {
        if(group.equals(1)){
            return firstGroup;
        }
        else if (group.equals(2)) {
            return secondGroup;
        }
        return new LinkedHashSet<>();
    }

    public void setFirstGroup(Set<ItemDto> firstGroup) { this.firstGroup = firstGroup; }

    public void setSecondGroup(Set<ItemDto> secondGroup) { this.secondGroup = secondGroup; }


    @Override
    public QuestionDetails getQuestionDetails(Question question) {
        return new ItemCombinationQuestion(question, this);
    }
    @Override
    public void update(ItemCombinationQuestion question) {
        question.update(this);
    }

    @Override
    public String toString() {
        return "ItemCombinationQuestionDto{" +
                "firstGroup=" + getGroup(1) +
                "secondGroup=" + getGroup(2) +
                '}';
    }

    public void sortGroups(int group){
        List<Integer> iDList = new ArrayList<Integer>();
        Set<ItemDto> newGroup = new LinkedHashSet<>();
        if (group == 1) {
            for (ItemDto item : firstGroup) {
                if (item.getId() == null) {
                    return;
                }
                iDList.add(item.getId());
                Collections.sort(iDList);
            }
            for (Integer id : iDList)
                for (ItemDto item : firstGroup)
                    if (item.getId().equals(id))
                        newGroup.add(item);
            firstGroup = newGroup;
        } else {
            for (ItemDto item : secondGroup){
                if (item.getId() == null){
                    return;
                }
                iDList.add(item.getId());
                Collections.sort(iDList);
            }
            for (Integer id : iDList)
                for (ItemDto item : secondGroup)
                    if (item.getId().equals(id))
                        newGroup.add(item);
            secondGroup = newGroup;
        }
    }
}


