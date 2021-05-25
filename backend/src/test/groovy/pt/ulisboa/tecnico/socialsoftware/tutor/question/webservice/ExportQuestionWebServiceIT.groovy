package pt.ulisboa.tecnico.socialsoftware.tutor.question.webservice


import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import com.fasterxml.jackson.databind.ObjectMapper
import groovyx.net.http.RESTClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.auth.domain.AuthUser
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.domain.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.MultipleChoiceQuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.ItemCombinationQuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.ItemDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.OpenQuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.OptionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.User

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ExportQuestionWebServiceIT extends SpockTest {
    @LocalServerPort
    private int port

    def course
    def courseExecution
    def teacher
    def response
    def question
    def student

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

        createdUserLogin(USER_1_EMAIL, USER_1_PASSWORD)
    }

    def "request the export of an open answer type question"(){
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

        question = questionRepository.findAll().get(0)

        and: 'prepare request response'
        restClient.handler.failure = { resp, reader ->
            [response:resp, reader:reader]
        }
        restClient.handler.success = { resp, reader ->
            [response:resp, reader:reader]
        }

        when:
        response = restClient.get(
                path: '/questions/courses/' + course.getId() +'/export',
                requestContentType: 'application/json')

        then: "check the response status"

        assert response['response'].status == 200
        assert response['reader'] != null
    }


    def "request the export of a multiple choice answer type question"(){
        given: "a multiple choise answer question"
        def questionDto = new QuestionDto()
        questionDto.setKey(1)
        questionDto.setTitle(QUESTION_1_TITLE)
        questionDto.setContent(QUESTION_1_CONTENT)
        questionDto.setStatus(Question.Status.AVAILABLE.name())
        questionDto.setQuestionDetailsDto(new MultipleChoiceQuestionDto())

        and: "a correct answer to the question"
        def options = new ArrayList<OptionDto>()

        def optionDto = new OptionDto()
        optionDto.setContent(OPTION_1_CONTENT)
        optionDto.setCorrect(true)
        optionDto.setRelevance(2)
        options.add(optionDto)

        optionDto = new OptionDto()
        optionDto.setContent(OPTION_1_CONTENT)
        optionDto.setCorrect(false)
        options.add(optionDto)

        questionDto.getQuestionDetailsDto().setOptions(options)

        questionService.createQuestion(course.getId(), questionDto)

        question = questionRepository.findAll().get(0)

        and: 'prepare request response'
        restClient.handler.failure = { resp, reader ->
            [response:resp, reader:reader]
        }
        restClient.handler.success = { resp, reader ->
            [response:resp, reader:reader]
        }

        when:
        response = restClient.get(
                path: '/questions/courses/' + course.getId() +'/export',
                requestContentType: 'application/json')

        then: "check the response status"

        assert response['response'].status == 200
        assert response['reader'] != null
    }

    def "request to export an item combination question"(){
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

        questionService.createQuestion(course.getId(), questionDto)

        and: 'prepare request response'
        restClient.handler.failure = { resp, reader ->
            [response:resp, reader:reader]
        }
        restClient.handler.success = { resp, reader ->
            [response:resp, reader:reader]
        }

        when:
        response = restClient.get(
                path: '/questions/courses/' + course.getId() +'/export',
                requestContentType: 'application/json')
        then: "check the response status"
        assert response['response'].status == 200
        assert response['reader'] != null

    }

    def "student cannot export IC question"(){
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

        questionService.createQuestion(course.getId(), questionDto)

        and: "a student login"
        createdUserLogin(USER_2_EMAIL, USER_2_PASSWORD)

        and: 'prepare request response'
        restClient.handler.failure = { resp, reader ->
            [response:resp, reader:reader]
        }
        restClient.handler.success = { resp, reader ->
            [response:resp, reader:reader]
        }

        when:
        response = restClient.get(
                path: '/questions/courses/' + course.getId() +'/export',
                requestContentType: 'application/json')
        then: "check the response status"
        assert response['response'].status == 403

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