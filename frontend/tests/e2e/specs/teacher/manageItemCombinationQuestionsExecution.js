describe('Manage Item Combination Questions Walk-through', () => {
    function validateQuestion(title, content,
                              items) {
        cy.get('[data-cy="showQuestionDialog"]')
            .should('be.visible')
            .within(($ls) => {
                cy.get('.headline').should('contain', title);
                cy.get('span > p').should('contain', content);
                cy.get('span > p').should('contain', 'First Group Items:');
                cy.get('li').each(($el, index, $list) => {
                    cy.get($el).should('contain', items[index]);
                });
                cy.get('span > p').should('contain', 'Second Group Items:');
            });
    }

    function validateQuestionFull(
        title,
        content,
        firstGroup = []) {
        cy.log('Validate question with show dialog. ');

        cy.get('[data-cy="questionTitleGrid"]').first().click();

        validateQuestion(title, content, firstGroup);


        cy.get('button').contains('close').click();
    }

    before(() => {
        cy.cleanMultipleChoiceQuestionsByName('Cypress Question Example');
        cy.cleanCodeFillInQuestionsByName('Cypress Question Example');
        cy.cleanItemCombinationQuestionsByName('Cypress Question Example');
    });
    after(() => {
        cy.cleanItemCombinationQuestionsByName('Cypress Question Example');
    });

    beforeEach(() => {
        cy.demoTeacherLogin();
        cy.server();
        cy.route('GET', '/courses/*/questions').as('getQuestions');
        cy.route('GET', '/courses/*/topics').as('getTopics');
        cy.get('[data-cy="managementMenuButton"]').click();
        cy.get('[data-cy="questionsTeacherMenuButton"]').click();

        cy.wait('@getQuestions').its('status').should('eq', 200);

        cy.wait('@getTopics').its('status').should('eq', 200);
    });

    afterEach(() => {
        cy.logout();
    });

    it('Creates a new item combination question', function () {
        cy.get('button').contains('New Question').click();

        cy.get('[data-cy="createOrEditQuestionDialog"]')
            .parent()
            .should('be.visible');

        cy.get('span.headline').should('contain', 'New Question');

        cy.get(
            '[data-cy="questionTitleTextArea"]'
        ).type('Cypress IC Question Example - 01', {force: true});
        cy.get(
            '[data-cy="questionQuestionTextArea"]'
        ).type('Cypress IC Question Example - Content - 01', {force: true});

        cy.get('[data-cy="questionTypeInput"]')
            .type('item_combination', {force: true})
            .click({force: true});

        cy.wait(1000);

        cy.get('[data-cy="questionFirstGroupItemsInput"')
            .should('have.length', 1)
            .each(($el, index, $list) => {
                cy.get($el).within(($ls) => {
                    cy.get(`[data-cy="Item1${index + 1}"]`).type('Item ' + index);
                });
            });
        cy.get('[data-cy="questionSecondGroupItemsInput"')
            .should('have.length', 1)
            .each(($el, index, $list) => {
                cy.get($el).within(($ls) => {
                    cy.get(`[data-cy="Item2${index + 1}"]`).type('Item ' + index);
                });
            });
        // cy.get(`[data-cy="addLinksItemCombination"]`).type({select}).type({chips}).select('Item 0');
        cy.get(`[data-cy="addLinksItemCombination1"]`).parent().click()
        cy.get(".v-menu__content").contains("Item 0").click()

        cy.route('POST', '/courses/*/questions/').as('postQuestion');
        cy.get('button').contains('Store Links').click();
        cy.get('button').contains('Save').click();
        cy.wait('@postQuestion').its('status').should('eq', 200);

        cy.get('[data-cy="questionTitleGrid"]')
            .first()
            .should('contain', 'Cypress IC Question Example - 01');

        validateQuestionFull(
            'Cypress IC Question Example - 01',
            'Cypress IC Question Example - Content - 01',
            ['Item 0', '0: Item 0', 'Item 0']
        );
    });

    it('Can view IC question (with button)', function () {
        cy.get('tbody tr')
            .first()
            .within(($list) => {
                cy.get('button').contains('visibility').click();
            });

        validateQuestion(
            'Cypress IC Question Example - 01',
            'Cypress IC Question Example - Content - 01',
            ['Item 0', '0: Item 0', 'Item 0']
        );

        cy.get('button').contains('close').click();
    });

    it('Can view IC question (with click)', function () {
        cy.get('[data-cy="questionTitleGrid"]').first().click();

        validateQuestion(
            'Cypress IC Question Example - 01',
            'Cypress IC Question Example - Content - 01',
            ['Item 0', '0: Item 0', 'Item 0']
        );

        cy.get('button').contains('close').click();
    });

    it('Can update IC question title (with right-click)', function () {
        cy.route('PUT', '/questions/*').as('updateQuestion');

        cy.get('[data-cy="questionTitleGrid"]').first().rightclick();

        cy.get('[data-cy="createOrEditQuestionDialog"]')
            .parent()
            .should('be.visible')
            .within(($list) => {
                cy.get('span.headline').should('contain', 'Edit Question');

                cy.get('[data-cy="questionTitleTextArea"]')
                    .clear({force: true})
                    .type('Cypress IC Question Example - 01 - Edited', {force: true});

                cy.get('button').contains('Save').click();
            });

        cy.wait('@updateQuestion').its('status').should('eq', 200);

        cy.get('[data-cy="questionTitleGrid"]')
            .first()
            .should('contain', 'Cypress IC Question Example - 01 - Edited');

        validateQuestionFull(
            (title = 'Cypress IC Question Example - 01 - Edited'),
            (content = 'Cypress IC Question Example - Content - 01'),
            ['Item 0', '0: Item 0', 'Item 0']
        );
    });

    it('Can update IC question content (with button)', function () {
        cy.route('PUT', '/questions/*').as('updateQuestion');

        cy.get('tbody tr')
            .first()
            .within(($list) => {
                cy.get('button').contains('edit').click();
            });

        cy.get('[data-cy="createOrEditQuestionDialog"]')
            .parent()
            .should('be.visible')
            .within(($list) => {
                cy.get('span.headline').should('contain', 'Edit Question');

                cy.get('[data-cy="questionQuestionTextArea"]')
                    .clear({force: true})
                    .type('Cypress New Content For IC Question!', {force: true});

                cy.get('button').contains('Save').click();
            });

        cy.wait('@updateQuestion').its('status').should('eq', 200);

        validateQuestionFull(
            (title = 'Cypress IC Question Example - 01 - Edited'),
            (content = 'Cypress New Content For IC Question!'),
            ['Item 0', '0: Item 0', 'Item 0']
        );
    });

    it('Can duplicate IC question', function () {
        cy.get('tbody tr')
            .first()
            .within(($list) => {
                cy.get('button').contains('cached').click();
            });

        cy.get('[data-cy="createOrEditQuestionDialog"]')
            .parent()
            .should('be.visible');

        cy.get('span.headline').should('contain', 'New Question');

        cy.get('[data-cy="questionTitleTextArea"]')
            .should('have.value', 'Cypress IC Question Example - 01 - Edited')
            .type('{end} - DUP', {force: true});
        cy.get('[data-cy="questionQuestionTextArea"]').should(
            'have.value',
            'Cypress New Content For IC Question!'
        );

        cy.get('[data-cy="questionFirstGroupItemsInput"')
            .should('have.length', 1)
            .each(($el, index, $list) => {
                cy.get($el).within(($ls) => {
                    cy.get('textarea').should('have.value', 'Item ' + index);
                });
            });

        cy.get('[data-cy="questionSecondGroupItemsInput"')
            .should('have.length', 1)
            .each(($el, index, $list) => {
                cy.get($el).within(($ls) => {
                    cy.get('textarea').should('have.value', 'Item ' + index);
                });
            });

        cy.route('POST', '/courses/*/questions/').as('postQuestion');

        cy.get('button').contains('Save').click();

        cy.wait('@postQuestion').its('status').should('eq', 200);

        cy.get('[data-cy="questionTitleGrid"]')
            .first()
            .should('contain', 'Cypress IC Question Example - 01 - Edited - DUP');

        validateQuestionFull(
            'Cypress IC Question Example - 01 - Edited - DUP',
            'Cypress New Content For IC Question!',
            ['Item 0', '0: Item 0', 'Item 0']
        );
    });

    it('Can delete created IC question', function () {
        cy.route('DELETE', '/questions/*').as('deleteQuestion');
        cy.get('tbody tr')
            .first()
            .within(($list) => {
                cy.get('button').contains('delete').click();
            });

        cy.wait('@deleteQuestion').its('status').should('eq', 200);
    });
});