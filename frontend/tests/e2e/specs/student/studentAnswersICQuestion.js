describe('Student Walkthrough', () => {
    beforeEach(() => {
        //create quiz
        cy.demoTeacherLogin();
        cy.createICQuestion(
            'Question Title',
            'Question',
            'Item 1',
            'Item 2'
        );
        cy.createICQuestion(
            'Question Title2',
            'Question',
            'Item 1',
            'Item 2'
        );
        cy.createQuizzWith2Questions(
            'Quiz Title',
            'Question Title',
            'Question Title2'
        );
        cy.contains('Logout').click();
    });

    it('student creates discussion', () => {
        cy.demoStudentLogin();
        cy.solveICQuizz('Quiz Title', 2, 'Item 2');
        cy.contains('Logout').click();
        Cypress.on('uncaught:exception', (err, runnable) => {
            // returning false here prevents Cypress from
            // failing the test
            return false;
        });
    });
});