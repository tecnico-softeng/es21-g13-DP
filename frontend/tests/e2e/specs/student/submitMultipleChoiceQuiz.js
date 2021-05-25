describe('Student walkthrough', () => {
  beforeEach(() => {
    cy.demoTeacherLogin();
    cy.createQuestion(
      'Question Title',
      'Question',
      'Option',
      'Option',
      'Option',
      'Correct'
    );
    cy.createQuestion(
      'Question Title2',
      'Question',
      'Option',
      'Option',
      'Option',
      'Correct'
    );
    cy.createQuizzWith2Questions(
      'Quiz Title',
      'Question Title',
      'Question Title2'
    );
    cy.contains('Logout').click();

    cy.demoStudentLogin();

    cy.get('[data-cy="submissionStudentMenuButton"]').click();
  });

  afterEach(() => {
    cy.contains('Logout').click();
  });

  it('login submits an answer', () => {
    cy.solveQuizz(
      'Quiz Title',
      2
    );
    cy.viewSolvedQuiz('Question', 'Question');
  });
});
