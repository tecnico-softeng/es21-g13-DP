package pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.*;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Item;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.ItemCombinationQuestion;

import javax.persistence.Transient;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ItemCombinationStatementAnswerDetailsDto extends StatementAnswerDetailsDto {
    //esta linha está sujeita a alterações
    private List<StatementLinkDto> itemsId = new ArrayList<>();

    public ItemCombinationStatementAnswerDetailsDto(){
        itemsId = new ArrayList<>();
    }

    public ItemCombinationStatementAnswerDetailsDto(ItemCombinationAnswer answer){
        this.itemsId = new ArrayList<>();
        if (!answer.getAnswerItems().isEmpty()) {
            int counter = 0;
             for(Item item: answer.getAnswerItems()){
                StatementItemDto sourceItem = new StatementItemDto(item);
                List<StatementItemDto> links = new ArrayList<>(item.getLinks().stream().map(it -> new StatementItemDto(it)).collect(Collectors.toList()));
                StatementLinkDto link = new StatementLinkDto(sourceItem, links);
                this.itemsId.add(link);
            }
        }
    }

    public ItemCombinationStatementAnswerDetailsDto(List<StatementLinkDto> itemsId){
        this.itemsId = itemsId;
    }

    public List<StatementLinkDto> getItemsId(){return itemsId;}

    public void setItemsId(List<StatementLinkDto> itemsId) {this.itemsId = itemsId;}

    @Transient
    private ItemCombinationAnswer itemCombinationAnswer;

    @Override
    public AnswerDetails getAnswerDetails(QuestionAnswer questionAnswer){
        itemCombinationAnswer = new ItemCombinationAnswer(questionAnswer);
        questionAnswer.getQuestion().getQuestionDetails().update(this);
        return itemCombinationAnswer;
    }

    @Override
    public boolean emptyAnswer() {
        return itemsId == null;
    }

    @Override
    public QuestionAnswerItem getQuestionAnswerItem(String username, int quizId, StatementAnswerDto statementAnswerDto) {
        return new ItemCombinationAnswerItem(username, quizId, statementAnswerDto, this);
    }

    //VER COM O GUTAVO COMO É QUE ELE FEZ O INTEMANSWER
   /* @Override
    public void update(ItemCombinationQuestion question) {
        //itemCombinationAnswer.setItems(question, this);
        itemCombinationAnswer.setItems(this.itemsId);

    }*/

    @Override
    public void update(ItemCombinationQuestion question) {
        itemCombinationAnswer.setAnswerItems(question, this);
        itemCombinationAnswer.setCorrectItems(question, this);
    }


    @Override
    public String toString() {
        return "ItemCombinationStatementAnswerDto{" +
                "itemId=" + itemsId +
                '}';
    }


}
