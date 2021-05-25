package pt.ulisboa.tecnico.socialsoftware.tutor.question.dto;


import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Item;
import java.io.Serializable;
import java.util.*;

public class ItemDto implements Serializable {


    private Integer id;
    private Integer sequence;
    private Set<Integer> itemSequenceSet = new LinkedHashSet<>();
    private String content;

    public ItemDto(){}

    public ItemDto(Item item){
        this.id = item.getId();
        this.sequence = item.getSequence();
        this.content = item.getContent();
        this.addAllLinks(item);
    }
    public Integer getSequence() {
        return sequence;
    }
    public void setSequence(Integer sequence) {
        this.sequence = sequence;
    }

    public void addLink(Integer sequence){
        this.itemSequenceSet.add(sequence);
    }

    public void removeLink(Integer sequence){
        this.itemSequenceSet.remove(sequence);
    }

    public Set<Integer> getItemSequenceSet(){return itemSequenceSet;}

    public void setId(Integer iD){
        this.id = iD;
    }

    public Integer getId() {
        return id;
    }
    public void setContent(String content){this.content = content;}
    public String getContent() {
        return content;
    }


    public void addAllLinks(Item item) {
        for (Item linkedItem:item.getLinks()){
            this.addLink(linkedItem.getSequence());
        }
    }

    @Override
    public String toString() {
        return "ItemDto{" +
                "id=" + id +
                ", content='" + content + '\'' +
        '}';
    }
}