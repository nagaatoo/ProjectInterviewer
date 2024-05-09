package ru.numbdev.interviewer.page;

import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;
import org.springframework.context.ApplicationContext;
import ru.numbdev.interviewer.enums.QuestionComponentType;
import ru.numbdev.interviewer.page.component.QuestionComponent;

import java.util.UUID;

@Route(value = "/question/:identifier", layout = MainPage.class)
@PageTitle("Обновить опросник")
@PermitAll
public class QuestionUpdatePage extends HorizontalLayout implements BeforeEnterObserver {

    private QuestionComponent questionComponent;
    private final ApplicationContext context;

    public QuestionUpdatePage(ApplicationContext context) {
        this.context = context;
    }

    private void initTemplateUpdatePage(UUID questionId) {
        questionComponent = context.getBean(QuestionComponent.class);
        questionComponent.init(QuestionComponentType.EDITABLE, questionId);

        setSizeFull();
        add(questionComponent);
        setAlignSelf(FlexComponent.Alignment.END, questionComponent);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        initTemplateUpdatePage(UUID.fromString(event.getLocation().getSegments().get(1)));
    }
}
