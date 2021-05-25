package groovy.pt.ulisboa.tecnico.socialsoftware.tutor.answer.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.ItemCombinationAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuestionAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuizAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.ItemCombinationStatementAnswerDetailsDto
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.StatementAnswerDto
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.StatementQuizDto
import pt.ulisboa.tecnico.socialsoftware.tutor.auth.domain.AuthUser
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Item
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.ItemCombinationQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Option
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Topic
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.QuizQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.User
import pt.ulisboa.tecnico.socialsoftware.tutor.utils.DateHandler

@DataJpaTest
class ItemCombinationQuizTest extends SpockTest{
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
    def quiz
    def date
    def user
    def itemCombinationAnswer
    def wrongAnswerItem1
    def wrongAnswerItem2
    def itemCombinationWrongAnswer

    def setup() {
        createExternalCourseAndExecution()

        user = new User(USER_1_NAME, USER_1_USERNAME, USER_1_EMAIL, User.Role.STUDENT, false, AuthUser.Type.TECNICO)
        user.addCourse(externalCourseExecution)
        userRepository.save(user)

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
        def answerItems = new ArrayList<>([answerItem1, answerItem2])

        date = DateHandler.now()

        quizAnswer = new QuizAnswer(user, quiz)
        quizAnswerRepository.save(quizAnswer)

        itemCombinationAnswer = new ItemCombinationAnswer(new QuestionAnswer(), itemsOK)
        itemCombinationAnswer.setAnswerItems(answerItems)
        itemCombinationAnswer.setCorrectItems(new ArrayList<Item>([item11, item12]))


        wrongAnswerItem1 = new Item()
        wrongAnswerItem1.setContent(ITEM_1_CONTENT)
        wrongAnswerItem1.setSequence(0)
        wrongAnswerItem1.setGroupNumber(1)
        itemRepository.save(wrongAnswerItem1)

        wrongAnswerItem2 = new Item()
        wrongAnswerItem2.setContent(ITEM_2_CONTENT)
        wrongAnswerItem2.setSequence(1)
        wrongAnswerItem2.setGroupNumber(1)
        itemRepository.save(wrongAnswerItem2)
        def wrongAnswerItems = new ArrayList<>([wrongAnswerItem1, wrongAnswerItem2])

        itemCombinationWrongAnswer = new ItemCombinationAnswer(new QuestionAnswer(), itemsOK)
        itemCombinationWrongAnswer.setAnswerItems(wrongAnswerItems)
        itemCombinationWrongAnswer.setCorrectItems(new ArrayList<Item>([item11, item12]))

        //questionAnswerRepository.save(itemCombinationAnswer)
    }

    /*
    criar quiz answer e verificar se está na base de dados
    criar quiz answer com aluno que está associado ao quiz
    criar quiz answer com aluno que nao esteja associado ao quiz
    criar quiz answer com respostas todas corretas
    criar quiz answer com respostas erradas
    aluno tentar exportar um quiz (caso de erro?)
    professor exportar um quiz
    */

    def 'conclude quiz IC answer'(){
        given:  'a completed quiz'
        quizAnswer.completed = true
        and: 'an answer'
        def statementQuizDto = new StatementQuizDto()
        statementQuizDto.id = quiz.getId()
        statementQuizDto.quizAnswerId = quizAnswer.getId()
        def statementAnswerDto = new StatementAnswerDto()
        def itemCombinationAnswerDto = new ItemCombinationStatementAnswerDetailsDto(itemCombinationAnswer)

        statementAnswerDto.setAnswerDetails(itemCombinationAnswerDto)
        statementAnswerDto.setSequence(0)
        statementAnswerDto.setTimeTaken(100)
        statementAnswerDto.setQuestionAnswerId(quizAnswer.getQuestionAnswers().get(0).getId())
        statementQuizDto.getAnswers().add(statementAnswerDto)

        when:
        def correctAnswers = answerService.concludeQuiz(statementQuizDto)

        then: 'nothing occurs'
        quizAnswer.getAnswerDate() == null
        questionAnswerRepository.findAll().size() == 1
        def questionAnswer = questionAnswerRepository.findAll().get(0)
        questionAnswer.getQuizAnswer() == quizAnswer
        quizAnswer.getQuestionAnswers().contains(questionAnswer)
        questionAnswer.getQuizQuestion() == quizQuestion
        quizQuestion.getQuestionAnswers().contains(questionAnswer)
        questionAnswer.getAnswerDetails() == null
        and: 'the return value is OK'
        correctAnswers.size() == 0
    }

    def 'conclude quiz before conclusion date'() {
        given: 'a quiz with future conclusionDate'
        quiz.setConclusionDate(DateHandler.now().plusDays(2))
        and: 'an answer'
        def statementQuizDto = new StatementQuizDto()
        statementQuizDto.id = quiz.getId()
        statementQuizDto.quizAnswerId = quizAnswer.getId()
        def statementAnswerDto = new StatementAnswerDto()
        def itemCombinationAnswerDto = new ItemCombinationStatementAnswerDetailsDto(itemCombinationAnswer)

        statementAnswerDto.setAnswerDetails(itemCombinationAnswerDto)
        statementAnswerDto.setSequence(0)
        statementAnswerDto.setTimeTaken(100)
        statementAnswerDto.setQuestionAnswerId(quizAnswer.getQuestionAnswers().get(0).getId())
        statementQuizDto.getAnswers().add(statementAnswerDto)

        when:
        def correctAnswers = answerService.concludeQuiz(statementQuizDto)

        then: 'the value is createQuestion and persistent'
        quizAnswer.isCompleted()
        questionAnswerRepository.findAll().size() == 1
        def questionAnswer = questionAnswerRepository.findAll().get(0)
        questionAnswer.getQuizAnswer() == quizAnswer
        quizAnswer.getQuestionAnswers().contains(questionAnswer)
        questionAnswer.getQuizQuestion() == quizQuestion
        quizQuestion.getQuestionAnswers().contains(questionAnswer)
        ((ItemCombinationAnswer) questionAnswer.getAnswerDetails()).getAnswerItems().get(0).getContent().equals(item11.getContent())
        ((ItemCombinationAnswer) questionAnswer.getAnswerDetails()).getAnswerItems().get(1).getContent().equals(item12.getContent())
        item11.getQuestionAnswers().contains(questionAnswer.getAnswerDetails())
        item12.getQuestionAnswers().contains(questionAnswer.getAnswerDetails())
        and: 'the return value is OK'
        correctAnswers.size() == 1
        def correctAnswerDto = correctAnswers.get(0)
        correctAnswerDto.getSequence() == 0
        correctAnswerDto.getCorrectAnswerDetails().getCorrectLinksSequence(0) != null
        correctAnswerDto.getCorrectAnswerDetails().getCorrectLinksSequence(1) != null
        questionAnswer.isCorrect() == true
    }


    def 'conclude quiz with wrong answer before conclusion date'() {
        given: 'a quiz with future conclusionDate'
        quiz.setConclusionDate(DateHandler.now().plusDays(2))
        and: 'a wrong answer'
        def statementQuizDto = new StatementQuizDto()
        statementQuizDto.id = quiz.getId()
        statementQuizDto.quizAnswerId = quizAnswer.getId()
        def statementAnswerDto = new StatementAnswerDto()
        def itemCombinationAnswerDto = new ItemCombinationStatementAnswerDetailsDto(itemCombinationWrongAnswer)

        statementAnswerDto.setAnswerDetails(itemCombinationAnswerDto)
        statementAnswerDto.setSequence(0)
        statementAnswerDto.setTimeTaken(100)
        statementAnswerDto.setQuestionAnswerId(quizAnswer.getQuestionAnswers().get(0).getId())
        statementQuizDto.getAnswers().add(statementAnswerDto)

        when:
        def correctAnswers = answerService.concludeQuiz(statementQuizDto)

        then: 'the value is createQuestion and persistent'
        quizAnswer.isCompleted()
        questionAnswerRepository.findAll().size() == 1
        def questionAnswer = questionAnswerRepository.findAll().get(0)
        questionAnswer.getQuizAnswer() == quizAnswer
        quizAnswer.getQuestionAnswers().contains(questionAnswer)
        questionAnswer.getQuizQuestion() == quizQuestion
        quizQuestion.getQuestionAnswers().contains(questionAnswer)
        ((ItemCombinationAnswer) questionAnswer.getAnswerDetails()).getAnswerItems().get(0).getContent().equals(item11.getContent())
        ((ItemCombinationAnswer) questionAnswer.getAnswerDetails()).getAnswerItems().get(1).getContent().equals(item12.getContent())
        item11.getQuestionAnswers().contains(questionAnswer.getAnswerDetails())
        item12.getQuestionAnswers().contains(questionAnswer.getAnswerDetails())
        and: 'the return value is OK'
        correctAnswers.size() == 1
        questionAnswer.isCorrect() == false

    }
 /*   def 'create quiz IC answer with all correct answers'(){expect:true}

    def 'create quiz IC answer with some correct answers'(){expect:false}

    def 'create quiz IC answer with somme correct answers and on wrong'(){expect:false}

    def 'create quiz IC answer with all wrong answers'(){expect:false}

    def 'export quiz with IC questions'(){expect:false}

    def 'import quiz with IC questions'(){expect:false} */

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}
