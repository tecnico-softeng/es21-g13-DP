package pt.ulisboa.tecnico.socialsoftware.tutor.question.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.CodeFillInQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.CodeOrderQuestion

import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.*
import spock.lang.Unroll



import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage


@DataJpaTest
class CreateQuestionTest extends SpockTest {
    def setup() {
        createExternalCourseAndExecution()
    }

    //item combination answer tests
    def "create empty ic question"(){
        given: "a questionDto"
        def questionDto = new QuestionDto()
        questionDto.setKey(1)
        questionDto.setTitle(QUESTION_1_TITLE)
        questionDto.setContent(QUESTION_1_CONTENT)
        questionDto.setStatus(Question.Status.AVAILABLE.name())
        questionDto.setQuestionDetailsDto(new ItemCombinationQuestionDto())

        when:
        questionService.createQuestion(externalCourse.getId(), questionDto)

        then: "the correct question is inside the repository"
        questionRepository.count() == 1L
        def result = questionRepository.findAll().get(0)
        result.getId() != null
        result.getKey() == 1
        result.getStatus() == Question.Status.AVAILABLE
        result.getTitle() == QUESTION_1_TITLE
        result.getContent() == QUESTION_1_CONTENT
        result.getImage() == null
        result.getQuestionDetails().getGroup(1).size() == 0
        result.getQuestionDetails().getGroup(2).size() == 0
        result.getCourse().getName() == COURSE_1_NAME
        externalCourse.getQuestions().contains(result)
    }

    def "create ic question with image and empty set of groups"(){
        given: "a questionDto"
        def questionDto = new QuestionDto()
        questionDto.setKey(1)
        questionDto.setTitle(QUESTION_1_TITLE)
        questionDto.setContent(QUESTION_1_CONTENT)
        questionDto.setStatus(Question.Status.AVAILABLE.name())
        questionDto.setQuestionDetailsDto(new ItemCombinationQuestionDto())

        and: 'an image'
        def image = new ImageDto()
        image.setUrl(IMAGE_1_URL)
        image.setWidth(20)
        questionDto.setImage(image)

        when:
        questionService.createQuestion(externalCourse.getId(), questionDto)

        then: "the correct question is inside the repository"
        questionRepository.count() == 1L
        def result = questionRepository.findAll().get(0)
        result.getId() != null
        result.getKey() == 1
        result.getStatus() == Question.Status.AVAILABLE
        result.getTitle() == QUESTION_1_TITLE
        result.getContent() == QUESTION_1_CONTENT
        result.getQuestionDetails().getGroup(1).size() == 0
        result.getQuestionDetails().getGroup(2).size() == 0
        result.getImage().getId() != null
        result.getImage().getUrl() == IMAGE_1_URL
        result.getImage().getWidth() == 20
        result.getCourse().getName() == COURSE_1_NAME
        externalCourse.getQuestions().contains(result)
    }

    def "create ic question with one to one element ratio"(){
        given: "a questionDto"
        def questionDto = new QuestionDto()
        questionDto.setKey(1)
        questionDto.setTitle(QUESTION_1_TITLE)
        questionDto.setContent(QUESTION_1_CONTENT)
        questionDto.setStatus(Question.Status.AVAILABLE.name())
        questionDto.setQuestionDetailsDto(new ItemCombinationQuestionDto())
        and: 'an itemDto'
        def itemDto1 = new ItemDto()
        itemDto1.setContent(ITEM_1_CONTENT)
        def group_1_items = new LinkedHashSet<ItemDto>()
        group_1_items.add(itemDto1)
        questionDto.getQuestionDetailsDto().setFirstGroup(group_1_items)
        def itemDto2 = new ItemDto()
        itemDto2.setContent(ITEM_2_CONTENT)
        def group_2_items = new LinkedHashSet<ItemDto>()
        group_2_items.add(itemDto2)
        questionDto.getQuestionDetailsDto().setSecondGroup(group_2_items)

        when:
        def qdto = questionService.createQuestion(externalCourse.getId(), questionDto)
        def iter = qdto.getQuestionDetailsDto().getGroup(1).iterator()
        def it1dto = iter.next()
        def iter2 = qdto.getQuestionDetailsDto().getGroup(2).iterator()
        def it2dto = iter2.next()
        questionService.createLink(it1dto, it2dto)


        then: "the correct question is inside the repository"
        questionRepository.count() == 1L
        def result = questionRepository.findAll().get(0)
        result.getId() != null
        result.getKey() == 1
        result.getStatus() == Question.Status.AVAILABLE
        result.getTitle() == QUESTION_1_TITLE
        result.getContent() == QUESTION_1_CONTENT
        result.getImage() == null
        result.getQuestionDetails().getGroup(1).size() == 1
        result.getQuestionDetails().getGroup(2).size() == 1
        def group1iterator = result.getQuestionDetails().getGroup(1).iterator()
        def group2iterator = result.getQuestionDetails().getGroup(2).iterator()
        def group1item = group1iterator.next()
        def group2item = group2iterator.next()
        group1item.getContent() == ITEM_1_CONTENT
        group2item.getContent() == ITEM_2_CONTENT
        group1item.getLinks().size() == 1
        group2item.getLinks().size() == 1
        group1item.getLinks().contains(group2item)
        group2item.getLinks().contains(group1item)
        result.getCourse().getName() == COURSE_1_NAME
        externalCourse.getQuestions().contains(result)
    }


    def "create ic question with one to two elements the groups"(){
        given: "a questionDto"
        def questionDto = new QuestionDto()
        questionDto.setKey(1)
        questionDto.setTitle(QUESTION_1_TITLE)
        questionDto.setContent(QUESTION_1_CONTENT)
        questionDto.setStatus(Question.Status.AVAILABLE.name())
        questionDto.setQuestionDetailsDto(new ItemCombinationQuestionDto())
        and: 'an itemDto'
        def itemDto1 = new ItemDto()
        itemDto1.setContent(ITEM_1_CONTENT)
        def group_1_items = new LinkedHashSet<ItemDto>()
        group_1_items.add(itemDto1)
        questionDto.getQuestionDetailsDto().setFirstGroup(group_1_items)
        def itemDto2 = new ItemDto()
        itemDto2.setContent(ITEM_2_CONTENT)
        def itemDto3 = new ItemDto()
        itemDto3.setContent(ITEM_3_CONTENT)
        def group_2_items = new LinkedHashSet<ItemDto>()
        group_2_items.add(itemDto2)
        group_2_items.add(itemDto3)
        questionDto.getQuestionDetailsDto().setSecondGroup(group_2_items)


        when:
        def qdto = questionService.createQuestion(externalCourse.getId(), questionDto)
        def iter = qdto.getQuestionDetailsDto().getGroup(1).iterator()
        def it1dto = iter.next()
        def iter2 = qdto.getQuestionDetailsDto().getGroup(2).iterator()
        def it2dto = iter2.next()
        def it3dto = iter2.next()
        questionService.createLink(it1dto, it2dto)
        questionService.createLink(it1dto, it3dto)

        then: "the correct question is inside the repository"
        questionRepository.count() == 1L
        def result = questionRepository.findAll().get(0)
        result.getId() != null
        result.getKey() == 1
        result.getStatus() == Question.Status.AVAILABLE
        result.getTitle() == QUESTION_1_TITLE
        result.getContent() == QUESTION_1_CONTENT
        result.getImage() == null
        result.getQuestionDetails().getGroup(1).size() == 1
        result.getQuestionDetails().getGroup(2).size() == 2
        def group1iterator = result.getQuestionDetails().getGroup(1).iterator()
        def group2iterator = result.getQuestionDetails().getGroup(2).iterator()
        def group1item = group1iterator.next()
        def group2item = group2iterator.next()
        def group3item = group2iterator.next()
        group1item.getContent() == ITEM_1_CONTENT
        group2item.getContent() == ITEM_2_CONTENT
        group3item.getContent() == ITEM_3_CONTENT
        group1item.getLinks().size() == 2
        group2item.getLinks().size() == 1
        group3item.getLinks().size() == 1
        group1item.getLinks().contains(group2item)
        group1item.getLinks().contains(group3item)
        group2item.getLinks().contains(group1item)
        group3item.getLinks().contains(group1item)
        result.getCourse().getName() == COURSE_1_NAME
        externalCourse.getQuestions().contains(result)
    }



    def "create ic question with two to one element ratio"(){
        given: "a questionDto"
        def questionDto = new QuestionDto()
        questionDto.setKey(1)
        questionDto.setTitle(QUESTION_1_TITLE)
        questionDto.setContent(QUESTION_1_CONTENT)
        questionDto.setStatus(Question.Status.AVAILABLE.name())
        questionDto.setQuestionDetailsDto(new ItemCombinationQuestionDto())
        and: 'an itemDto'
        def itemDto1 = new ItemDto()
        itemDto1.setContent(ITEM_1_CONTENT)
        def itemDto2 = new ItemDto()
        itemDto2.setContent(ITEM_2_CONTENT)
        def group_1_items = new LinkedHashSet<ItemDto>()
        group_1_items.add(itemDto1)
        group_1_items.add(itemDto2)
        questionDto.getQuestionDetailsDto().setFirstGroup(group_1_items)
        def itemDto3 = new ItemDto()
        itemDto3.setContent(ITEM_3_CONTENT)
        def group_2_items = new LinkedHashSet<ItemDto>()
        group_2_items.add(itemDto3)
        questionDto.getQuestionDetailsDto().setSecondGroup(group_2_items)

        when:
        def qdto = questionService.createQuestion(externalCourse.getId(), questionDto)
        def iter = qdto.getQuestionDetailsDto().getGroup(1).iterator()
        def it1dto = iter.next()
        def it2dto = iter.next()
        def iter2 = qdto.getQuestionDetailsDto().getGroup(2).iterator()
        def it3dto = iter2.next()
        questionService.createLink(it1dto, it3dto)
        questionService.createLink(it2dto, it3dto)

        then: "the correct question is inside the repository"
        questionRepository.count() == 1L
        def result = questionRepository.findAll().get(0)
        result.getId() != null
        result.getKey() == 1
        result.getStatus() == Question.Status.AVAILABLE
        result.getTitle() == QUESTION_1_TITLE
        result.getContent() == QUESTION_1_CONTENT
        result.getImage() == null
        result.getQuestionDetails().getGroup(1).size() == 2
        result.getQuestionDetails().getGroup(2).size() == 1
        def group1iterator = result.getQuestionDetails().getGroup(1).iterator()
        def group2iterator = result.getQuestionDetails().getGroup(2).iterator()
        def group1item = group1iterator.next()
        def group2item = group1iterator.next()
        def group3item = group2iterator.next()
        group1item.getContent() == ITEM_1_CONTENT
        group2item.getContent() == ITEM_2_CONTENT
        group3item.getContent() == ITEM_3_CONTENT
        group1item.getLinks().size() == 1
        group2item.getLinks().size() == 1
        group3item.getLinks().size() == 2
        group1item.getLinks().contains(group3item)
        group2item.getLinks().contains(group3item)
        group3item.getLinks().contains(group1item)
        group3item.getLinks().contains(group1item)
        result.getCourse().getName() == COURSE_1_NAME
        externalCourse.getQuestions().contains(result)
    }

    def "create ic question with two to two element ratio"(){
        given: "a questionDto"
        def questionDto = new QuestionDto()
        questionDto.setKey(1)
        questionDto.setTitle(QUESTION_1_TITLE)
        questionDto.setContent(QUESTION_1_CONTENT)
        questionDto.setStatus(Question.Status.AVAILABLE.name())
        questionDto.setQuestionDetailsDto(new ItemCombinationQuestionDto())
        and: 'an itemDto'
        def itemDto1 = new ItemDto()
        itemDto1.setContent(ITEM_1_CONTENT)
        def itemDto2 = new ItemDto()
        itemDto2.setContent(ITEM_2_CONTENT)
        def group_1_items = new LinkedHashSet<ItemDto>()
        group_1_items.add(itemDto1)
        group_1_items.add(itemDto2)
        questionDto.getQuestionDetailsDto().setFirstGroup(group_1_items)
        def itemDto3 = new ItemDto()
        itemDto3.setContent(ITEM_3_CONTENT)
        def itemDto4 = new ItemDto()
        itemDto4.setContent(ITEM_4_CONTENT)
        def group_2_items = new LinkedHashSet<ItemDto>()
        group_2_items.add(itemDto3)
        group_2_items.add(itemDto4)
        questionDto.getQuestionDetailsDto().setSecondGroup(group_2_items)

        when:
        def qdto = questionService.createQuestion(externalCourse.getId(), questionDto)
        def iter = qdto.getQuestionDetailsDto().getGroup(1).iterator()
        def it1dto = iter.next()
        def it2dto = iter.next()
        def iter2 = qdto.getQuestionDetailsDto().getGroup(2).iterator()
        def it3dto = iter2.next()
        def it4dto = iter2.next()
        questionService.createLink(it2dto, it4dto)
        questionService.createLink(it1dto, it3dto)
        questionService.createLink(it2dto, it3dto)
        questionService.createLink(it1dto, it4dto)


        then: "the correct question is inside the repository"
        questionRepository.count() == 1L
        def result = questionRepository.findAll().get(0)
        result.getId() != null
        result.getKey() == 1
        result.getStatus() == Question.Status.AVAILABLE
        result.getTitle() == QUESTION_1_TITLE
        result.getContent() == QUESTION_1_CONTENT
        result.getImage() == null
        result.getQuestionDetails().getGroup(1).size() == 2
        result.getQuestionDetails().getGroup(2).size() == 2
        def group1iterator = result.getQuestionDetails().getGroup(1).iterator()
        def group2iterator = result.getQuestionDetails().getGroup(2).iterator()
        def group1item = group1iterator.next()
        def group2item = group1iterator.next()
        def group3item = group2iterator.next()
        def group4item = group2iterator.next()
        group1item.getContent() == ITEM_1_CONTENT
        group2item.getContent() == ITEM_2_CONTENT
        group3item.getContent() == ITEM_3_CONTENT
        group4item.getContent() == ITEM_4_CONTENT
        group1item.getLinks().size() == 2
        group2item.getLinks().size() == 2
        group3item.getLinks().size() == 2
        group4item.getLinks().size() == 2
        group1item.getLinks().contains(group3item)
        group1item.getLinks().contains(group4item)
        group2item.getLinks().contains(group3item)
        group2item.getLinks().contains(group4item)
        group3item.getLinks().contains(group1item)
        group3item.getLinks().contains(group2item)
        group4item.getLinks().contains(group1item)
        group4item.getLinks().contains(group2item)
        result.getCourse().getName() == COURSE_1_NAME
        externalCourse.getQuestions().contains(result)
    }

    def "create two ic questions"(){
        given: "two questionDtos"
        def question_1_Dto = new QuestionDto()
        question_1_Dto.setKey(1)
        question_1_Dto.setTitle(QUESTION_1_TITLE)
        question_1_Dto.setContent(QUESTION_1_CONTENT)
        question_1_Dto.setStatus(Question.Status.AVAILABLE.name())
        question_1_Dto.setQuestionDetailsDto(new ItemCombinationQuestionDto())
        def question_2_Dto = new QuestionDto()
        question_2_Dto.setKey(2)
        question_2_Dto.setTitle(QUESTION_2_TITLE)
        question_2_Dto.setContent(QUESTION_2_CONTENT)
        question_2_Dto.setStatus(Question.Status.AVAILABLE.name())
        question_2_Dto.setQuestionDetailsDto(new ItemCombinationQuestionDto())
        and: 'a group with one item for each questionDto'

        def itemDto1 = new ItemDto()
        itemDto1.setContent(ITEM_1_CONTENT)
        def group_1_items_question_1 = new LinkedHashSet<ItemDto>()
        group_1_items_question_1.add(itemDto1)
        question_1_Dto.getQuestionDetailsDto().setFirstGroup(group_1_items_question_1)
        def itemDto2 = new ItemDto()
        itemDto2.setContent(ITEM_2_CONTENT)
        def group_2_items_question_1 = new LinkedHashSet<ItemDto>()
        group_2_items_question_1.add(itemDto2)
        question_1_Dto.getQuestionDetailsDto().setSecondGroup(group_2_items_question_1)

        def itemDto3 = new ItemDto()
        itemDto3.setContent(ITEM_3_CONTENT)
        def group_1_items_question_2 = new LinkedHashSet<ItemDto>()
        group_1_items_question_2.add(itemDto3)
        question_2_Dto.getQuestionDetailsDto().setFirstGroup(group_1_items_question_2)
        def itemDto4 = new ItemDto()
        itemDto4.setContent(ITEM_4_CONTENT)
        def group_2_items_question_2 = new LinkedHashSet<ItemDto>()
        group_2_items_question_2.add(itemDto4)
        question_2_Dto.getQuestionDetailsDto().setSecondGroup(group_2_items_question_2)




        when:
        def qdto1 = questionService.createQuestion(externalCourse.getId(), question_1_Dto)
        def qdto2 = questionService.createQuestion(externalCourse.getId(), question_2_Dto)

        def iter11 = qdto1.getQuestionDetailsDto().getGroup(1).iterator()
        def it1dto_q1 = iter11.next()
        def iter12 = qdto1.getQuestionDetailsDto().getGroup(2).iterator()
        def it2dto_q1 = iter12.next()
        def iter21 = qdto2.getQuestionDetailsDto().getGroup(1).iterator()
        def it1dto_q2 = iter21.next()
        def iter22 = qdto2.getQuestionDetailsDto().getGroup(2).iterator()
        def it2dto_q2 = iter22.next()

        questionService.createLink(it1dto_q1, it2dto_q1)
        questionService.createLink(it1dto_q2, it2dto_q2)

        then: "the correct questions are inside the repository"
        questionRepository.count() == 2L
        def result_1 = questionRepository.findAll().get(0)
        def result_2 = questionRepository.findAll().get(1)
        result_1.getId() != null
        result_1.getKey() == 1
        result_1.getStatus() == Question.Status.AVAILABLE
        result_1.getTitle() == QUESTION_1_TITLE
        result_1.getContent() == QUESTION_1_CONTENT
        result_1.getImage() == null
        result_2.getId() != null
        result_2.getKey() == 2
        result_2.getStatus() == Question.Status.AVAILABLE
        result_2.getTitle() == QUESTION_2_TITLE
        result_2.getContent() == QUESTION_2_CONTENT
        result_2.getImage() == null
        System.out.println("\n\n chegou aqui \n\n")
        result_1.getQuestionDetails().getGroup(1).size() == 1
        result_1.getQuestionDetails().getGroup(2).size() == 1
        result_2.getQuestionDetails().getGroup(1).size() == 1
        result_2.getQuestionDetails().getGroup(2).size() == 1
        def question1group1iterator = result_1.getQuestionDetails().getGroup(1).iterator()
        def question1group2iterator = result_1.getQuestionDetails().getGroup(2).iterator()
        def question2group1iterator = result_2.getQuestionDetails().getGroup(1).iterator()
        def question2group2iterator = result_2.getQuestionDetails().getGroup(2).iterator()
        def question1group1item = question1group1iterator.next()
        def question1group2item = question1group2iterator.next()
        def question2group1item = question2group1iterator.next()
        def question2group2item = question2group2iterator.next()
        question1group1item.getContent() == ITEM_1_CONTENT
        question1group2item.getContent() == ITEM_2_CONTENT
        question2group1item.getContent() == ITEM_3_CONTENT
        question2group2item.getContent() == ITEM_4_CONTENT
        question1group1item.getLinks().size() == 1
        question1group2item.getLinks().size() == 1
        question2group1item.getLinks().size() == 1
        question2group2item.getLinks().size() == 1
        question1group1item.getLinks().contains(question1group2item)
        question1group2item.getLinks().contains(question1group1item)
        question2group1item.getLinks().contains(question2group2item)
        question2group2item.getLinks().contains(question2group1item)
        result_1.getCourse().getName() == COURSE_1_NAME
        externalCourse.getQuestions().contains(result_1)
        result_2.getCourse().getName() == COURSE_1_NAME
        externalCourse.getQuestions().contains(result_2)

    }


    //end of item combination answer test



    def "create a multiple choice question with no image and one option"() {
        given: "a questionDto"
        def questionDto = new QuestionDto()
        questionDto.setKey(1)
        questionDto.setTitle(QUESTION_1_TITLE)
        questionDto.setContent(QUESTION_1_CONTENT)
        questionDto.setStatus(Question.Status.AVAILABLE.name())
        questionDto.setQuestionDetailsDto(new MultipleChoiceQuestionDto())
        and: 'a optionId'
        def optionDto = new OptionDto()
        optionDto.setContent(OPTION_1_CONTENT)
        optionDto.setCorrect(true)
        def options = new ArrayList<OptionDto>()
        options.add(optionDto)
        questionDto.getQuestionDetailsDto().setOptions(options)

        when:
        questionService.createQuestion(externalCourse.getId(), questionDto)

        then: "the correct question is inside the repository"
        questionRepository.count() == 1L
        def result = questionRepository.findAll().get(0)
        result.getId() != null
        result.getKey() == 1
        result.getStatus() == Question.Status.AVAILABLE
        result.getTitle() == QUESTION_1_TITLE
        result.getContent() == QUESTION_1_CONTENT
        result.getImage() == null
        result.getQuestionDetails().getOptions().size() == 1
        result.getCourse().getName() == COURSE_1_NAME
        externalCourse.getQuestions().contains(result)
        def resOption = result.getQuestionDetails().getOptions().get(0)
        resOption.getContent() == OPTION_1_CONTENT
        resOption.isCorrect()

    }

    def "create a multiple choice question with image and two options"() {
        given: "a questionDto"
        def questionDto = new QuestionDto()
        questionDto.setKey(1)
        questionDto.setTitle(QUESTION_1_TITLE)
        questionDto.setContent(QUESTION_1_CONTENT)
        questionDto.setStatus(Question.Status.AVAILABLE.name())
        questionDto.setQuestionDetailsDto(new MultipleChoiceQuestionDto())

        and: 'an image'
        def image = new ImageDto()
        image.setUrl(IMAGE_1_URL)
        image.setWidth(20)
        questionDto.setImage(image)
        and: 'two options'
        def optionDto = new OptionDto()
        optionDto.setContent(OPTION_1_CONTENT)
        optionDto.setCorrect(true)
        def options = new ArrayList<OptionDto>()
        options.add(optionDto)
        optionDto = new OptionDto()
        optionDto.setContent(OPTION_1_CONTENT)
        optionDto.setCorrect(false)
        options.add(optionDto)
        questionDto.getQuestionDetailsDto().setOptions(options)

        when:
        questionService.createQuestion(externalCourse.getId(), questionDto)

        then: "the correct question is inside the repository"
        questionRepository.count() == 1L
        def result = questionRepository.findAll().get(0)
        result.getId() != null
        result.getKey() == 1
        result.getStatus() == Question.Status.AVAILABLE
        result.getTitle() == QUESTION_1_TITLE
        result.getContent() == QUESTION_1_CONTENT
        result.getImage().getId() != null
        result.getImage().getUrl() == IMAGE_1_URL
        result.getImage().getWidth() == 20
        result.getQuestionDetails().getOptions().size() == 2
    }

    def "create two multiple choice questions"() {
        given: "a questionDto"
        def questionDto = new QuestionDto()
        questionDto.setTitle(QUESTION_1_TITLE)
        questionDto.setContent(QUESTION_1_CONTENT)
        questionDto.setStatus(Question.Status.AVAILABLE.name())
        questionDto.setQuestionDetailsDto(new MultipleChoiceQuestionDto())
        and: 'a optionId'
        def optionDto = new OptionDto()
        optionDto.setContent(OPTION_1_CONTENT)
        optionDto.setCorrect(true)
        def options = new ArrayList<OptionDto>()
        options.add(optionDto)
        questionDto.getQuestionDetailsDto().setOptions(options)

        when: 'are created two questions'
        questionService.createQuestion(externalCourse.getId(), questionDto)
        questionDto.setKey(null)
        questionService.createQuestion(externalCourse.getId(), questionDto)

        then: "the two questions are created with the correct numbers"
        questionRepository.count() == 2L
        def resultOne = questionRepository.findAll().get(0)
        def resultTwo = questionRepository.findAll().get(1)
        resultOne.getKey() + resultTwo.getKey() == 3
    }

    def "create multiple choice question with two correct options and one wrong"() {
        given: "a questionDto"
        def questionDto = new QuestionDto()
        questionDto.setKey(1)
        questionDto.setTitle(QUESTION_1_TITLE)
        questionDto.setContent(QUESTION_1_CONTENT)
        questionDto.setStatus(Question.Status.AVAILABLE.name())
        questionDto.setQuestionDetailsDto(new MultipleChoiceQuestionDto())

        and: 'three options'
        def optionDto = new OptionDto()
        optionDto.setContent(OPTION_1_CONTENT)
        optionDto.setCorrect(true)
        def options = new ArrayList<OptionDto>()
        options.add(optionDto)
        optionDto = new OptionDto()
        optionDto.setContent(OPTION_1_CONTENT)
        optionDto.setCorrect(true)
        options.add(optionDto)
        optionDto = new OptionDto()
        optionDto.setContent(OPTION_1_CONTENT)
        optionDto.setCorrect(false)
        options.add(optionDto)
        questionDto.getQuestionDetailsDto().setOptions(options)

        when:
        questionService.createQuestion(externalCourse.getId(), questionDto)

        then: "the correct question is inside the repository"
        questionRepository.count() == 1L
        def result = questionRepository.findAll().get(0)
        result.getId() != null
        result.getKey() == 1
        result.getStatus() == Question.Status.AVAILABLE
        result.getTitle() == QUESTION_1_TITLE
        result.getContent() == QUESTION_1_CONTENT
        result.getQuestionDetails().getOptions().size() == 3
        result.getQuestionDetails().getCorrectAnswers().size() == 2
    }

    def "create multiple choice question where two answers have different relevance"() {
        given: "a questionDto"
        def questionDto = new QuestionDto()
        questionDto.setKey(1)
        questionDto.setTitle(QUESTION_1_TITLE)
        questionDto.setContent(QUESTION_1_CONTENT)
        questionDto.setStatus(Question.Status.AVAILABLE.name())
        questionDto.setQuestionDetailsDto(new MultipleChoiceQuestionDto())

        and: 'two options'
        def optionDto = new OptionDto()
        optionDto.setContent(OPTION_1_CONTENT)
        optionDto.setCorrect(true)
        optionDto.setRelevance(1)
        def options = new ArrayList<OptionDto>()
        options.add(optionDto)
        optionDto = new OptionDto()
        optionDto.setContent(OPTION_1_CONTENT)
        optionDto.setCorrect(true)
        optionDto.setRelevance(2)
        options.add(optionDto)
        questionDto.getQuestionDetailsDto().setOptions(options)

        when:
        questionService.createQuestion(externalCourse.getId(), questionDto)

        then: "the correct answer is inside the repository"
        questionRepository.count() == 1L
        def result = questionRepository.findAll().get(0)
        result.getId() != null
        result.getKey() == 1
        result.getStatus() == Question.Status.AVAILABLE
        result.getTitle() == QUESTION_1_TITLE
        result.getContent() == QUESTION_1_CONTENT
        result.getQuestionDetails().getOptions().size() == 2
        result.getQuestionDetails().getCorrectOptionsRelevance().get(0) != result.getQuestionDetails().getCorrectOptionsRelevance().get(1)
    }

    def "create a code fill in question"() {
        given: "a questionDto"
        def questionDto = new QuestionDto()
        questionDto.setKey(1)
        questionDto.setTitle(QUESTION_1_TITLE)
        questionDto.setContent(QUESTION_1_CONTENT)
        questionDto.setStatus(Question.Status.AVAILABLE.name())

        def codeQuestionDto = new CodeFillInQuestionDto()
        codeQuestionDto.setCode(CODE_QUESTION_1_CODE)
        codeQuestionDto.setLanguage(CODE_QUESTION_1_LANGUAGE)

        CodeFillInSpotDto fillInSpotDto = new CodeFillInSpotDto()
        OptionDto optionDto = new OptionDto()
        optionDto.setContent(OPTION_1_CONTENT)
        optionDto.setCorrect(true)
        fillInSpotDto.getOptions().add(optionDto)
        fillInSpotDto.setSequence(1)

        codeQuestionDto.getFillInSpots().add(fillInSpotDto)

        questionDto.setQuestionDetailsDto(codeQuestionDto)

        when:
        def rawResult = questionService.createQuestion(externalCourse.getId(), questionDto)

        then: "the correct data is sent back"
        rawResult instanceof QuestionDto
        def result = (QuestionDto) rawResult
        result.getId() != null
        result.getStatus() == Question.Status.AVAILABLE.toString()
        result.getTitle() == QUESTION_1_TITLE
        result.getContent() == QUESTION_1_CONTENT
        result.getImage() == null
        result.getQuestionDetailsDto().getFillInSpots().size() == 1
        result.getQuestionDetailsDto().getFillInSpots().get(0).getOptions().size() == 1

        then: "the correct question is inside the repository"
        questionRepository.count() == 1L
        def repoResult = questionRepository.findAll().get(0)
        repoResult.getId() != null
        repoResult.getKey() == 1
        repoResult.getStatus() == Question.Status.AVAILABLE
        repoResult.getTitle() == QUESTION_1_TITLE
        repoResult.getContent() == QUESTION_1_CONTENT
        repoResult.getImage() == null
        repoResult.getCourse().getName() == COURSE_1_NAME
        externalCourse.getQuestions().contains(repoResult)

        def repoCode = (CodeFillInQuestion) repoResult.getQuestionDetails()
        repoCode.getFillInSpots().size() == 1
        repoCode.getCode() == CODE_QUESTION_1_CODE
        repoCode.getLanguage() == CODE_QUESTION_1_LANGUAGE
        def resOption = repoCode.getFillInSpots().get(0).getOptions().get(0)
        resOption.getContent() == OPTION_1_CONTENT
        resOption.isCorrect()

    }

    def "cannot create a code fill in question without fillin spots"() {
        given: "a questionDto"
        def questionDto = new QuestionDto()
        questionDto.setKey(1)
        questionDto.setTitle(QUESTION_1_TITLE)
        questionDto.setContent(QUESTION_1_CONTENT)
        questionDto.setStatus(Question.Status.AVAILABLE.name())
        questionDto.setQuestionDetailsDto(new CodeFillInQuestionDto())

        when:
        def result = questionService.createQuestion(externalCourse.getId(), questionDto)

        then: "exception is thrown"
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.AT_LEAST_ONE_OPTION_NEEDED
    }

    def "cannot create a code fill in question with fillin spots without options"() {
        given: "a questionDto with 1 fill in spot without options"
        def questionDto = new QuestionDto()
        questionDto.setKey(1)
        questionDto.setTitle(QUESTION_1_TITLE)
        questionDto.setContent(QUESTION_1_CONTENT)
        questionDto.setStatus(Question.Status.AVAILABLE.name())
        questionDto.setQuestionDetailsDto(new CodeFillInQuestionDto())

        CodeFillInSpotDto fillInSpotDto = new CodeFillInSpotDto()
        questionDto.getQuestionDetailsDto().getFillInSpots().add(fillInSpotDto)


        when:
        def result = questionService.createQuestion(externalCourse.getId(), questionDto)

        then: "exception is thrown"
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.NO_CORRECT_OPTION
    }

    def "cannot create a code fill in question with fillin spots without correct options"() {
        given: "a questionDto with 1 fill in spot without options"
        def questionDto = new QuestionDto()
        questionDto.setKey(1)
        questionDto.setTitle(QUESTION_1_TITLE)
        questionDto.setContent(QUESTION_1_CONTENT)
        questionDto.setStatus(Question.Status.AVAILABLE.name())
        questionDto.setQuestionDetailsDto(new CodeFillInQuestionDto())

        CodeFillInSpotDto fillInSpotDto = new CodeFillInSpotDto()
        OptionDto optionDto = new OptionDto()
        optionDto.setContent(OPTION_1_CONTENT)
        optionDto.setCorrect(false)
        questionDto.getQuestionDetailsDto().getFillInSpots().add(fillInSpotDto)


        when:
        def result = questionService.createQuestion(externalCourse.getId(), questionDto)

        then: "exception is thrown"
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.NO_CORRECT_OPTION
    }


    def "create a code order question"() {
        given: "a questionDto"
        def questionDto = new QuestionDto()
        questionDto.setKey(1)
        questionDto.setTitle(QUESTION_1_TITLE)
        questionDto.setContent(QUESTION_1_CONTENT)
        questionDto.setStatus(Question.Status.AVAILABLE.name())

        def codeQuestionDto = new CodeOrderQuestionDto()
        codeQuestionDto.setLanguage(CODE_QUESTION_1_LANGUAGE)

        CodeOrderSlotDto slotDto1 = new CodeOrderSlotDto()
        slotDto1.content = OPTION_1_CONTENT;
        slotDto1.order = 1;

        CodeOrderSlotDto slotDto2 = new CodeOrderSlotDto()
        slotDto2.content = OPTION_1_CONTENT;
        slotDto2.order = 2;

        CodeOrderSlotDto slotDto3 = new CodeOrderSlotDto()
        slotDto3.content = OPTION_1_CONTENT;
        slotDto3.order = 3;

        codeQuestionDto.getCodeOrderSlots().add(slotDto1)
        codeQuestionDto.getCodeOrderSlots().add(slotDto2)
        codeQuestionDto.getCodeOrderSlots().add(slotDto3)

        questionDto.setQuestionDetailsDto(codeQuestionDto)

        when:
        def rawResult = questionService.createQuestion(externalCourse.getId(), questionDto)

        then: "the correct data is sent back"
        rawResult instanceof QuestionDto
        def result = (QuestionDto) rawResult
        result.getId() != null
        result.getStatus() == Question.Status.AVAILABLE.toString()
        result.getTitle() == QUESTION_1_TITLE
        result.getContent() == QUESTION_1_CONTENT
        result.getImage() == null
        result.getQuestionDetailsDto().getCodeOrderSlots().size() == 3
        result.getQuestionDetailsDto().getCodeOrderSlots().get(0).getContent() == OPTION_1_CONTENT

        then: "the correct question is inside the repository"
        questionRepository.count() == 1L
        def repoResult = questionRepository.findAll().get(0)
        repoResult.getId() != null
        repoResult.getKey() == 1
        repoResult.getStatus() == Question.Status.AVAILABLE
        repoResult.getTitle() == QUESTION_1_TITLE
        repoResult.getContent() == QUESTION_1_CONTENT
        repoResult.getImage() == null
        repoResult.getCourse().getName() == COURSE_1_NAME
        externalCourse.getQuestions().contains(repoResult)

        def repoCode = (CodeOrderQuestion) repoResult.getQuestionDetails()
        repoCode.getCodeOrderSlots().size() == 3
        repoCode.getLanguage() == CODE_QUESTION_1_LANGUAGE
        def resOption = repoCode.getCodeOrderSlots().get(0)
        resOption.getContent() == OPTION_1_CONTENT
    }

    def "cannot create a code order question without CodeOrderSlots"() {
        given: "a questionDto"
        def questionDto = new QuestionDto()
        questionDto.setKey(1)
        questionDto.setTitle(QUESTION_1_TITLE)
        questionDto.setContent(QUESTION_1_CONTENT)
        questionDto.setStatus(Question.Status.AVAILABLE.name())

        def codeQuestionDto = new CodeOrderQuestionDto()
        codeQuestionDto.setLanguage(CODE_QUESTION_1_LANGUAGE)

        questionDto.setQuestionDetailsDto(codeQuestionDto)

        when:
        def result = questionService.createQuestion(externalCourse.getId(), questionDto)

        then: "exception is thrown"
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.AT_LEAST_THREE_SLOTS_NEEDED
    }

    def "cannot create a code order question without 3 CodeOrderSlots"() {
        given: "a questionDto"
        def questionDto = new QuestionDto()
        questionDto.setKey(1)
        questionDto.setTitle(QUESTION_1_TITLE)
        questionDto.setContent(QUESTION_1_CONTENT)
        questionDto.setStatus(Question.Status.AVAILABLE.name())

        def codeQuestionDto = new CodeOrderQuestionDto()
        codeQuestionDto.setLanguage(CODE_QUESTION_1_LANGUAGE)

        CodeOrderSlotDto slotDto1 = new CodeOrderSlotDto()
        slotDto1.content = OPTION_1_CONTENT;
        slotDto1.order = 1;

        CodeOrderSlotDto slotDto2 = new CodeOrderSlotDto()
        slotDto2.content = OPTION_1_CONTENT;
        slotDto2.order = 2;

        codeQuestionDto.getCodeOrderSlots().add(slotDto1)
        codeQuestionDto.getCodeOrderSlots().add(slotDto2)

        questionDto.setQuestionDetailsDto(codeQuestionDto)
        when:
        def result = questionService.createQuestion(externalCourse.getId(), questionDto)

        then: "exception is thrown"
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.AT_LEAST_THREE_SLOTS_NEEDED
    }

    def "cannot create a code order question without 3 CodeOrderSlots with order"() {
        given: "a questionDto"
        def questionDto = new QuestionDto()
        questionDto.setKey(1)
        questionDto.setTitle(QUESTION_1_TITLE)
        questionDto.setContent(QUESTION_1_CONTENT)
        questionDto.setStatus(Question.Status.AVAILABLE.name())

        def codeQuestionDto = new CodeOrderQuestionDto()
        codeQuestionDto.setLanguage(CODE_QUESTION_1_LANGUAGE)

        CodeOrderSlotDto slotDto1 = new CodeOrderSlotDto()
        slotDto1.content = OPTION_1_CONTENT;
        slotDto1.order = 1;

        CodeOrderSlotDto slotDto2 = new CodeOrderSlotDto()
        slotDto2.content = OPTION_1_CONTENT;
        slotDto2.order = 2;

        CodeOrderSlotDto slotDto3 = new CodeOrderSlotDto()
        slotDto3.content = OPTION_1_CONTENT;
        slotDto3.order = null;

        codeQuestionDto.getCodeOrderSlots().add(slotDto1)
        codeQuestionDto.getCodeOrderSlots().add(slotDto2)
        codeQuestionDto.getCodeOrderSlots().add(slotDto3)

        questionDto.setQuestionDetailsDto(codeQuestionDto)
        when:
        def result = questionService.createQuestion(externalCourse.getId(), questionDto)

        then: "exception is thrown"
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.AT_LEAST_THREE_SLOTS_NEEDED
    }


    @Unroll
    def "fail to create any question for invalid/non-existent course (#nonExistentId)"(Integer nonExistentId) {
        given: "any multiple choice question dto"
        def questionDto = new QuestionDto()
        when:
        questionService.createQuestion(nonExistentId, questionDto)
        then:
        def exception = thrown(TutorException)
        exception.errorMessage == ErrorMessage.COURSE_NOT_FOUND
        where:
        nonExistentId << [-1, 0, 200]
    }


    def "create an open answer question"() {
        given: "a questionDto"
        def questionDto = new QuestionDto()
        questionDto.setKey(1)
        questionDto.setTitle(QUESTION_1_TITLE)
        questionDto.setContent(QUESTION_1_CONTENT)
        questionDto.setStatus(Question.Status.AVAILABLE.name())
        questionDto.setQuestionDetailsDto(new OpenQuestionDto())
        and: "a correct answer to the question"
        questionDto.getQuestionDetailsDto().setAnswer(OPEN_ANSWER_1)

        when:
        questionService.createQuestion(externalCourse.getId(), questionDto)

        then: "the correct question is inside the repository"
        questionRepository.count() == 1L
        def result = questionRepository.findAll().get(0)
        result.getId() != null
        result.getKey() == 1
        result.getStatus() == Question.Status.AVAILABLE
        result.getTitle() == QUESTION_1_TITLE
        result.getContent() == QUESTION_1_CONTENT
        result.getImage() == null
        result.getCourse().getName() == COURSE_1_NAME
        externalCourse.getQuestions().contains(result)
        result.getQuestionDetails().getAnswer() == OPEN_ANSWER_1
    }

    def "open answer question cannot receive an empty correct answer"() {
        given: "any open answer question"
        def questionDto = new QuestionDto()
        questionDto.setKey(1)
        questionDto.setTitle(QUESTION_1_TITLE)
        questionDto.setContent(QUESTION_1_CONTENT)
        questionDto.setStatus(Question.Status.AVAILABLE.name())
        questionDto.setQuestionDetailsDto(new OpenQuestionDto())
        when:
        questionService.createQuestion(externalCourse.getId(), questionDto)
        then:
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.EMPTY_ANSWER
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}