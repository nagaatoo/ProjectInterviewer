package ru.numbdev.interviewer.page;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.ItemDoubleClickEvent;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;
import org.springframework.beans.factory.annotation.Value;
import ru.numbdev.interviewer.jpa.entity.InterviewEntity;
import ru.numbdev.interviewer.page.component.abstracts.AbstractListPage;
import ru.numbdev.interviewer.service.crud.InterviewCrudService;

import java.text.MessageFormat;

@Route(value = "/interviews", layout = MainPage.class)
@PageTitle("Интервью")
@PermitAll
public class InterviewsListPage extends AbstractListPage<InterviewEntity> {

    @Value("${server.port}")
    private String port;

    @Value("${server.domain-name}")
    private String domainName;

    private final InterviewCrudService interviewCrudService;

    public InterviewsListPage(InterviewCrudService interviewCrudService) {
        super(InterviewEntity.class);
        this.interviewCrudService = interviewCrudService;

        initPage(false);
        addColumn(InterviewEntity::getName, "Название");
        addColumn(InterviewEntity::getSolution, "Решение");
        addColumn(InterviewEntity::getInterviewerLogin, "Интервьювер");
        addColumn(e -> MessageFormat.format("http://{0}:{1}/room/{2}", domainName, port, e.getRoom().getId()), "Ссылка");
    }

    @Override
    protected DataProvider<InterviewEntity, String> buildDataProvider() {
        // 2 запроса - это дофигища для фильтра
        // подумать как поменять
        return DataProvider.fromFilteringCallbacks(
          query -> interviewCrudService.findInterview(
                  query.getPage(),
                  query.getPageSize(),
                  getFilterValue()
          ).getContent().stream(),
          query -> (int) interviewCrudService.getSize(
                  query.getPage(),
                  query.getPageSize(),
                  getFilterValue()
          )
        );
    }

    @Override
    protected ComponentEventListener<ClickEvent<Button>> addAction() {
        return e -> {};
    }


    @Override
    protected ComponentEventListener<ClickEvent<Button>> removeAction() {
        return e -> {};
    }

    @Override
    protected ComponentEventListener<ItemDoubleClickEvent<InterviewEntity>> chooseElement() {
        return e -> {
            var url =  MessageFormat.format("http://{0}:{1}/room/{2}", domainName, port, e.getItem().getRoom().getId().toString());
            UI.getCurrent().getPage().open(url);
        };
    }
}
