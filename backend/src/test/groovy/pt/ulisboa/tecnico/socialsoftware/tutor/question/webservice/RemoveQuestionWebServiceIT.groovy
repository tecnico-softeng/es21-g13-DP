package pt.ulisboa.tecnico.socialsoftware.tutor.question.webservice

import groovyx.net.http.HttpResponseException
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import com.fasterxml.jackson.databind.ObjectMapper
import groovyx.net.http.RESTClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.auth.domain.AuthUser
import groovyx.net.http.HttpResponseException
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.domain.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Image
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Item
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.ItemCombinationQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.ItemCombinationQuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.ItemDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.MultipleChoiceQuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.OpenQuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.OptionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.questionsubmission.dto.QuestionSubmissionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.User



@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class RemoveQuestionWebServiceIT extends SpockTest {
    @LocalServerPort
    private int port

    def course
    def courseExecution
    def teacher
    def response
    def question
    def student
    def teacher2

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


        student = new User(USER_2_NAME, USER_2_EMAIL, USER_2_EMAIL,
                User.Role.STUDENT, false, AuthUser.Type.TECNICO)
        student.authUser.setPassword(passwordEncoder.encode(USER_2_PASSWORD))
        student.addCourse(courseExecution)
        courseExecution.addUser(student)
        userRepository.save(student)

        teacher2 = new User(USER_3_NAME, USER_3_EMAIL, USER_3_EMAIL,
                User.Role.TEACHER, false, AuthUser.Type.EXTERNAL)
        teacher2.authUser.setPassword(passwordEncoder.encode(USER_2_PASSWORD))
        userRepository.save(teacher2)


        createdUserLogin(USER_1_EMAIL, USER_1_PASSWORD)
    }

    def "request the removal of a multiple choice question"(){
        given: 'a multiple choice question dto'
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
        optionDto.setCorrect(true)
        optionDto.setRelevance(1)
        options.add(optionDto)
        questionDto.getQuestionDetailsDto().setOptions(options)

        questionService.createQuestion(course.getId(), questionDto)
        def question = questionRepository.findAll().get(0)
        when:
        response = restClient.delete(
                path: '/questions/' + question.getId(),
                requestContentType: 'application/json')

        then: "check the response status"
        response != null
        response.status == 200
        questionRepository.findAll().isEmpty()
    }

    def "teacher cant remove an existing multiple choice question if not on the course"(){
        given: 'a multiple choice question dto'
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
        optionDto.setCorrect(true)
        optionDto.setRelevance(1)
        options.add(optionDto)
        questionDto.getQuestionDetailsDto().setOptions(options)
        and: 'a new teacher login'
        createdUserLogin(USER_3_EMAIL, USER_2_PASSWORD)

        questionService.createQuestion(course.getId(), questionDto)

        def question = questionRepository.findAll().get(0)

        when:
        response = restClient.delete(
                path: '/questions/' + question.getId(),
                requestContentType: 'application/json')

        then: "catch the exception"
        def exception = thrown(HttpResponseException)
        exception.getStatusCode() == 403
    }

    def "request the removal of an open answer type question"(){
        given: "an open answer question"
        def questionDto = new QuestionDto()
        questionDto.setKey(1)
        questionDto.setTitle(QUESTION_1_TITLE)
        questionDto.setContent(QUESTION_1_CONTENT)
        questionDto.setStatus(Question.Status.AVAILABLE.name())
        questionDto.setQuestionDetailsDto(new OpenQuestionDto())
        and: "a correct answer to the question"
        questionDto.getQuestionDetailsDto().setAnswer(OPEN_ANSWER_1)

        questionService.createQuestion(course.getId(), questionDto)

        def question = questionRepository.findAll().get(0)

        when:
        response = restClient.delete(
                path: '/questions/' + question.getId(),
                requestContentType: 'application/json')

        then: "check the response status"
        response != null
        response.status == 200
    }

    def "remove ic question for a course execution"() {
        given:
        def questionDto = new QuestionDto()
        questionDto.setKey(1)
        questionDto.setTitle(QUESTION_1_TITLE)
        questionDto.setContent(QUESTION_1_CONTENT)
        questionDto.setStatus(Question.Status.AVAILABLE.name())
        questionDto.setQuestionDetailsDto(new ItemCombinationQuestionDto())
        and: 'two itemDto'
        def itemDto1 = new ItemDto()
        itemDto1.setContent(ITEM_1_CONTENT)
        itemDto1.addLink(0)
        def group_1_items = new LinkedHashSet<ItemDto>()
        group_1_items.add(itemDto1)
        questionDto.getQuestionDetailsDto().setFirstGroup(group_1_items)
        def itemDto2 = new ItemDto()
        itemDto2.setContent(ITEM_2_CONTENT)
        itemDto2.addLink(0)
        def group_2_items = new LinkedHashSet<ItemDto>()
        group_2_items.add(itemDto2)
        questionDto.getQuestionDetailsDto().setSecondGroup(group_2_items)
        questionService.createQuestion(course.getId(), questionDto)

        question = questionRepository.findAll().get(0)

        when:
        response = restClient.delete(
                path: '/questions/' + question.getId(),
                requestContentType: 'application/json')

        then: "check the response status"
        response != null
        response.status == 200
        questionRepository.findAll().isEmpty()
    }

    def "no permission to remove ic question for a course execution"(){
        given:
        createdUserLogin(USER_2_EMAIL, USER_2_PASSWORD)

        def questionDto = new QuestionDto()
        questionDto.setKey(1)
        questionDto.setTitle(QUESTION_1_TITLE)
        questionDto.setContent(QUESTION_1_CONTENT)
        questionDto.setStatus(Question.Status.AVAILABLE.name())
        questionDto.setQuestionDetailsDto(new ItemCombinationQuestionDto())
        and: 'two itemDto'
        def itemDto1 = new ItemDto()
        itemDto1.setContent(ITEM_1_CONTENT)
        itemDto1.addLink(0)
        def group_1_items = new LinkedHashSet<ItemDto>()
        group_1_items.add(itemDto1)
        questionDto.getQuestionDetailsDto().setFirstGroup(group_1_items)
        def itemDto2 = new ItemDto()
        itemDto2.setContent(ITEM_2_CONTENT)
        itemDto2.addLink(0)
        def group_2_items = new LinkedHashSet<ItemDto>()
        group_2_items.add(itemDto2)
        questionDto.getQuestionDetailsDto().setSecondGroup(group_2_items)
        questionService.createQuestion(course.getId(), questionDto)

        question = questionRepository.findAll().get(0)

        when:
        response = restClient.delete(
                path: '/questions/' + question.getId(),
                requestContentType: 'application/json')


        then: "catch the exception"
        def exception = thrown(HttpResponseException)
        exception.getStatusCode() == 403

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
