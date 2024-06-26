package ru.numbdev.interviewer.page.list;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.ItemDoubleClickEvent;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteParam;
import com.vaadin.flow.router.RouteParameters;
import jakarta.annotation.security.PermitAll;
import ru.numbdev.interviewer.jpa.entity.QuestionnaireEntity;
import ru.numbdev.interviewer.page.MainPage;
import ru.numbdev.interviewer.page.crud.QuestionCreatePage;
import ru.numbdev.interviewer.page.crud.QuestionUpdatePage;
import ru.numbdev.interviewer.page.component.abstracts.AbstractListPage;
import ru.numbdev.interviewer.service.crud.QuestionsCrudService;
import ru.numbdev.interviewer.service.crud.UserCrudService;

@Route(value = "/questions", layout = MainPage.class)
@PageTitle("Опросники")
@PermitAll
public class QuestionsListPage extends AbstractListPage<QuestionnaireEntity> {

    private final QuestionsCrudService questionsCrudService;
    private final UserCrudService userCrudService;

    public QuestionsListPage(QuestionsCrudService questionsCrudService, UserCrudService userCrudService) {
        super(QuestionnaireEntity.class);
        this.questionsCrudService = questionsCrudService;
        this.userCrudService = userCrudService;

        initPage();
        addColumn(QuestionnaireEntity::getName, "Название");
        addColumn(e -> userCrudService.getByLogin(e.getAuthor()).getFio(), "Автор");
    }

    @Override
    protected ComponentEventListener<ClickEvent<Button>> addAction() {
        return e -> UI.getCurrent().navigate(QuestionCreatePage.class);
    }

    @Override
    protected ComponentEventListener<ClickEvent<Button>> removeAction() {
        return e -> {
            questionsCrudService.delete(getSelectedElement());
            refresh();
        };
    }

    @Override
    protected ComponentEventListener<ItemDoubleClickEvent<QuestionnaireEntity>> chooseElement() {
        return e -> UI.getCurrent().navigate(QuestionUpdatePage.class, new RouteParameters(
                new RouteParam("identifier", e.getItem().getId().toString())
        ));
    }

    @Override
    protected DataProvider<QuestionnaireEntity, String> buildDataProvider() {
        // 2 запроса - это дофигища для фильтра
        // подумать как поменять
        // Исполементировать свой CallbackDataProvider
        return DataProvider.fromFilteringCallbacks(
                query -> questionsCrudService.findQuestionary(
                        query.getPage(),
                        query.getPageSize(),
                        getFilterValue()
                ).getContent().stream(),
                query -> (int) questionsCrudService.getSize(
                        query.getPage(),
                        query.getPageSize(),
                        getFilterValue()
                )
        );
    }
}
