package pt.ulisboa.tecnico.socialsoftware.tutor.impexp.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.ImageDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.ItemCombinationQuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.ItemDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto

@DataJpaTest
class ImportExportItemCombinationQuestionsTest extends SpockTest{
    def questionId
    def teacher
    def setup() {
        createExternalCourseAndExecution()

        def questionDto = new QuestionDto()
        questionDto.setTitle(QUESTION_1_TITLE)
        questionDto.setContent(QUESTION_1_CONTENT)
        questionDto.setStatus(Question.Status.AVAILABLE.name())
        questionDto.setQuestionDetailsDto(new ItemCombinationQuestionDto())

        def image = new ImageDto()
        image.setUrl(IMAGE_1_URL)
        image.setWidth(20)
        questionDto.setImage(image)

        def itemDto1 = new ItemDto()
        def itemDto2 = new ItemDto()
        itemDto1.setSequence(0)
        itemDto2.setSequence(0)
        itemDto1.setContent(ITEM_1_CONTENT)
        itemDto2.setContent(ITEM_2_CONTENT)
        def items1 = new LinkedHashSet<ItemDto>()
        def items2 = new LinkedHashSet<ItemDto>()

        items1.add(itemDto1)
        items2.add(itemDto2)
        questionDto.getQuestionDetailsDto().setFirstGroup(items1)
        questionDto.getQuestionDetailsDto().setSecondGroup(items2)

        def qdto = questionService.createQuestion(externalCourse.getId(), questionDto)
        def iter = qdto.getQuestionDetailsDto().getGroup(1).iterator()
        def it1dto = iter.next()
        def iter2 = qdto.getQuestionDetailsDto().getGroup(2).iterator()
        def it2dto = iter2.next()
        questionService.createLink(it1dto, it2dto)
        questionId = qdto.getId()

    }

     def 'export to XML'() {
        when:
        def questionsXML = questionService.exportQuestionsToXml()
        print(questionsXML)
        then:
        def result = questionRepository.findAll().get(0)
        result.getQuestionDetails().getGroup(1).size() == 1
        result.getQuestionDetails().getGroup(2).size() == 1
        questionsXML != null
    }

    def "export and import to/from XML"() {
        given: 'a xml with questions'
        def questionsXml = questionService.exportQuestionsToXml()
        print questionsXml
        and: 'a clean database'
        questionService.removeQuestion(questionId)

        when:
        questionService.importQuestionsFromXml(questionsXml)

        then:
        questionRepository.findQuestions(externalCourse.getId()).size() == 1
        def result = questionRepository.findAll().get(0)
        result.getKey() == 1
        result.getTitle() == QUESTION_1_TITLE
        result.getContent() == QUESTION_1_CONTENT
        result.getStatus().name() == Question.Status.AVAILABLE.name()
        def imageResult = result.getImage()
        imageResult.getWidth() == 20
        imageResult.getUrl() == IMAGE_1_URL
        result.getQuestionDetails().getGroup(1).size() == 1
        result.getQuestionDetails().getGroup(2).size() == 1
        def itemOneResult = result.getQuestionDetails().getGroup(1).iterator().next()
        def itemTwoResult = result.getQuestionDetails().getGroup(2).iterator().next()
        itemOneResult.getSequence() + itemTwoResult.getSequence() == 0
        itemOneResult.getContent() == ITEM_1_CONTENT
        itemTwoResult.getContent() == ITEM_2_CONTENT
        def item1Links = itemOneResult.getLinks()
        def item2Links = itemTwoResult.getLinks()
        item1Links.contains(itemTwoResult)
        item2Links.contains(itemOneResult)

    }

    def "export to Latex"(){
        when:
        def questionsLatex = questionService.exportQuestionsToLatex()
        print(questionsLatex)
        then:
        questionsLatex != null
    }




    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}
