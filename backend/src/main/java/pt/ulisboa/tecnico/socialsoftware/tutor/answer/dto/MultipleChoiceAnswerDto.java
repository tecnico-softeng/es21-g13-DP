package pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.MultipleChoiceAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.OptionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Option;

import java.util.ArrayList;
import java.util.List;

public class MultipleChoiceAnswerDto extends AnswerDetailsDto {
    private List<OptionDto> options;

    public MultipleChoiceAnswerDto() {
        this.options = new ArrayList<OptionDto>();
    }

    public MultipleChoiceAnswerDto(MultipleChoiceAnswer answer) {
        this.options = new ArrayList<OptionDto>();
        for (Option option : answer.getOptions()){
            this.options.add(new OptionDto(option));
        }
    }

    public List<OptionDto> getOptions() {
        return options;
    }

    public void setOption(List<OptionDto> options) {
        this.options = options;
    }
}
