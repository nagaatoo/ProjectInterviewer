package ru.numbdev.interviewer.page;

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
import ru.numbdev.interviewer.jpa.entity.TemplateEntity;
import ru.numbdev.interviewer.page.component.abstracts.AbstractListPage;
import ru.numbdev.interviewer.service.crud.TemplateCrudService;

@Route(value = "/templates", layout = MainPage.class)
@PageTitle("Шаблоны")
@PermitAll
public class TemplatesListPage extends AbstractListPage<TemplateEntity> {

    private final TemplateCrudService templateCrudService;

    public TemplatesListPage(TemplateCrudService templateCrudService) {
        super(TemplateEntity.class);
        this.templateCrudService = templateCrudService;
        initPage();
        addColumn(TemplateEntity::getName, "Название");
        addColumn(TemplateEntity::getOwner, "Автор");
    }

    @Override
    protected DataProvider<TemplateEntity, String> buildDataProvider() {
        // 2 запроса - это дофигища для фильтра
        // подумать как поменять
        // Исполементировать свой CallbackDataProvider
        return DataProvider.fromFilteringCallbacks(
                query -> templateCrudService.findTemplates(
                        query.getPage(),
                        query.getPageSize(),
                        getFilterValue()
                ).getContent().stream(),
                query -> (int) templateCrudService.getSize(
                        query.getPage(),
                        query.getPageSize(),
                        getFilterValue()
                )
        );
    }

    @Override
    protected ComponentEventListener<ClickEvent<Button>> addAction() {
        return e -> UI.getCurrent().navigate(TemplateCreatePage.class);
    }

    @Override
    protected ComponentEventListener<ClickEvent<Button>> removeAction() {
        return e -> {
            templateCrudService.delete(getSelectedElement());
            refresh();
        };
    }

    @Override
    protected ComponentEventListener<ItemDoubleClickEvent<TemplateEntity>> chooseElement() {
        return e -> UI.getCurrent().navigate(TemplateUpdatePage.class, new RouteParameters(
                new RouteParam("identifier", e.getItem().getId().toString())
        ));
    }
}
