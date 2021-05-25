package pt.ulisboa.tecnico.socialsoftware.tutor.question.domain;


import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.ItemCombinationAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.MultipleChoiceAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.DomainEntity;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.Visitor;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.ItemDto;

import javax.persistence.*;
import java.util.*;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.*;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.INVALID_CONTENT_FOR_OPTION;

@Entity
@Table(name = "items")
public class Item implements DomainEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private Integer sequence;

    @Column(nullable = false)
    private Integer groupNumber;

    @Column(nullable = true)
    @ManyToMany(mappedBy = "items", fetch = FetchType.EAGER)
    private Set<Item> links = new LinkedHashSet<>();

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "question_details_id")
    private ItemCombinationQuestion questionDetails;


    @ManyToMany
    @JoinColumn(name = "items_id")
    private final Set<Item> items = new LinkedHashSet<>();

    @ManyToMany(mappedBy = "correctItems", fetch = FetchType.EAGER)
    private Set<ItemCombinationAnswer> questionAnswers = new LinkedHashSet<>();

    @ManyToMany(mappedBy = "answerItems", fetch = FetchType.EAGER)
    private Set<ItemCombinationAnswer> questionStudentAnswers = new LinkedHashSet<>();

    public Item(){

    }

    public Item(ItemDto item){
        setSequence(item.getSequence());
        setContent(item.getContent());
    }

    public Item(Integer sequence, String content){
        setSequence(sequence);
        setContent(content);
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visitItem(this);
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer iD) {this.id = iD;}

    public Integer getSequence() {
        return sequence;
    }

    public void setSequence(Integer sequence){
        if (sequence == null || sequence < 0)
            throw new TutorException(INVALID_SEQUENCE_FOR_ITEM);
        this.sequence = sequence;
    }

    public Integer getGroupNumber(){return groupNumber;}

    public void setGroupNumber(Integer groupNumber){this.groupNumber = groupNumber;}

    public String getContent() {
        return "";
    }

    public void setContent(String content) {
        if (content == null || content.isBlank())
            throw new TutorException(INVALID_CONTENT_FOR_OPTION);

        this.content = content;
    }

    public Set<Item> getLinks() {
        return links;
    }


    public void addLink(Item item){
        this.links.add(item);
        item.items.add(this);
    }

    public void setLinks(Set<Item> links){ this.links = links;}

    public void deleteLink(Item item){
        this.links.remove(item);
        item.items.remove(this);
    }


    public ItemCombinationQuestion getQuestionDetails() {
        return questionDetails;
    }

    public void setQuestionDetails(ItemCombinationQuestion question, Integer group) {
        this.questionDetails = question;
        this.setGroupNumber(group);
        question.addItem(this);
    }

    public void addQuestionAnswer(ItemCombinationAnswer questionAnswer) {
        questionAnswers.add(questionAnswer);
    }

    public Set<ItemCombinationAnswer> getQuestionAnswers() {
        return questionAnswers;
    }

    public String toString() {
        StringBuilder linksToString = new StringBuilder();
        for (Item linkedItem: this.links){
            linksToString.append(String.valueOf(linkedItem.getId()));
        }
        return "Item{" +
                "id=" + id +
                ", sequence=" + sequence +
                ", content='" + content +
                ", group number=" + groupNumber +
                ", links=" + linksToString.toString() +
                '}';
    }

    public void remove() {
        this.questionDetails = null;
        this.questionAnswers.clear();
    }

    public Set<ItemCombinationAnswer> getQuestionStudentAnswers() {
        return questionStudentAnswers;
    }

    public void addQuestionStudentAnswer(ItemCombinationAnswer answer){
        this.questionStudentAnswers.add(answer);
    }

}
