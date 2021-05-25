package pt.ulisboa.tecnico.socialsoftware.tutor.question.domain;

import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.*;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.Visitor;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.Updator;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.ItemCombinationQuestionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.ItemDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDetailsDto;

import javax.persistence.*;
import java.util.*;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.*;

@Entity
@DiscriminatorValue(Question.QuestionTypes.ITEM_COMBINATION_QUESTION)
public class ItemCombinationQuestion extends QuestionDetails{

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "questionDetails", fetch = FetchType.EAGER, orphanRemoval = true)
    private Set<Item> group = new LinkedHashSet<>();

    public ItemCombinationQuestion(){
        super();
    }

    public ItemCombinationQuestion(Question question, ItemCombinationQuestionDto questionDto) {
        super(question);
        setGroupItems(questionDto.getFirstGroup(), questionDto.getSecondGroup());
        setLinks(questionDto);
    }
    @Override
    public CorrectAnswerDetailsDto getCorrectAnswerDetailsDto() {return new ItemCombinationCorrectAnswerDto(this);}

    @Override
    public StatementQuestionDetailsDto getStatementQuestionDetailsDto() {
        return new ItemCombinationStatementQuestionDetailsDto(this);
    }

    @Override
    public StatementAnswerDetailsDto getEmptyStatementAnswerDetailsDto() {
        return new ItemCombinationStatementAnswerDetailsDto();
    }

    @Override
    public AnswerDetailsDto getEmptyAnswerDetailsDto() {
        return new ItemCombinationAnswerDto();
    }




    public Set<Item> getGroup(int id) {
        Set<Item> groupId = new LinkedHashSet<>();
        for(Item item: group)
            if(item.getGroupNumber() == id)
                groupId.add(item);
        return groupId;
    }


    public void setGroupItems(Set<ItemDto> firstGroup, Set<ItemDto> secondGroup){
        for (Item item: this.group) {
            item.remove();
        }
        this.group.clear();

        int indexFirstGroup = 0;
        int indexSecondGroup = 0;
        Set<ItemDto> itemDtos = new LinkedHashSet<>();
        itemDtos.addAll(firstGroup);
        itemDtos.addAll(secondGroup);
        int group;
        for (ItemDto itemDto : itemDtos) {
            if (firstGroup.contains(itemDto)){
                group = 1;
                if (itemDto.getSequence() == null){
                    itemDto.setSequence(indexFirstGroup++);
                }
            }else{
                group = 2;
                if (itemDto.getSequence() == null){
                    itemDto.setSequence(indexSecondGroup++);
                }
            }
            new Item(itemDto).setQuestionDetails(this, group);
        }
    }

    public void setLinks(ItemCombinationQuestionDto questionDetails){
        for(ItemDto itemDto: questionDetails.getGroup(1)){
            for(Integer seqLink: itemDto.getItemSequenceSet()){
                Item item = getGroup(1).stream().filter(it -> it.getSequence().equals(itemDto.getSequence()))
                        .findAny().orElseThrow(() -> new TutorException(ITEM_NOT_FOUND));
                Item item2 = getGroup(2).stream().filter(it -> it.getSequence().equals(seqLink))
                        .findAny().orElseThrow(() -> new TutorException(ITEM_NOT_FOUND));
                item.addLink(item2);
                item2.addLink(item);
            }
        }
    }

    public void addItem(Item item){
        group.add(item);
    }

    @Override
    public QuestionDetailsDto getQuestionDetailsDto() {
        return new ItemCombinationQuestionDto(this);
    }

    public void update(ItemCombinationQuestionDto questionDetails) {
        setGroupItems(questionDetails.getFirstGroup(), questionDetails.getSecondGroup());
        setLinks(questionDetails);
    }

    @Override
    public void update(Updator updator) {
        updator.update(this);
    }

    @Override
    public void delete() {
        super.delete();
        for(Item item : this.group){
            item.remove();
        }
        this.group.clear();
    }

    @Override
    public String toString(){
        return "ItemCombinationQuestion{" +
                "fristGroup=" + getGroup(1) +
                "secondGroup=" + getGroup(2) +
                '}';
    }

    @Override
    public String getCorrectAnswerRepresentation() {
        StringBuilder latexText = new StringBuilder();
        latexText.append("\nGroup 1 $\\longleftrightarrow$ Group 2\n\n" );
        for(Item item: getGroup(1)){
                latexText.append("\\quad\\quad\\quad " + convertSequenceToLetter(item.getSequence()));
                for(Item link: item.getLinks())
                        latexText.append(" $\\longleftrightarrow$ " + convertSequenceToLetter(link.getSequence()) + "\n");
        }
        return latexText.toString();
    }

    public static String convertSequenceToLetter(Integer correctAnswer) {
        return correctAnswer != null ? Character.toString('A' + correctAnswer) : "-";
    }

    @Override
    public String getAnswerRepresentation(List<Integer> selectedIds) {
        StringBuilder latexText = new StringBuilder();
        for(Integer seq: selectedIds)
            latexText.append(" $\\longleftrightarrow$ " + convertSequenceToLetter(seq) + "\n");
        return latexText.toString();
    }


    @Override
    public void accept(Visitor visitor) {
        visitor.visitQuestionDetails(this);
    }

    public void visitItems(Visitor visitor) {
        for (Item item : this.getGroup(1)) {
            item.accept(visitor);
        }
        for (Item item : this.getGroup(2)) {
            item.accept(visitor);
        }
    }
}