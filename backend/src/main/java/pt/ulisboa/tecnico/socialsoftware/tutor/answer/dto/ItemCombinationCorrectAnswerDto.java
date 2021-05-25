package pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Item;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.ItemCombinationQuestion;

import java.util.ArrayList;
import java.util.List;

public class ItemCombinationCorrectAnswerDto extends CorrectAnswerDetailsDto{
private List<List<Integer>> correctLinksSequence;
    private boolean isCorrect;

    public ItemCombinationCorrectAnswerDto(){}

    public ItemCombinationCorrectAnswerDto(ItemCombinationQuestion question) {
        this.correctLinksSequence = new ArrayList<>();
        for (Item item : question.getGroup(1)){
            List<Integer> links = new ArrayList<>();
            for (Item linked : item.getLinks()){
                links.add(linked.getSequence());
            }
            this.correctLinksSequence.add(links);
        }
        System.out.println("oioi");
        System.out.println(this.correctLinksSequence + "\n\n\n");
    }

    public List<Integer> getCorrectLinksSequence(Integer sequence) {
        return correctLinksSequence.get(sequence);
    }

    public void setCorrectItemsId(List<List<Integer>> correctLinksSequence) {
        this.correctLinksSequence = correctLinksSequence;
    }

    public List<List<Integer>> getCorrectLinksSequence() {
        return correctLinksSequence;
    }

    public void setCorrectLinksSequence(List<List<Integer>> correctLinksSequence) {
        this.correctLinksSequence = correctLinksSequence;
    }

    @Override
    public String toString() {
        return "ItemCombinationCorrectAnswerDto{" +
                "correctItemId=" + correctLinksSequence +
                '}';
    }
}
