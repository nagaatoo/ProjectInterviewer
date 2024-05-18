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
import ru.numbdev.interviewer.jpa.entity.CandidateEntity;
import ru.numbdev.interviewer.page.MainPage;
import ru.numbdev.interviewer.page.component.abstracts.AbstractListPage;
import ru.numbdev.interviewer.page.crud.CandidateCreatePage;
import ru.numbdev.interviewer.page.crud.CandidateUpdatePage;
import ru.numbdev.interviewer.service.crud.CandidateCrudService;
import ru.numbdev.interviewer.service.crud.UserCrudService;

@Route(value = "/candidates", layout = MainPage.class)
@PageTitle("Кандидаты")
@PermitAll
public class CandidatesListPage extends AbstractListPage<CandidateEntity> {

    private final CandidateCrudService candidateCrudService;
    private final UserCrudService userCrudService;

    protected CandidatesListPage(CandidateCrudService candidateCrudService, UserCrudService userCrudService) {
        super(CandidateEntity.class);
        this.candidateCrudService = candidateCrudService;
        this.userCrudService = userCrudService;

        initPage(true);
        addColumn(CandidateEntity::getFio, "Имя");
        addColumn(c -> c.getCandidateSolution() != null ? c.getCandidateSolution().getText() : "В процессе", "Решение");
        addColumn(CandidateEntity::getCreated, "Внесен");
        addColumn(e -> userCrudService.getByLogin(e.getCreatedBy()).getFio(), "Кто внес");
    }

    @Override
    protected DataProvider<CandidateEntity, String> buildDataProvider() {
        // 2 запроса - это дофигища для фильтра
        // подумать как поменять
        return DataProvider.fromFilteringCallbacks(
                query -> candidateCrudService.findCandidates(
                        query.getPage(),
                        query.getPageSize(),
                        getFilterValue()
                ).getContent().stream(),
                query -> (int) candidateCrudService.getSize(
                        query.getPage(),
                        query.getPageSize(),
                        getFilterValue()
                )
        );
    }

    @Override
    protected ComponentEventListener<ClickEvent<Button>> addAction() {
        return e -> UI.getCurrent().navigate(CandidateCreatePage.class);
    }

    @Override
    protected ComponentEventListener<ClickEvent<Button>> removeAction() {
        return e -> {
            candidateCrudService.save(getSelectedElement().setDeleted(true));
            refresh();
        };
    }

    @Override
    protected ComponentEventListener<ItemDoubleClickEvent<CandidateEntity>> chooseElement() {
        return e -> UI.getCurrent().navigate(CandidateUpdatePage.class, new RouteParameters(
                new RouteParam("identifier", e.getItem().getId().toString())
        ));
    }
}
