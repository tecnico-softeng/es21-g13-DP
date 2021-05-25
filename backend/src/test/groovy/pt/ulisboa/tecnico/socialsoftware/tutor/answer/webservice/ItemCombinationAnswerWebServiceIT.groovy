package pt.ulisboa.tecnico.socialsoftware.tutor.answer.webservice

import com.fasterxml.jackson.databind.ObjectMapper
import groovyx.net.http.RESTClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuizAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.ItemCombinationStatementAnswerDetailsDto
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.StatementAnswerDto
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.StatementItemDto
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.StatementLinkDto
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.StatementQuizDto
import pt.ulisboa.tecnico.socialsoftware.tutor.auth.domain.AuthUser
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Item
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.ItemCombinationQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.QuizQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.dto.QuizDto
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.User
import pt.ulisboa.tecnico.socialsoftware.tutor.utils.DateHandler


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ItemCombinationAnswerWebServiceIT extends SpockTest {
    @LocalServerPort
    private int port

    def item11
    def item12
    def item21
    def item22
    def answerItem1
    def answerItem2
    def answerItem3
    def answerItem4
    def quizQuestion
    def sequenceLinksOK
    def itemsOK
    def quizAnswer
    def quizAnswer2
    def quiz
    def date
    def student
    def student2
    def teacher
    def response

    def itemCombinationAnswer
    def wrongAnswerItem1
    def wrongAnswerItem2
    def itemCombinationWrongAnswer


    def setup(){

        restClient = new RESTClient("http://localhost:" + port)

        createExternalCourseAndExecution()

        student = new User(USER_2_NAME, USER_2_EMAIL, USER_2_EMAIL,
                User.Role.STUDENT, false, AuthUser.Type.TECNICO)
        student.authUser.setPassword(passwordEncoder.encode(USER_2_PASSWORD))
        student.addCourse(externalCourseExecution)
        externalCourseExecution.addUser(student)
        userRepository.save(student)
        createdUserLogin(USER_2_EMAIL, USER_2_PASSWORD)

        student2 = new User(USER_1_NAME, USER_1_EMAIL, USER_1_EMAIL,
                User.Role.STUDENT, false, AuthUser.Type.TECNICO)
        student2.authUser.setPassword(passwordEncoder.encode(USER_1_PASSWORD))
        student2.addCourse(externalCourseExecution)
        externalCourseExecution.addUser(student2)
        userRepository.save(student2)

        teacher = new User(USER_3_NAME, USER_3_EMAIL, USER_3_EMAIL,
                User.Role.TEACHER, false, AuthUser.Type.TECNICO)
        teacher.authUser.setPassword(passwordEncoder.encode(USER_1_PASSWORD))
        teacher.addCourse(externalCourseExecution)
        externalCourseExecution.addUser(teacher)
        userRepository.save(teacher)

        quiz = new Quiz()
        quiz.setKey(1)
        quiz.setTitle("Quiz Title")
        quiz.setType(Quiz.QuizType.PROPOSED.toString())
        quiz.setCourseExecution(externalCourseExecution)
        quiz.setAvailableDate(DateHandler.now())
        quizRepository.save(quiz)

        def question = new Question()
        question.setKey(1)
        question.setTitle("Question Title")
        question.setCourse(externalCourse)
        def questionDetails = new ItemCombinationQuestion()
        question.setQuestionDetails(questionDetails)
        questionDetailsRepository.save(questionDetails)
        questionRepository.save(question)

        quizQuestion = new QuizQuestion(quiz, question, 0)
        quizQuestionRepository.save(quizQuestion)

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

        itemsOK = new ArrayList<Item>([item11, item12])


        answerItem1 = new Item()
        answerItem1.setContent(ITEM_1_CONTENT)
        answerItem1.setSequence(0)
        answerItem1.setGroupNumber(1)
        itemRepository.save(answerItem1)

        answerItem2 = new Item()
        answerItem2.setContent(ITEM_2_CONTENT)
        answerItem2.setSequence(1)
        answerItem2.setGroupNumber(1)
        itemRepository.save(answerItem2)

        answerItem3 = new Item()
        answerItem3.setContent(ITEM_3_CONTENT)
        answerItem3.setSequence(0)
        answerItem3.setGroupNumber(2)
        itemRepository.save(answerItem3)

        answerItem4 = new Item()
        answerItem4.setContent(ITEM_4_CONTENT)
        answerItem4.setSequence(1)
        answerItem4.setGroupNumber(2)
        itemRepository.save(answerItem4)

        answerItem1.addLink(answerItem4)
        answerItem4.addLink(answerItem1)
        answerItem2.addLink(answerItem3)
        answerItem3.addLink(answerItem2)

        date = DateHandler.now()

        quizAnswer = new QuizAnswer(student, quiz)
        quizAnswerRepository.save(quizAnswer)



    }

    def "student concludes a quiz"() {
        given: "an available quiz"
        quiz.setConclusionDate(DateHandler.now().plusDays(2))
        and: "an answer"
        def statementQuizDto = new StatementQuizDto()
        statementQuizDto.id = quiz.getId()
        statementQuizDto.quizAnswerId = quizAnswer.getId()
        def statementAnswerDto = new StatementAnswerDto()
        def itemCombinationAnswerDto = new ItemCombinationStatementAnswerDetailsDto()
        def statItem1Dto = new StatementItemDto(answerItem1)
        def statItem2Dto = new StatementItemDto(answerItem2)
        def statItem3Dto = new StatementItemDto(answerItem3)
        def statItem4Dto = new StatementItemDto(answerItem4)
        def linked1 = new ArrayList<StatementItemDto>();
        linked1.add(statItem4Dto);
        def linked2 = new ArrayList<StatementItemDto>();
        linked2.add(statItem3Dto);
        def statLink1Dto = new StatementLinkDto(statItem1Dto, linked1)
        def statLink2Dto = new StatementLinkDto(statItem2Dto, linked2)
        itemCombinationAnswerDto.setItemsId(new ArrayList<StatementLinkDto>([statLink1Dto, statLink2Dto]))
        statementAnswerDto.setAnswerDetails(itemCombinationAnswerDto)
        statementAnswerDto.setSequence(0)
        statementAnswerDto.setTimeTaken(100)
        statementAnswerDto.setQuestionAnswerId(quizAnswer.getQuestionAnswers().get(0).getId())
        statementQuizDto.getAnswers().add(statementAnswerDto)

        when:
        def mapper = new ObjectMapper().writer().withDefaultPrettyPrinter()
        System.out.println(mapper.writeValueAsString(statementAnswerDto));
        System.out.println("response")

        response = restClient.post(
                path: '/answers/' + quiz.getId() + '/submit',
                body: mapper.writeValueAsString(statementAnswerDto),
                requestContentType: 'application/json'
        )
        System.out.println(response)
        then: "check the response status"
        response != null
        response.status == 200
    }

    def "student sees quiz result"(){
        given: "an answered quiz"
        quiz.setConclusionDate(DateHandler.now().plusDays(2))
        def statementQuizDto = new StatementQuizDto(quizAnswer, true)
        def statementAnswerDto = new StatementAnswerDto()
        def itemCombinationAnswerDto = new ItemCombinationStatementAnswerDetailsDto()
        def statItem1Dto = new StatementItemDto(answerItem1)
        def statItem2Dto = new StatementItemDto(answerItem2)
        def statItem3Dto = new StatementItemDto(answerItem3)
        def statItem4Dto = new StatementItemDto(answerItem4)
        def linked1 = new ArrayList<StatementItemDto>();
        linked1.add(statItem4Dto);
        def linked2 = new ArrayList<StatementItemDto>();
        linked2.add(statItem3Dto);
        def statLink1Dto = new StatementLinkDto(statItem1Dto, linked1)
        def statLink2Dto = new StatementLinkDto(statItem2Dto, linked2)
        itemCombinationAnswerDto.setItemsId(new ArrayList<StatementLinkDto>([statLink1Dto, statLink2Dto]))
        statementAnswerDto.setAnswerDetails(itemCombinationAnswerDto)
        statementAnswerDto.setSequence(0)
        statementAnswerDto.setTimeTaken(100)
        statementAnswerDto.setQuestionAnswerId(quizAnswer.getQuestionAnswers().get(0).getId())
        statementQuizDto.getAnswers().add(statementAnswerDto)

        when:
        def mapper = new ObjectMapper().writer().withDefaultPrettyPrinter()
        System.out.println(mapper.writeValueAsString(statementQuizDto));
        System.out.println("response")

        response = restClient.post(
                path: '/answers/' + quiz.getId() + '/conclude',
                body: mapper.writeValueAsString(statementQuizDto),
                requestContentType: 'application/json'
        )

        then: "check the response status"
        response != null
        response.status == 200

    }

    def "teacher sees students quiz results"(){
        given: "a new student answering a quiz"
        createdUserLogin(USER_1_EMAIL, USER_1_PASSWORD)
        quizAnswer2 = new QuizAnswer(student2, quiz)
        quizAnswerRepository.save(quizAnswer)

        and: "a teacher login"
        createdUserLogin(USER_3_EMAIL, USER_1_PASSWORD)

        and: "multiple concluded quizzes"
        quiz.setConclusionDate(DateHandler.now().plusDays(2))
        def statementQuizDto = new StatementQuizDto(quizAnswer, true)
        def statementAnswerDto = new StatementAnswerDto()
        def itemCombinationAnswerDto = new ItemCombinationStatementAnswerDetailsDto()
        def statItem1Dto = new StatementItemDto(answerItem1)
        def statItem2Dto = new StatementItemDto(answerItem2)
        def statItem3Dto = new StatementItemDto(answerItem3)
        def statItem4Dto = new StatementItemDto(answerItem4)
        def linked1 = new ArrayList<StatementItemDto>();
        linked1.add(statItem4Dto);
        def linked2 = new ArrayList<StatementItemDto>();
        linked2.add(statItem3Dto);
        def statLink1Dto = new StatementLinkDto(statItem1Dto, linked1)
        def statLink2Dto = new StatementLinkDto(statItem2Dto, linked2)
        itemCombinationAnswerDto.setItemsId(new ArrayList<StatementLinkDto>([statLink1Dto, statLink2Dto]))
        statementAnswerDto.setAnswerDetails(itemCombinationAnswerDto)
        statementAnswerDto.setSequence(0)
        statementAnswerDto.setTimeTaken(100)
        statementAnswerDto.setQuestionAnswerId(quizAnswer.getQuestionAnswers().get(0).getId())
        statementQuizDto.getAnswers().add(statementAnswerDto)

        quiz.setConclusionDate(DateHandler.now().plusDays(2))
        def statementQuizDto1 = new StatementQuizDto(quizAnswer2, true)
        def statementAnswerDto1 = new StatementAnswerDto()
        def itemCombinationAnswerDto1 = new ItemCombinationStatementAnswerDetailsDto()
        def statItem1Dto1 = new StatementItemDto(answerItem1)
        def statItem2Dto1 = new StatementItemDto(answerItem2)
        def statItem3Dto1 = new StatementItemDto(answerItem3)
        def statItem4Dto1 = new StatementItemDto(answerItem4)
        def linked11 = new ArrayList<StatementItemDto>();
        linked1.add(statItem4Dto1);
        def linked21 = new ArrayList<StatementItemDto>();
        linked2.add(statItem3Dto1);
        def statLink1Dto1 = new StatementLinkDto(statItem1Dto1, linked11)
        def statLink2Dto1 = new StatementLinkDto(statItem2Dto1, linked21)
        itemCombinationAnswerDto1.setItemsId(new ArrayList<StatementLinkDto>([statLink1Dto1, statLink2Dto1]))
        statementAnswerDto1.setAnswerDetails(itemCombinationAnswerDto1)
        statementAnswerDto1.setSequence(0)
        statementAnswerDto1.setTimeTaken(100)
        statementAnswerDto1.setQuestionAnswerId(quizAnswer2.getQuestionAnswers().get(0).getId())
        statementQuizDto1.getAnswers().add(statementAnswerDto)

        when:
        def mapper = new ObjectMapper().writer().withDefaultPrettyPrinter()
        System.out.println(mapper.writeValueAsString(quiz.getId()));
        System.out.println("response")

        response = restClient.get(
                path: '/quizzes/' + quiz.getId() + '/answers',
                requestContentType: 'application/json'
        )

        then: "check the response status"
        response != null
        response.status == 200
    }

    /*def "teacher exports a quiz"(){
        given: "a teacher login"
        createdUserLogin(USER_3_EMAIL, USER_1_PASSWORD)

        and: "an answered quiz"
        quiz.setConclusionDate(DateHandler.now().plusDays(2))
        def statementQuizDto = new StatementQuizDto(quizAnswer, true)
        def statementAnswerDto = new StatementAnswerDto()
        def itemCombinationAnswerDto = new ItemCombinationStatementAnswerDetailsDto()
        def statItem1Dto = new StatementItemDto(answerItem1)
        def statItem2Dto = new StatementItemDto(answerItem2)
        def statItem3Dto = new StatementItemDto(answerItem3)
        def statItem4Dto = new StatementItemDto(answerItem4)
        def linked1 = new ArrayList<StatementItemDto>();
        linked1.add(statItem4Dto);
        def linked2 = new ArrayList<StatementItemDto>();
        linked2.add(statItem3Dto);
        def statLink1Dto = new StatementLinkDto(statItem1Dto, linked1)
        def statLink2Dto = new StatementLinkDto(statItem2Dto, linked2)
        itemCombinationAnswerDto.setItemsId(new ArrayList<StatementLinkDto>([statLink1Dto, statLink2Dto]))
        statementAnswerDto.setAnswerDetails(itemCombinationAnswerDto)
        statementAnswerDto.setSequence(0)
        statementAnswerDto.setTimeTaken(100)
        statementAnswerDto.setQuestionAnswerId(quizAnswer.getQuestionAnswers().get(0).getId())
        statementQuizDto.getAnswers().add(statementAnswerDto)

        when:
        def mapper = new ObjectMapper().writer().withDefaultPrettyPrinter()
        System.out.println(mapper.writeValueAsString(quiz.getId()));
        System.out.println("response")

        response = restClient.get(
                path: '/quizzes/' + quiz.getId() + '/export',
                requestContentType: 'application/json'
        )

        then: "check the response status"
        response != null
        response.status == 200
    }*/

    def cleanup() {
        userRepository.deleteAll()
        courseExecutionRepository.deleteAll()
        courseRepository.deleteAll()
        itemRepository.deleteAll()
        questionRepository.deleteAll()
        questionDetailsRepository.deleteAll()
    }

}