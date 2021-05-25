package pt.ulisboa.tecnico.socialsoftware.tutor.question.service


import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Image
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.ItemCombinationQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Item
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question

@DataJpaTest
class RemoveICQuestionTest extends SpockTest {
    def ICquestion
    def item11
    def item12
    def item21
    def item22

    def setup() {
        createExternalCourseAndExecution()

        def image = new Image()
        image.setUrl(IMAGE_1_URL)
        image.setWidth(20)
        imageRepository.save(image)
        ICquestion = new Question()
        ICquestion.setKey(1)
        ICquestion.setTitle(QUESTION_1_TITLE)
        ICquestion.setContent(QUESTION_1_CONTENT)
        ICquestion.setStatus(Question.Status.AVAILABLE)
        ICquestion.setNumberOfAnswers(4)
        ICquestion.setNumberOfCorrect(2)
        ICquestion.setCourse(externalCourse)
        ICquestion.setImage(image)

        def questionDetails = new ItemCombinationQuestion()
        ICquestion.setQuestionDetails(questionDetails)
        questionDetailsRepository.save(questionDetails)
        questionRepository.save(questionDetails)

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


    }
    def "remove an IC question with links"(){
        when:
        item11.addLink(item22)
        item12.addLink(item21)
        questionService.removeQuestion(ICquestion.getId())

        then: "the question is removed"
        questionRepository.count() == 0L
        imageRepository.count() == 0L
        itemRepository.count() == 0L
    }

    def "remove an ICQuestion with no links"(){
        when:
        questionService.removeQuestion(ICquestion.getId())

        then: "the question is removed"
        questionRepository.count() == 0L
        imageRepository.count() == 0L
        itemRepository.count() == 0L
    }


    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}

}

