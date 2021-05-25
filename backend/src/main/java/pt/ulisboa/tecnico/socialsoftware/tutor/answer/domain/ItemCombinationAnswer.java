package pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain;


import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.*;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.Visitor;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Item;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.ItemCombinationQuestion;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question;

import javax.persistence.*;

import java.util.*;
import java.util.stream.Collectors;

@Entity
@DiscriminatorValue(Question.QuestionTypes.ITEM_COMBINATION_QUESTION)
public class ItemCombinationAnswer extends AnswerDetails{


    @ManyToMany(cascade=CascadeType.ALL)
    @JoinColumn(name = "items_id")
    private List<Item> correctItems;

    @ManyToMany(cascade=CascadeType.ALL)
    @JoinColumn(name = "answer_items_id")
    private List<Item> answerItems;



    public ItemCombinationAnswer() {super();}

    public ItemCombinationAnswer(QuestionAnswer questionAnswer){
        super(questionAnswer);
        correctItems = new ArrayList<>();
        answerItems = new ArrayList<>();
    }

    public ItemCombinationAnswer(QuestionAnswer questionAnswer, List<Item> correctItems){
        super(questionAnswer);
        this.setCorrectItems(correctItems);
    }

    public ItemCombinationAnswer(QuestionAnswer questionAnswer, List<Item> correctItems, List<Item> answerItems){
        super(questionAnswer);
        this.setCorrectItems(correctItems);
        this.setAnswerItems(answerItems);
    }

    public List<Item> getCorrectItems(){return this.correctItems;}

    public void setCorrectItems(List<Item> items){
        this.correctItems = new ArrayList<>(items);

        for(Item item: items){
            item.addQuestionAnswer(this);
        }
    }

    public List<Item> getAnswerItems(){return this.answerItems;}

    public void setAnswerItems(List<Item> items){
        this.answerItems = new ArrayList<>(items);

        for(Item item: items){
            item.addQuestionStudentAnswer(this);
        }
    }



    public void setAnswerItems(ItemCombinationQuestion question, ItemCombinationStatementAnswerDetailsDto answerDetailsDto) {
        if (!answerDetailsDto.getItemsId().isEmpty()) {
            for(Item item: this.getAnswerItems())
                item.getQuestionStudentAnswers().remove(this);
            this.answerItems.clear();
            this.answerItems = new ArrayList<>();
            List<Item> group2 = new ArrayList<>();
            for (Item item : question.getGroup(2)){
                group2.add(new Item(item.getSequence(), item.getContent()));
            }
            for (StatementLinkDto linksDto : answerDetailsDto.getItemsId()){
                Item sourceItem = new Item(linksDto.getSourceItem().getSequence(), linksDto.getSourceItem().getContent());
                sourceItem.setGroupNumber(1);
                this.answerItems.add(sourceItem);
                for (StatementItemDto linkedItem : linksDto.getLinkedItems()){
                    Item originItem = group2.stream().filter(it -> it.getSequence() == linkedItem.getSequence())
                            .collect(Collectors.toList()).stream().findAny().orElse(null);
                    originItem.setGroupNumber(2);
                    sourceItem.addLink(originItem);
                    sourceItem.addQuestionStudentAnswer(this);
                }
            }
        }else
            this.setAnswerItems(Collections.emptyList());
    }

    public void setCorrectItems(ItemCombinationQuestion question, ItemCombinationStatementAnswerDetailsDto answerDetailsDto){
        if (!answerDetailsDto.getItemsId().isEmpty()){
            for(Item item: this.getCorrectItems())
                item.getQuestionStudentAnswers().remove(this);
            this.correctItems.clear();
            this.correctItems = new ArrayList<>();
            for (StatementLinkDto linksDto : answerDetailsDto.getItemsId()){
                Item sourceItem = question.getGroup(1).stream()
                        .filter(it -> it.getSequence() == linksDto.getSourceItem().getSequence()).findAny().orElse(null);
                if (sourceItem != null){
                    this.correctItems.add(sourceItem);
                    sourceItem.addQuestionAnswer(this);
                }
            }
        }
    }

    public void remove(){
        for (Item item : this.correctItems){
            item.getQuestionAnswers().remove(this);
        }
        for (Item item : this.answerItems){
            item.getQuestionStudentAnswers().remove(this);
        }
        correctItems.clear();
        answerItems.clear();
    }

    public boolean isCorrect() {
        if(correctItems.isEmpty() && this.answerItems.isEmpty())
            return true;
        else if(!(correctItems.isEmpty() || this.answerItems.isEmpty()))
            for(Item item: correctItems){
                Item answerItem = this.answerItems.stream().filter(it -> it.getSequence() == item.getSequence()).findAny().orElse(null);
                if (answerItem == null){
                    return false;
                }
                List<Integer> correctLinks = item.getLinks().stream().map(it -> it.getSequence()).collect(Collectors.toList());
                List<Integer> answerLinks = answerItem.getLinks().stream().map(it -> it.getSequence()).collect(Collectors.toList());
                Collections.sort(correctLinks);
                Collections.sort(answerLinks);
                if (!correctLinks.equals(answerLinks)){
                    return false;
                }
            }
        else
            return false;
        return true;
    }

    @Override
    public AnswerDetailsDto getAnswerDetailsDto() {
        return new ItemCombinationAnswerDto(this);
    }

    @Override
    public String getAnswerRepresentation() {
        StringBuilder latexText = new StringBuilder();
        latexText.append("\nGroup 1 $\\longleftrightarrow$ Group 2\n\n" );
        for(Item item: answerItems){
            latexText.append("\\quad\\quad\\quad " + convertSequenceToLetter(item.getSequence()));
            for(Integer link: item.getLinks().stream().map(it -> it.getSequence()).collect(Collectors.toList())){
                     latexText.append(" $\\longleftrightarrow$ " + convertSequenceToLetter(link) + "\n");
            }
        }
        return latexText.toString();
    }

    public static String convertSequenceToLetter(Integer answer) {
        return answer != null ? Character.toString('A' + answer) : "-";
    }

    @Override
    public boolean isAnswered() {
        return !answerItems.isEmpty();
    }

    @Override
    public StatementAnswerDetailsDto getStatementAnswerDetailsDto() {
        return new ItemCombinationStatementAnswerDetailsDto(this);
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visitAnswerDetails(this);
    }
}

