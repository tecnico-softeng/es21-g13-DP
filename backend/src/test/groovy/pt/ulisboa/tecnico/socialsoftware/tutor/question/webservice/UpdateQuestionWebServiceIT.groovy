package pt.ulisboa.tecnico.socialsoftware.tutor.question.webservice


import groovy.json.JsonOutput
import groovyx.net.http.RESTClient

import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import com.fasterxml.jackson.databind.ObjectMapper
import groovyx.net.http.HttpResponseException
import groovyx.net.http.RESTClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.auth.domain.AuthUser
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.domain.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Item
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.ItemCombinationQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.ItemCombinationQuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.ItemDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.OpenQuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.OptionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.MultipleChoiceQuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.User

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UpdateQuestionWebServiceIT extends SpockTest {
    @LocalServerPort
    private int port

    def course
    def courseExecution
    def teacher
    def teacher2
    def student
    def response
    ObjectMapper mapper = new ObjectMapper()
    def item11
    def item12
    def item21
    def item22
    def iCquestion

    def setup() {
        restClient = new RESTClient("http://localhost:" + port)

        course = new Course(COURSE_1_NAME, Course.Type.EXTERNAL)
        courseRepository.save(course)
        courseExecution = new CourseExecution(course, COURSE_1_ACRONYM, COURSE_1_ACADEMIC_TERM, Course.Type.EXTERNAL, LOCAL_DATE_TOMORROW)
        courseExecutionRepository.save(courseExecution)

        teacher = new User(USER_1_NAME, USER_1_EMAIL, USER_1_EMAIL,
                User.Role.TEACHER, false, AuthUser.Type.EXTERNAL)
        teacher.authUser.setPassword(passwordEncoder.encode(USER_1_PASSWORD))
        teacher.addCourse(courseExecution)
        courseExecution.addUser(teacher)
        userRepository.save(teacher)

        teacher2 = new User(USER_3_NAME, USER_3_EMAIL, USER_3_EMAIL,
                User.Role.TEACHER, false, AuthUser.Type.EXTERNAL)
        teacher2.authUser.setPassword(passwordEncoder.encode(USER_1_PASSWORD))
        userRepository.save(teacher2)

        student = new User(USER_2_NAME, USER_2_EMAIL, USER_2_EMAIL,
                User.Role.STUDENT, false, AuthUser.Type.EXTERNAL)
        student.authUser.setPassword(passwordEncoder.encode(USER_1_PASSWORD))
        userRepository.save(student)

        createdUserLogin(USER_1_EMAIL, USER_1_PASSWORD)

        item11 = new Item()
        item11.setContent(ITEM_1_CONTENT)
        item11.setSequence(0)
        item12 = new Item()
        item12.setContent(ITEM_2_CONTENT)
        item12.setSequence(1)
        item21 = new Item()
        item21.setContent(ITEM_3_CONTENT)
        item21.setSequence(0)
        item22 = new Item()
        item22.setContent(ITEM_4_CONTENT)
        item22.setSequence(1)

        item11.addLink(item22)
        item22.addLink(item11)
        item12.addLink(item21)
        item21.addLink(item12)

    }

    def "request the change of an open answer type question"(){
        given: "an open answer question"
        def questionDto = new QuestionDto()
        questionDto.setKey(1)
        questionDto.setTitle(QUESTION_1_TITLE)
        questionDto.setContent(QUESTION_1_CONTENT)
        questionDto.setStatus(Question.Status.AVAILABLE.name())
        questionDto.setQuestionDetailsDto(new OpenQuestionDto())
        questionDto.getQuestionDetailsDto().setAnswer(OPEN_ANSWER_1)
        questionService.createQuestion(course.getId(), questionDto)

        and: "and a new question with a different correct answer"
        def newQuestionDto = new QuestionDto()
        newQuestionDto.setKey(1)
        newQuestionDto.setTitle(QUESTION_2_TITLE)
        newQuestionDto.setContent(QUESTION_2_CONTENT)
        newQuestionDto.setStatus(Question.Status.AVAILABLE.name())
        newQuestionDto.setQuestionDetailsDto(new OpenQuestionDto())
        newQuestionDto.getQuestionDetailsDto().setAnswer(OPEN_ANSWER_2)

        def questionId = questionRepository.findAll().get(0).getId()
        when:
        response = restClient.put(
                path: '/questions/' + questionId,
                body: mapper.writeValueAsString(newQuestionDto),
                requestContentType: 'application/json')

        then: "check the response status"
        response != null
        response.status == 200

        and: "if it responds with the changed question"
        def question = response.data
        question != null
        question.title == QUESTION_2_TITLE
        question.content == QUESTION_2_CONTENT
        question.questionDetailsDto.answer == OPEN_ANSWER_2
    }

    def "cannot update to a question with an empty answer"(){
        given: "a questionDto"
        def questionDto = new QuestionDto()
        questionDto.setKey(1)
        questionDto.setTitle(QUESTION_1_TITLE)
        questionDto.setContent(QUESTION_1_CONTENT)
        questionDto.setStatus(Question.Status.AVAILABLE.name())
        questionDto.setQuestionDetailsDto(new OpenQuestionDto())
        questionDto.getQuestionDetailsDto().setAnswer(OPEN_ANSWER_1)
        questionService.createQuestion(course.getId(), questionDto)

        and: "and a new question with no correct answer"
        def newQuestionDto = new QuestionDto()
        newQuestionDto.setKey(1)
        newQuestionDto.setTitle(QUESTION_2_TITLE)
        newQuestionDto.setContent(QUESTION_2_CONTENT)
        newQuestionDto.setStatus(Question.Status.AVAILABLE.name())
        newQuestionDto.setQuestionDetailsDto(new OpenQuestionDto())

        def questionId = questionRepository.findAll().get(0).getId()

        when:
        response = restClient.put(
                path: '/questions/' + questionId,
                body: mapper.writeValueAsString(newQuestionDto),
                requestContentType: 'application/json')
        then: "catch the exception"
        def exception = thrown(HttpResponseException)
        exception.getStatusCode() == 400
    }
    
    def "request update to an IC question"(){

        given: "create an IC question"
        iCquestion = new Question()
        iCquestion.setCourse(course)
        iCquestion.setKey(1)
        iCquestion.setTitle(QUESTION_1_TITLE)
        iCquestion.setContent(QUESTION_1_CONTENT)
        iCquestion.setStatus(Question.Status.AVAILABLE)
        iCquestion.setNumberOfAnswers(4)
        iCquestion.setNumberOfCorrect(2)
        def questionDetails = new ItemCombinationQuestion()
        iCquestion.setQuestionDetails(questionDetails)
        questionDetailsRepository.save(questionDetails)
        item11.setQuestionDetails(questionDetails,1)
        item12.setQuestionDetails(questionDetails,1)
        item21.setQuestionDetails(questionDetails,2)
        item22.setQuestionDetails(questionDetails,2)
        questionRepository.save(iCquestion)

        item11 = itemRepository.findAll().get(0)
        item12 = itemRepository.findAll().get(1)
        item21 = itemRepository.findAll().get(2)
        item22 = itemRepository.findAll().get(3)

        and: "a changed question"
        def newquestionDto = new QuestionDto(iCquestion)
        newquestionDto.setTitle(QUESTION_2_TITLE)
        newquestionDto.setContent(QUESTION_2_CONTENT)
        newquestionDto.setQuestionDetailsDto(new ItemCombinationQuestionDto())
        and: '2 changed items'
        def items1 = new LinkedHashSet<ItemDto>()
        def items2 = new LinkedHashSet<ItemDto>()
        def itemDto = new ItemDto(item11)
        itemDto.setContent(ITEM_2_CONTENT)
        items1.add(itemDto)
        itemDto = new ItemDto(item22)
        itemDto.setContent(ITEM_1_CONTENT)
        items2.add(itemDto)
        newquestionDto.getQuestionDetailsDto().setFirstGroup(items1)
        newquestionDto.getQuestionDetailsDto().setSecondGroup(items2)

        def questionId = questionRepository.findAll().get(0).getId()
        when:
        response = restClient.put(
                path: '/questions/' + iCquestion.getId(),
                body: mapper.writeValueAsString(newquestionDto),
                requestContentType: 'application/json')

        then:
        response != null
        response.status == 200
        and: "if it responds with the correct questionDetails"
        def question = response.data
        question.id != null
        question.status == Question.Status.AVAILABLE.name()
        question.title == newquestionDto.getTitle()
        question.content == newquestionDto.getContent()
        question.status == Question.Status.AVAILABLE.name()
        question.questionDetailsDto.firstGroup.get(0).content == ITEM_2_CONTENT
        question.questionDetailsDto.firstGroup.get(0).itemSequenceSet.get(0) == 1
        question.questionDetailsDto.secondGroup.get(0).itemSequenceSet.get(0) == 0
    }

    def "update ic question with the deletion of an item link"() {
        given: "create an IC question"
        iCquestion = new Question()
        iCquestion.setCourse(course)
        iCquestion.setKey(1)
        iCquestion.setTitle(QUESTION_1_TITLE)
        iCquestion.setContent(QUESTION_1_CONTENT)
        iCquestion.setStatus(Question.Status.AVAILABLE)
        iCquestion.setNumberOfAnswers(4)
        iCquestion.setNumberOfCorrect(2)
        def questionDetails = new ItemCombinationQuestion()
        iCquestion.setQuestionDetails(questionDetails)
        questionDetailsRepository.save(questionDetails)
        item11.setQuestionDetails(questionDetails,1)
        item12.setQuestionDetails(questionDetails,1)
        item21.setQuestionDetails(questionDetails,2)
        item22.setQuestionDetails(questionDetails,2)
        questionRepository.save(iCquestion)

        item11 = itemRepository.findAll().get(0)
        item12 = itemRepository.findAll().get(1)
        item21 = itemRepository.findAll().get(2)
        item22 = itemRepository.findAll().get(3)

        and: "a changed question"
        def newquestionDto = new QuestionDto(iCquestion)
        newquestionDto.setTitle(QUESTION_2_TITLE)
        newquestionDto.setContent(QUESTION_2_CONTENT)
        newquestionDto.setQuestionDetailsDto(new ItemCombinationQuestionDto())
        and: '2 changed items'
        def items1 = new LinkedHashSet<ItemDto>()
        def items2 = new LinkedHashSet<ItemDto>()
        def itemDto = new ItemDto(item11)
        itemDto.removeLink(1)
        itemDto.setContent(ITEM_2_CONTENT)
        items1.add(itemDto)
        itemDto = new ItemDto(item22)
        itemDto.removeLink(0)
        items2.add(itemDto)
        newquestionDto.getQuestionDetailsDto().setFirstGroup(items1)
        newquestionDto.getQuestionDetailsDto().setSecondGroup(items2)

        def questionId = questionRepository.findAll().get(0).getId()
        when:
        response = restClient.put(
                path: '/questions/' + iCquestion.getId(),
                body: mapper.writeValueAsString(newquestionDto),
                requestContentType: 'application/json')

        then:
        response != null
        response.status == 200
        and: "if it responds with the correct questionDetails"
        def question = response.data
        question.id != null
        question.status == Question.Status.AVAILABLE.name()
        question.title == newquestionDto.getTitle()
        question.content == newquestionDto.getContent()
        question.status == Question.Status.AVAILABLE.name()
        question.questionDetailsDto.firstGroup.get(0).itemSequenceSet.size() == 0
        question.questionDetailsDto.secondGroup.get(0).itemSequenceSet.size() == 0
    }

    def "update ic question with a new item link"() {
        given: "create an IC question"
        iCquestion = new Question()
        iCquestion.setCourse(course)
        iCquestion.setKey(1)
        iCquestion.setTitle(QUESTION_1_TITLE)
        iCquestion.setContent(QUESTION_1_CONTENT)
        iCquestion.setStatus(Question.Status.AVAILABLE)
        iCquestion.setNumberOfAnswers(4)
        iCquestion.setNumberOfCorrect(2)
        def questionDetails = new ItemCombinationQuestion()
        iCquestion.setQuestionDetails(questionDetails)
        questionDetailsRepository.save(questionDetails)
        item11.setQuestionDetails(questionDetails,1)
        item12.setQuestionDetails(questionDetails,1)
        item21.setQuestionDetails(questionDetails,2)
        item22.setQuestionDetails(questionDetails,2)
        questionRepository.save(iCquestion)

        item11 = itemRepository.findAll().get(0)
        item12 = itemRepository.findAll().get(1)
        item21 = itemRepository.findAll().get(2)
        item22 = itemRepository.findAll().get(3)

        and: "a changed question"
        def newquestionDto = new QuestionDto(iCquestion)
        newquestionDto.setTitle(QUESTION_2_TITLE)
        newquestionDto.setContent(QUESTION_2_CONTENT)
        newquestionDto.setQuestionDetailsDto(new ItemCombinationQuestionDto())
        and: '2 changed items'
        def items1 = new LinkedHashSet<ItemDto>()
        def items2 = new LinkedHashSet<ItemDto>()
        def itemDto1 = new ItemDto(item11)
        itemDto1.addLink(0)
        itemDto1.removeLink(1)
        itemDto1.setContent(ITEM_2_CONTENT)
        items1.add(itemDto1)
        def itemDto2 = new ItemDto(item21)
        itemDto2.addLink(0)
        itemDto2.removeLink(1)
        items2.add(itemDto2)
        newquestionDto.getQuestionDetailsDto().setFirstGroup(items1)
        newquestionDto.getQuestionDetailsDto().setSecondGroup(items2)

        def questionId = questionRepository.findAll().get(0).getId()
        when:
        response = restClient.put(
                path: '/questions/' + iCquestion.getId(),
                body: mapper.writeValueAsString(newquestionDto),
                requestContentType: 'application/json')

        then:
        response != null
        response.status == 200
        and: "if it responds with the correct questionDetails"
        def question = response.data
        question.id != null
        question.status == Question.Status.AVAILABLE.name()
        question.title == newquestionDto.getTitle()
        question.content == newquestionDto.getContent()
        question.status == Question.Status.AVAILABLE.name()
        question.questionDetailsDto.firstGroup.get(0).itemSequenceSet.size() == 1
        def sequenceSet = question.questionDetailsDto.firstGroup.get(0).itemSequenceSet
        sequenceSet.contains(0)
        def sequenceSet2 = question.questionDetailsDto.secondGroup.get(0).itemSequenceSet
        question.questionDetailsDto.secondGroup.get(0).itemSequenceSet.size() == 1
        sequenceSet2.contains(0)

    }

    def "update ic question by changing item link"() {
        given: "create an IC question"
        iCquestion = new Question()
        iCquestion.setCourse(course)
        iCquestion.setKey(1)
        iCquestion.setTitle(QUESTION_1_TITLE)
        iCquestion.setContent(QUESTION_1_CONTENT)
        iCquestion.setStatus(Question.Status.AVAILABLE)
        iCquestion.setNumberOfAnswers(4)
        iCquestion.setNumberOfCorrect(2)
        def questionDetails = new ItemCombinationQuestion()
        iCquestion.setQuestionDetails(questionDetails)
        questionDetailsRepository.save(questionDetails)
        item11.setQuestionDetails(questionDetails,1)
        item12.setQuestionDetails(questionDetails,1)
        item21.setQuestionDetails(questionDetails,2)
        item22.setQuestionDetails(questionDetails,2)
        questionRepository.save(iCquestion)

        item11 = itemRepository.findAll().get(0)
        item12 = itemRepository.findAll().get(1)
        item21 = itemRepository.findAll().get(2)
        item22 = itemRepository.findAll().get(3)

        and: "a changed question"
        def newquestionDto = new QuestionDto(iCquestion)
        newquestionDto.setTitle(QUESTION_2_TITLE)
        newquestionDto.setContent(QUESTION_2_CONTENT)
        newquestionDto.setQuestionDetailsDto(new ItemCombinationQuestionDto())
        and: '2 changed items'
        def items1 = new LinkedHashSet<ItemDto>()
        def items2 = new LinkedHashSet<ItemDto>()
        def itemDto1 = new ItemDto(item11)
        itemDto1.removeLink(1)
        itemDto1.addLink(0)
        itemDto1.setContent(ITEM_2_CONTENT)
        items1.add(itemDto1)
        def itemDto2 = new ItemDto(item21)
        itemDto2.addLink(0)
        itemDto2.removeLink(1)
        items2.add(itemDto2)
        newquestionDto.getQuestionDetailsDto().setFirstGroup(items1)
        newquestionDto.getQuestionDetailsDto().setSecondGroup(items2)

        def questionId = questionRepository.findAll().get(0).getId()
        when:
        response = restClient.put(
                path: '/questions/' + iCquestion.getId(),
                body: mapper.writeValueAsString(newquestionDto),
                requestContentType: 'application/json')

        then:
        response != null
        response.status == 200
        and: "if it responds with the correct questionDetails"
        def question = response.data
        question.id != null
        question.status == Question.Status.AVAILABLE.name()
        question.title == newquestionDto.getTitle()
        question.content == newquestionDto.getContent()
        question.status == Question.Status.AVAILABLE.name()
        question.questionDetailsDto.firstGroup.get(0).itemSequenceSet.size() == 1
        question.questionDetailsDto.firstGroup.get(0).itemSequenceSet.get(0) == 0
        question.questionDetailsDto.secondGroup.get(0).itemSequenceSet.size() == 1
        question.questionDetailsDto.secondGroup.get(0).itemSequenceSet.contains(0)
    }

    def "student cannot update an IC question"(){
        given: "create an IC question"
        createdUserLogin(USER_2_EMAIL, USER_1_PASSWORD)


        iCquestion = new Question()
        iCquestion.setCourse(course)
        iCquestion.setKey(1)
        iCquestion.setTitle(QUESTION_1_TITLE)
        iCquestion.setContent(QUESTION_1_CONTENT)
        iCquestion.setStatus(Question.Status.AVAILABLE)
        iCquestion.setNumberOfAnswers(4)
        iCquestion.setNumberOfCorrect(2)
        def questionDetails = new ItemCombinationQuestion()
        iCquestion.setQuestionDetails(questionDetails)
        questionDetailsRepository.save(questionDetails)
        item11.setQuestionDetails(questionDetails,1)
        item12.setQuestionDetails(questionDetails,1)
        item21.setQuestionDetails(questionDetails,2)
        item22.setQuestionDetails(questionDetails,2)
        questionRepository.save(iCquestion)

        item11 = itemRepository.findAll().get(0)
        item12 = itemRepository.findAll().get(1)
        item21 = itemRepository.findAll().get(2)
        item22 = itemRepository.findAll().get(3)

        and: "a changed question"
        def newquestionDto = new QuestionDto(iCquestion)
        newquestionDto.setTitle(QUESTION_2_TITLE)
        newquestionDto.setContent(QUESTION_2_CONTENT)
        newquestionDto.setQuestionDetailsDto(new ItemCombinationQuestionDto())

        when:
        response = restClient.put(
                path: '/questions/' + iCquestion.getId(),
                body: mapper.writeValueAsString(newquestionDto),
                requestContentType: 'application/json')

        then:
        def exception = thrown(HttpResponseException)
        exception.getStatusCode() == 403
    }

    def "cannot update question if not in the course"(){
        given: "any teacher not in the course"
        createdUserLogin(USER_3_EMAIL, USER_1_PASSWORD)

        and: "a multipleChoiceQuestion answer question"
        def questionDto = new QuestionDto()
        questionDto.setKey(1)
        questionDto.setTitle(QUESTION_1_TITLE)
        questionDto.setContent(QUESTION_1_CONTENT)
        questionDto.setStatus(Question.Status.AVAILABLE.name())
        questionDto.setQuestionDetailsDto(new MultipleChoiceQuestionDto())
        
        def options = new ArrayList<OptionDto>()

        def optionDto = new OptionDto()
        optionDto.setContent(OPTION_1_CONTENT)
        optionDto.setCorrect(true)
        optionDto.setRelevance(2)
        options.add(optionDto)

        optionDto = new OptionDto()
        optionDto.setContent(OPTION_2_CONTENT)
        optionDto.setCorrect(false)
        options.add(optionDto)
        questionDto.getQuestionDetailsDto().setOptions(options)

        questionService.createQuestion(course.getId(), questionDto)

        and: "and a new question"

        def newQuestionDto = new QuestionDto()
        newQuestionDto.setKey(1)
        newQuestionDto.setTitle(QUESTION_2_TITLE)
        newQuestionDto.setContent(QUESTION_2_CONTENT)
        newQuestionDto.setStatus(Question.Status.AVAILABLE.name())
        newQuestionDto.setQuestionDetailsDto(new MultipleChoiceQuestionDto())

        and: "a the same correct answer to the question"
        newQuestionDto.getQuestionDetailsDto().setOptions(options)

        def questionId = questionRepository.findAll().get(0).getId()

        when:
        response = restClient.put(
                path: '/questions/' + questionId,
                body: mapper.writeValueAsString(newQuestionDto),
                requestContentType: 'application/json')

        then:
        def exception = thrown(HttpResponseException)
        exception.getStatusCode() == 403
    }


    def "request the update of a multiple choice answer type question"(){
        given: "a multipleChoiceQuestion answer question"
        def questionDto = new QuestionDto()
        questionDto.setKey(1)
        questionDto.setTitle(QUESTION_1_TITLE)
        questionDto.setContent(QUESTION_1_CONTENT)
        questionDto.setStatus(Question.Status.AVAILABLE.name())
        questionDto.setQuestionDetailsDto(new MultipleChoiceQuestionDto())

        def options = new ArrayList<OptionDto>()

        def optionDto = new OptionDto()
        optionDto.setContent(OPTION_1_CONTENT)
        optionDto.setCorrect(true)
        optionDto.setRelevance(2)
        options.add(optionDto)

        optionDto = new OptionDto()
        optionDto.setContent(OPTION_2_CONTENT)
        optionDto.setCorrect(false)
        options.add(optionDto)
        questionDto.getQuestionDetailsDto().setOptions(options)

        questionService.createQuestion(course.getId(), questionDto)

        and: "and a new question"

        def newQuestionDto = new QuestionDto()
        newQuestionDto.setKey(1)
        newQuestionDto.setTitle(QUESTION_2_TITLE)
        newQuestionDto.setContent(QUESTION_2_CONTENT)
        newQuestionDto.setStatus(Question.Status.AVAILABLE.name())
        newQuestionDto.setQuestionDetailsDto(new MultipleChoiceQuestionDto())

        and: "a the same correct answer to the question"
        newQuestionDto.getQuestionDetailsDto().setOptions(options)

        def questionId = questionRepository.findAll().get(0).getId()

        when:
        response = restClient.put(
                path: '/questions/' + questionId,
                body: mapper.writeValueAsString(newQuestionDto),
                requestContentType: 'application/json')

        then: "check the response status"
        response != null
        response.status == 200
        and: "check if it responds with the updated question"
        def questionDtoResponse = response.data

        questionDtoResponse.title == QUESTION_2_TITLE
        questionDtoResponse.content == QUESTION_2_CONTENT

    }

    def "cannot update MultipleChoiceQuestion with invalid options"(){
        given: "a multipleChoiceQuestion answer question"
        def questionDto = new QuestionDto()
        questionDto.setKey(1)
        questionDto.setTitle(QUESTION_1_TITLE)
        questionDto.setContent(QUESTION_1_CONTENT)
        questionDto.setStatus(Question.Status.AVAILABLE.name())
        questionDto.setQuestionDetailsDto(new MultipleChoiceQuestionDto())

        def options = new ArrayList<OptionDto>()

        def optionDto = new OptionDto()
        optionDto.setContent(OPTION_1_CONTENT)
        optionDto.setCorrect(true)
        options.add(optionDto)

        optionDto = new OptionDto()
        optionDto.setContent(OPTION_2_CONTENT)
        optionDto.setCorrect(false)
        options.add(optionDto)
        questionDto.getQuestionDetailsDto().setOptions(options)

        questionService.createQuestion(course.getId(), questionDto)

        and: "and a new question"

        def newQuestionDto = new QuestionDto()
        newQuestionDto.setKey(1)
        newQuestionDto.setTitle(QUESTION_2_TITLE)
        newQuestionDto.setContent(QUESTION_2_CONTENT)
        newQuestionDto.setStatus(Question.Status.AVAILABLE.name())
        newQuestionDto.setQuestionDetailsDto(new MultipleChoiceQuestionDto())

        and: "a invalid set of options"
        options = new ArrayList<OptionDto>()

        optionDto = new OptionDto()
        optionDto.setContent(OPTION_1_CONTENT)
        optionDto.setCorrect(false)
        options.add(optionDto)

        optionDto = new OptionDto()
        optionDto.setContent(OPTION_2_CONTENT)
        optionDto.setCorrect(false)
        options.add(optionDto)

        newQuestionDto.getQuestionDetailsDto().setOptions(options)

        def questionId = questionRepository.findAll().get(0).getId()

        when:
        response = restClient.put(
                path: '/questions/' + questionId,
                body: mapper.writeValueAsString(newQuestionDto),
                requestContentType: 'application/json')

        then:
        def exception = thrown(HttpResponseException)
        exception.getStatusCode() == 400

    }

    def cleanup() {
        userRepository.deleteAll()
        courseExecutionRepository.deleteAll()
        courseRepository.deleteAll()
        itemRepository.deleteAll()
        optionRepository.deleteAll()
        questionRepository.deleteAll()
        questionDetailsRepository.deleteAll()
    }
}