package pt.ulisboa.tecnico.socialsoftware.tutor.question.webservice

import ch.qos.logback.core.net.SyslogOutputStream
import groovy.json.JsonOutput
import groovyx.net.http.RESTClient
import groovyx.net.http.HttpResponseException
import io.swagger.v3.core.util.Json
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.auth.domain.AuthUser
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.domain.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.ItemCombinationQuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.ItemDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.MultipleChoiceQuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.OptionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.questionsubmission.domain.QuestionSubmission
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.User
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.OpenQuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.User
import com.fasterxml.jackson.databind.ObjectMapper

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CreateQuestionWebServiceIT extends SpockTest {
    @LocalServerPort
    private int port

    def course
    def courseExecution
    def teacher
    def student
    def response
    def question


    def setup() {
        restClient = new RESTClient("http://localhost:" + port)

        course = new Course(COURSE_1_NAME, Course.Type.EXTERNAL)
        courseRepository.save(course)
        courseExecution = new CourseExecution(course, COURSE_1_ACRONYM, COURSE_1_ACADEMIC_TERM, Course.Type.EXTERNAL, LOCAL_DATE_TOMORROW)
        courseExecutionRepository.save(courseExecution)

        student = new User(USER_2_NAME, USER_2_EMAIL, USER_2_EMAIL,
                User.Role.STUDENT, false, AuthUser.Type.TECNICO)
        student.authUser.setPassword(passwordEncoder.encode(USER_2_PASSWORD))
        student.addCourse(courseExecution)
        courseExecution.addUser(student)
        userRepository.save(student)

        teacher = new User(USER_1_NAME, USER_1_EMAIL, USER_1_EMAIL,
                User.Role.TEACHER, false, AuthUser.Type.EXTERNAL)

        teacher.authUser.setPassword(passwordEncoder.encode(USER_1_PASSWORD))
        teacher.addCourse(courseExecution)
        courseExecution.addUser(teacher)
        userRepository.save(teacher)
        createdUserLogin(USER_1_EMAIL, USER_1_PASSWORD)

    }

    def "request the creation of a multiple choice question with 2 right answers and 1 wrong"() {
        given: 'a question dto'
        def questionDto = new QuestionDto()
        questionDto.setQuestionDetailsDto(new MultipleChoiceQuestionDto())
        questionDto.setTitle(QUESTION_1_TITLE)
        questionDto.setContent(QUESTION_1_CONTENT)
        def optionDto = new OptionDto()
        optionDto.setContent(OPTION_1_CONTENT)
        optionDto.setCorrect(true)
        optionDto.setRelevance(2)
        def options = new ArrayList<OptionDto>()
        options.add(optionDto)
        optionDto = new OptionDto()
        optionDto.setContent(OPTION_2_CONTENT)
        optionDto.setCorrect(true)
        optionDto.setRelevance(1)
        options.add(optionDto)
        optionDto = new OptionDto()
        optionDto.setContent(OPTION_3_CONTENT)
        optionDto.setCorrect(false)
        options.add(optionDto)
        questionDto.getQuestionDetailsDto().setOptions(options)
        when:
        ObjectMapper mapper = new ObjectMapper()
        System.out.println(mapper.writeValueAsString(questionDto));

        response = restClient.post(
                path: '/questions/courses/' + course.getId(),
                body: mapper.writeValueAsString(questionDto),
                requestContentType: 'application/json'
        )

        then: "check the response status"
        response != null
        response.status == 200
        and: "if it responds with the correct multiple choice question"
        def question = response.data
        question != null
        question.title == QUESTION_1_TITLE
        question.questionDetailsDto.options.size() == 3
        question.questionDetailsDto.options.get(0).correct == question.questionDetailsDto.options.get(1).correct
        question.questionDetailsDto.options.get(2).correct == false
    }

    def "cannot create multiple choice question with no right answers"() {
        given: 'a question dto'
        def questionDto = new QuestionDto()
        questionDto.setQuestionDetailsDto(new MultipleChoiceQuestionDto())
        questionDto.setTitle(QUESTION_1_TITLE)
        questionDto.setContent(QUESTION_1_CONTENT)
        questionDto.setStatus(Question.Status.SUBMITTED.name())
        def optionDto = new OptionDto()
        optionDto.setContent(OPTION_1_CONTENT)
        optionDto.setCorrect(false)
        def options = new ArrayList<OptionDto>()
        options.add(optionDto)
        questionDto.getQuestionDetailsDto().setOptions(options)

        when:
        ObjectMapper mapper = new ObjectMapper()
        response = restClient.post(
                path: '/questions/courses/' + course.getId(),
                body: mapper.writeValueAsString(questionDto),
                requestContentType: 'application/json'
        )
        then: "catch the exception"
        def exception = thrown(HttpResponseException)
        exception.getStatusCode() == 400
    }



    def "a student cannot create a multiple choice question"(){
        given: "a multiple choice question"
        def questionDto = new QuestionDto()
        questionDto.setQuestionDetailsDto(new MultipleChoiceQuestionDto())
        questionDto.setTitle(QUESTION_1_TITLE)
        questionDto.setContent(QUESTION_1_CONTENT)
        questionDto.setStatus(Question.Status.SUBMITTED.name())
        def optionDto = new OptionDto()
        optionDto.setContent(OPTION_1_CONTENT)
        optionDto.setCorrect(true)
        optionDto.setRelevance(2)
        def options = new ArrayList<OptionDto>()
        options.add(optionDto)
        optionDto = new OptionDto()
        optionDto.setContent(OPTION_2_CONTENT)
        optionDto.setCorrect(false)
        options.add(optionDto)
        questionDto.getQuestionDetailsDto().setOptions(options)
        and: "a student login"
        createdUserLogin(USER_2_EMAIL, USER_2_PASSWORD)

        when:
        ObjectMapper mapper = new ObjectMapper()
        response = restClient.post(
                path:'/questions/courses/' + course.getId(),
                body: mapper.writeValueAsString(questionDto),
                requestContentType: 'application/json'
        )
        then: "catch the exception"
        def exception = thrown(HttpResponseException)
        exception.getStatusCode() == 403
    }

    def "request the creation of an open answer type question"(){
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
        ObjectMapper mapper = new ObjectMapper()
        response = restClient.post(
                path: '/questions/courses/' + course.getId(),
                body: mapper.writeValueAsString(questionDto),
                requestContentType: 'application/json'
        )
        then: "check the response status"
        response != null
        response.status == 200

        and: "if it responds with the correct question and its details"
        def question = response.data
        question != null
        question.title == QUESTION_1_TITLE
        question.questionDetailsDto.answer == OPEN_ANSWER_1
    }

    def "cannot request the creation of an open answer question with an empty answer"(){
        given: "a questionDto"
        def questionDto = new QuestionDto()
        questionDto.setKey(1)
        questionDto.setTitle(QUESTION_1_TITLE)
        questionDto.setContent(QUESTION_1_CONTENT)
        questionDto.setStatus(Question.Status.AVAILABLE.name())
        questionDto.setQuestionDetailsDto(new OpenQuestionDto())
        and: "no correct answer"

        when:
        ObjectMapper mapper = new ObjectMapper()
        response = restClient.post(
                path: '/questions/courses/' + course.getId(),
                body: mapper.writeValueAsString(questionDto),
                requestContentType: 'application/json'
        )
        then: "catch the exception"
        def exception = thrown(HttpResponseException)
        exception.getStatusCode() == 400
    }

    def "a student cannot create an open answer question"(){
        given: "a questionDto"
        def questionDto = new QuestionDto()
        questionDto.setKey(1)
        questionDto.setTitle(QUESTION_1_TITLE)
        questionDto.setContent(QUESTION_1_CONTENT)
        questionDto.setStatus(Question.Status.AVAILABLE.name())
        questionDto.setQuestionDetailsDto(new OpenQuestionDto())
        questionDto.getQuestionDetailsDto().setAnswer(OPEN_ANSWER_1)
        and: "a student login"
        createdUserLogin(USER_2_EMAIL, USER_2_PASSWORD)

        when:
        ObjectMapper mapper = new ObjectMapper()
        response = restClient.post(
                path: '/questions/courses/' + course.getId(),
                body: mapper.writeValueAsString(questionDto),
                requestContentType: 'application/json'
        )
        then: "catch the exception"
        def exception = thrown(HttpResponseException)
        exception.getStatusCode() == 403
    }

    def "create one to one ratio IC question for course execution"(){
        given: "a questionDto"
        def questionDto = new QuestionDto()
        questionDto.setKey(1)
        questionDto.setTitle(QUESTION_1_TITLE)
        questionDto.setContent(QUESTION_1_CONTENT)
        questionDto.setStatus(Question.Status.AVAILABLE.name())
        def questionDetailsDto = new ItemCombinationQuestionDto()
        and: 'two itemDto'
        def itemDto1 = new ItemDto()
        itemDto1.setContent(ITEM_1_CONTENT)
        itemDto1.addLink(0)
        def group_1_items = new LinkedHashSet<ItemDto>()
        group_1_items.add(itemDto1)
        questionDetailsDto.setFirstGroup(group_1_items)
        def itemDto2 = new ItemDto()
        itemDto2.setContent(ITEM_2_CONTENT)
        itemDto2.addLink(0)
        def group_2_items = new LinkedHashSet<ItemDto>()
        group_2_items.add(itemDto2)
        questionDetailsDto.setSecondGroup(group_2_items)
        questionDto.setQuestionDetailsDto(questionDetailsDto)


        when:
        def ow = new ObjectMapper().writer().withDefaultPrettyPrinter()
        response = restClient.post(
                path: '/questions/courses/' + course.getId(),
                body: ow.writeValueAsString(questionDto),
                requestContentType: 'application/json'
        )

        then: "check the response status"
        response != null
        response.status == 200
        and: "if it responds with the correct questionDetails"
        def question = response.data
        question.id != null
        question.status == Question.Status.AVAILABLE.name()
        question.title == questionDto.getTitle()
        question.content == questionDto.getContent()
        question.status == Question.Status.AVAILABLE.name()
        question.questionDetailsDto.firstGroup.get(0).content == itemDto1.getContent()
        question.questionDetailsDto.firstGroup.get(0).itemSequenceSet.get(0) == 0
        question.questionDetailsDto.secondGroup.get(0).content == itemDto2.getContent()
        question.questionDetailsDto.secondGroup.get(0).itemSequenceSet.get(0) == 0
    }

    def "teacher without permission cannot create IC question for course execution"(){
        given: "a questionDto"
        def questionDto = new QuestionDto()
        questionDto.setKey(1)
        questionDto.setTitle(QUESTION_1_TITLE)
        questionDto.setContent(QUESTION_1_CONTENT)
        questionDto.setStatus(Question.Status.AVAILABLE.name())
        def questionDetailsDto = new ItemCombinationQuestionDto()
        and: 'two itemDto'
        def itemDto1 = new ItemDto()
        itemDto1.setContent(ITEM_1_CONTENT)
        itemDto1.addLink(0)
        def group_1_items = new LinkedHashSet<ItemDto>()
        group_1_items.add(itemDto1)
        questionDetailsDto.setFirstGroup(group_1_items)
        def itemDto2 = new ItemDto()
        itemDto2.setContent(ITEM_2_CONTENT)
        itemDto2.addLink(0)
        def group_2_items = new LinkedHashSet<ItemDto>()
        group_2_items.add(itemDto2)
        questionDetailsDto.setSecondGroup(group_2_items)
        questionDto.setQuestionDetailsDto(questionDetailsDto)

        and: "a teacher not linked to the course"
        def teacher1
        teacher1 = new User(USER_3_NAME, USER_3_EMAIL, USER_3_EMAIL,
                User.Role.TEACHER, false, AuthUser.Type.EXTERNAL)

        teacher1.authUser.setPassword(passwordEncoder.encode("9876"))
        userRepository.save(teacher1)
        createdUserLogin(USER_3_EMAIL, "9876")
        when:
        def ow = new ObjectMapper().writer().withDefaultPrettyPrinter()
        response = restClient.post(
                path: '/questions/courses/' + course.getId(),
                body: ow.writeValueAsString(questionDto),
                requestContentType: 'application/json'
        )

        then:"catch the exception"
        def exception = thrown(HttpResponseException)
        exception.getStatusCode() == 403
        userRepository.deleteById(teacher1.getId())
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
