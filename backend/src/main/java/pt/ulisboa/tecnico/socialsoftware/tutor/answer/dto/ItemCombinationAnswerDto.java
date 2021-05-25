package pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.ItemCombinationAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.MultipleChoiceAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Item;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Option;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.ItemDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.OptionDto;

import java.util.List;

public class ItemCombinationAnswerDto extends AnswerDetailsDto{
    private List<ItemDto> correctItems;
    private List<ItemDto> answerItems;

    public ItemCombinationAnswerDto(){}

    public ItemCombinationAnswerDto(ItemCombinationAnswer answer) {
        for (Item item : answer.getAnswerItems()){
            this.answerItems.add(new ItemDto(item));
        }
        for (Item item : answer.getCorrectItems()){
            this.correctItems.add(new ItemDto(item));
        }
    }


    public List<ItemDto> getAnswerItems() {
        return answerItems;
    }

    public void setAnswerItems(List<ItemDto> answerItems) {
        this.answerItems = answerItems;
    }

    public List<ItemDto> getCorrectItems() {
        return correctItems;
    }

    public void setCorrectItems(List<ItemDto> correctItems) {
        this.correctItems = correctItems;
    }

}
