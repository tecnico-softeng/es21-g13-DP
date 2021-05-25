package pt.ulisboa.tecnico.socialsoftware.tutor.question.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuestionAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuizAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.auth.domain.AuthUser
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Image
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.ItemCombinationQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Item
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.ItemCombinationQuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.ItemDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.QuizQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.User

@DataJpaTest
class UpdateICQuestionTest extends SpockTest{

    def question
    def item11
    def item12
    def item21
    def item22
    def user

    def setup() {
        createExternalCourseAndExecution()

        user = new User(USER_1_NAME, USER_1_USERNAME, USER_1_EMAIL, User.Role.STUDENT, false, AuthUser.Type.TECNICO)
        user.addCourse(externalCourseExecution)
        userRepository.save(user)

        and: 'an image'
        def image = new Image()
        image.setUrl(IMAGE_1_URL)
        image.setWidth(20)
        imageRepository.save(image)

        given: "create a question"
        question = new Question()
        question.setCourse(externalCourse)
        question.setKey(1)
        question.setTitle(QUESTION_1_TITLE)
        question.setContent(QUESTION_1_CONTENT)
        question.setStatus(Question.Status.AVAILABLE)
        question.setNumberOfAnswers(4)
        question.setNumberOfCorrect(2)
        question.setImage(image)
        def questionDetails = new ItemCombinationQuestion()
        question.setQuestionDetails(questionDetails)
        questionDetailsRepository.save(questionDetails)
        questionRepository.save(question)

        and: 'four items'
        item11 = new Item()
        item11.setContent(ITEM_1_CONTENT)
        item11.setSequence(0)
        item11.setQuestionDetails(questionDetails,1)
        itemRepository.save(item11)

        item12 = new Item()
        item12.setContent(ITEM_2_CONTENT)
        item12.setSequence(1)
        item12.setQuestionDetails(questionDetails,1)
        itemRepository.save(item12)

        item21 = new Item()
        item21.setContent(ITEM_3_CONTENT)
        item21.setSequence(0)
        item21.setQuestionDetails(questionDetails,2)
        itemRepository.save(item21)

        item22 = new Item()
        item22.setContent(ITEM_4_CONTENT)
        item22.setSequence(1)
        item22.setQuestionDetails(questionDetails,2)
        itemRepository.save(item22)

        item11.addLink(item22)
        item22.addLink(item11)
        item12.addLink(item21)
        item21.addLink(item12)
    }


// Tests for updating ic questions
   def "update an ic question"(){
       given: "a changed question"
       def questionDto = new QuestionDto(question)
       questionDto.setTitle(QUESTION_2_TITLE)
       questionDto.setContent(QUESTION_2_CONTENT)
       questionDto.setQuestionDetailsDto(new ItemCombinationQuestionDto())
       and: '2 changed items'
       def items1 = new LinkedHashSet<ItemDto>()
       def items2 = new LinkedHashSet<ItemDto>()
       def itemDto = new ItemDto(item11)
       itemDto.setContent(ITEM_2_CONTENT)
       itemDto.addLink(1);
       items1.add(itemDto)
       itemDto = new ItemDto(item22)
       itemDto.addLink(0);
       items2.add(itemDto)

       questionDto.getQuestionDetailsDto().setFirstGroup(items1)
       questionDto.getQuestionDetailsDto().setSecondGroup(items2)

       when:
       questionService.updateQuestion(question.getId(), questionDto)

       then: "the question is changed"
       questionRepository.count() == 1L
       def result = questionRepository.findAll().get(0)
       result.getId() == question.getId()
       result.getTitle() == QUESTION_2_TITLE
       result.getContent() == QUESTION_2_CONTENT
       and: 'are not changed'
       result.getStatus() == Question.Status.AVAILABLE
       result.getNumberOfAnswers() == 4
       result.getNumberOfCorrect() == 2
       result.getImage() != null
       and: "two items are changed"
       result.getQuestionDetails().getGroup(1).size() == 1
       result.getQuestionDetails().getGroup(2).size() == 1
       def resItemOne = result.getQuestionDetails().getGroup(1).stream().filter({ item -> item.getSequence() == item11.getSequence()}).findAny().orElse(null)
       resItemOne.getContent() == ITEM_2_CONTENT
       def group1iterator = result.getQuestionDetails().getGroup(1).iterator()
       def group1itemLinks = group1iterator.next().getLinks()
       group1itemLinks.size() == 1
       def resItemTwo = result.getQuestionDetails().getGroup(2).stream().filter({ item -> item.getSequence()  == item22.getSequence() }).findAny().orElse(null)
       resItemTwo.getContent() == ITEM_4_CONTENT
       def group22itemLinks = resItemTwo.getLinks()
       group22itemLinks.size() == 1
       group1itemLinks.contains(resItemTwo)
       group22itemLinks.contains(resItemOne)
   }
    
    def "update ic question with missing information"(){
        given: 'a question'
        def questionDto = new QuestionDto(question)
        questionDto.setTitle('     ')

        when:
        questionService.updateQuestion(question.getId(), questionDto)

        then: "the question an exception is thrown"
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.INVALID_TITLE_FOR_QUESTION
    }


    def "delete ic question item link"() {
        given: "a changed question"
        def questionDto = new QuestionDto(question)
        questionDto.setTitle(QUESTION_2_TITLE)
        questionDto.setContent(QUESTION_2_CONTENT)
        questionDto.setQuestionDetailsDto(new ItemCombinationQuestionDto())
        and: "two items that are connected"
        def items1 = new LinkedHashSet<ItemDto>()
        def items2 = new LinkedHashSet<ItemDto>()
        def itemDto1 = new ItemDto(item11)
        items1.add(itemDto1)
        itemDto1.removeLink(1);
        def itemDto2 = new ItemDto(item22)
        itemDto2.removeLink(0)
        items2.add(itemDto2)
        questionDto.getQuestionDetailsDto().setFirstGroup(items1)
        questionDto.getQuestionDetailsDto().setSecondGroup(items2)

        when:
        questionService.updateQuestion(question.getId(), questionDto)


        then: "the question is changed, and the link was removed"
        questionRepository.count() == 1L
        def result = questionRepository.findAll().get(0)
        result.getId() == question.getId()
        result.getTitle() == QUESTION_2_TITLE
        result.getContent() == QUESTION_2_CONTENT
        and: 'are not changed'
        result.getStatus() == Question.Status.AVAILABLE
        result.getNumberOfAnswers() == 4
        result.getNumberOfCorrect() == 2
        result.getImage() != null
        and: "the link is gone"
        result.getQuestionDetails().getGroup(1).size() == 1
        result.getQuestionDetails().getGroup(2).size() == 1
        def resItemOne = result.getQuestionDetails().getGroup(1).stream().filter({ item -> item.getSequence()== item11.getSequence()}).findAny().orElse(null)
        resItemOne.getContent() == ITEM_1_CONTENT
        def group1iterator = result.getQuestionDetails().getGroup(1).iterator()
        def group1itemLinksSize = group1iterator.next().getLinks().size()
        group1itemLinksSize == 0
        def resItemTwo = result.getQuestionDetails().getGroup(2).stream().filter({ item -> item.getSequence() == item22.getSequence()}).findAny().orElse(null)
        resItemTwo.getContent() == ITEM_4_CONTENT
        def group22itemLinks = resItemTwo.getLinks()
        group22itemLinks.size() == 0
    }

    def "create ic question item link"() {
        given: "a changed question"
        def questionDto = new QuestionDto(question)
        questionDto.setTitle(QUESTION_2_TITLE)
        questionDto.setContent(QUESTION_2_CONTENT)
        questionDto.setQuestionDetailsDto(new ItemCombinationQuestionDto())
        and: "two items that are not connected"
        def items1 = new LinkedHashSet<ItemDto>()
        def items2 = new LinkedHashSet<ItemDto>()
        def itemDto1 = new ItemDto(item11)
        itemDto1.addLink(0)
        itemDto1.removeLink(1)
        items1.add(itemDto1)
        def itemDto2 = new ItemDto(item21)
        itemDto2.addLink(0)
        items2.add(itemDto2)
        questionDto.getQuestionDetailsDto().setFirstGroup(items1)
        questionDto.getQuestionDetailsDto().setSecondGroup(items2)

        when:
        questionService.updateQuestion(question.getId(), questionDto)

        then: "the question is changed, and the link was added"
        questionRepository.count() == 1L
        def result = questionRepository.findAll().get(0)
        result.getId() == question.getId()
        result.getTitle() == QUESTION_2_TITLE
        result.getContent() == QUESTION_2_CONTENT
        and: 'are not changed'
        result.getStatus() == Question.Status.AVAILABLE
        result.getNumberOfAnswers() == 4
        result.getNumberOfCorrect() == 2
        result.getImage() != null
        and: "the link is gone"
        result.getQuestionDetails().getGroup(1).size() == 1
        result.getQuestionDetails().getGroup(2).size() == 1
        def resItemOne = result.getQuestionDetails().getGroup(1).stream().filter({ item -> item.getSequence() == item11.getSequence()}).findAny().orElse(null)
        resItemOne.getContent() == ITEM_1_CONTENT
        def group1iterator = result.getQuestionDetails().getGroup(1).iterator()
        def group1itemLinks = group1iterator.next().getLinks()
        group1itemLinks.size() == 1
        def resItemTwo = result.getQuestionDetails().getGroup(2).stream().filter({ item -> item.getSequence() == item21.getSequence()}).findAny().orElse(null)
        resItemTwo.getContent() == ITEM_3_CONTENT
        def group2iterator = result.getQuestionDetails().getGroup(2).iterator()
        def group2itemLinks = group2iterator.next().getLinks()
        group2itemLinks.size() == 1
        group1itemLinks.contains(resItemTwo)
        group2itemLinks.contains(resItemOne)
    }

    def "change ic question item link"() {
        given: "a changed question"
        def questionDto = new QuestionDto(question)
        questionDto.setTitle(QUESTION_2_TITLE)
        questionDto.setContent(QUESTION_2_CONTENT)
        questionDto.setQuestionDetailsDto(new ItemCombinationQuestionDto())
        and: "three items"
        def items1 = new LinkedHashSet<ItemDto>()
        def items2 = new LinkedHashSet<ItemDto>()
        def itemDto1 = new ItemDto(item11)
        itemDto1.removeLink(1)
        itemDto1.addLink(0)
        items1.add(itemDto1)
        def itemDto2 = new ItemDto(item21)
        def itemDto3 = new ItemDto(item22)
        itemDto2.addLink(0)
        itemDto2.removeLink(1)
        itemDto3.removeLink(0)
        items2.add(itemDto2)
        items2.add(itemDto3)
        questionDto.getQuestionDetailsDto().setFirstGroup(items1)
        questionDto.getQuestionDetailsDto().setSecondGroup(items2)

        when:
        questionService.updateQuestion(question.getId(), questionDto)

        then: "the question is changed, and the link was changed"
        questionRepository.count() == 1L
        def result = questionRepository.findAll().get(0)
        result.getId() == question.getId()
        result.getTitle() == QUESTION_2_TITLE
        result.getContent() == QUESTION_2_CONTENT
        and: 'are not changed'
        result.getStatus() == Question.Status.AVAILABLE
        result.getNumberOfAnswers() == 4
        result.getNumberOfCorrect() == 2
        result.getImage() != null
        and: "the link is gone"
        result.getQuestionDetails().getGroup(1).size() == 1
        result.getQuestionDetails().getGroup(2).size() == 2
        def resItemOne = result.getQuestionDetails().getGroup(1).stream().filter({ item -> item.getSequence() == item11.getSequence()}).findAny().orElse(null)
        resItemOne.getContent() == ITEM_1_CONTENT
        def group1iterator = result.getQuestionDetails().getGroup(1).iterator()
        def group1itemLinks = group1iterator.next().getLinks()
        group1itemLinks.size() == 1
        def resItemTwo = result.getQuestionDetails().getGroup(2).stream().filter({ item -> item.getSequence() == item21.getSequence()}).findAny().orElse(null)
        resItemTwo.getContent() == ITEM_3_CONTENT
        def resItemThree = result.getQuestionDetails().getGroup(2).stream().filter({ item -> item.getSequence() == item22.getSequence()}).findAny().orElse(null)
        resItemThree.getContent() == ITEM_4_CONTENT
        def group2iterator = result.getQuestionDetails().getGroup(2).iterator()
        def group2itemLinks = group2iterator.next().getLinks()
        def group3itemLinks = group2iterator.next().getLinks()
        group2itemLinks.size() == 1
        group3itemLinks.size() == 0
        group1itemLinks.contains(resItemTwo)
        group2itemLinks.contains(resItemOne)
    }

    def "update ic question with a repetitive link"(){
        given: "a changed question"
        def questionDto = new QuestionDto(question)
        questionDto.setTitle(QUESTION_2_TITLE)
        questionDto.setContent(QUESTION_2_CONTENT)
        questionDto.setQuestionDetailsDto(new ItemCombinationQuestionDto())
        and: "two items that are not connected"
        def items1 = new LinkedHashSet<ItemDto>()
        def items2 = new LinkedHashSet<ItemDto>()
        def itemDto1 = new ItemDto(item11)
        items1.add(itemDto1)
        def itemDto2 = new ItemDto(item22)
        items2.add(itemDto2)
        questionDto.getQuestionDetailsDto().setFirstGroup(items1)
        questionDto.getQuestionDetailsDto().setSecondGroup(items2)

        when:
        questionService.updateQuestion(question.getId(), questionDto)
        questionService.createLink(itemDto1, itemDto2)

        then: "an exception is thrown"
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.LINK_ALREADY_EXISTS
    }

    def "update ic question with a same group link"(){
        given: "a changed question"
        def questionDto = new QuestionDto(question)
        questionDto.setTitle(QUESTION_2_TITLE)
        questionDto.setContent(QUESTION_2_CONTENT)
        questionDto.setQuestionDetailsDto(new ItemCombinationQuestionDto())
        and: "two items that are not connected"
        def items1 = new LinkedHashSet<ItemDto>()
        def itemDto1 = new ItemDto(item11)
        items1.add(itemDto1)
        def itemDto2 = new ItemDto(item22)
        items1.add(itemDto2)
        questionDto.getQuestionDetailsDto().setFirstGroup(items1)
        when:
        questionService.updateQuestion(question.getId(), questionDto)
        questionService.createLink(itemDto1, itemDto2)

        then: "an exception is thrown"
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.ITEM_NOT_FOUND
    }


    def "update ic question by removing a non existing link"(){
        given: "a changed question"
        def questionDto = new QuestionDto(question)
        questionDto.setTitle(QUESTION_2_TITLE)
        questionDto.setContent(QUESTION_2_CONTENT)
        questionDto.setQuestionDetailsDto(new ItemCombinationQuestionDto())
        and: "two items that are not connected"
        def items1 = new LinkedHashSet<ItemDto>()
        def itemDto1 = new ItemDto(item11)
        items1.add(itemDto1)
        def itemDto2 = new ItemDto(item21)
        items1.add(itemDto2)
        questionDto.getQuestionDetailsDto().setFirstGroup(items1)

        when:
        questionService.updateQuestion(question.getId(), questionDto)
        questionService.createLink(itemDto1, itemDto2)

        then: "an exception is thrown"
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.ITEM_NOT_FOUND
    }
    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}
