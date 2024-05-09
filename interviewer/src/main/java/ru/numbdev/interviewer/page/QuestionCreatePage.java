package ru.numbdev.interviewer.page;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;
import org.springframework.context.ApplicationContext;
import ru.numbdev.interviewer.enums.QuestionComponentType;
import ru.numbdev.interviewer.page.component.QuestionComponent;

@Route(value = "/question/create", layout = MainPage.class)
@PageTitle("Создать опросник")
@PermitAll
public class QuestionCreatePage extends VerticalLayout {

    private QuestionComponent questionComponent;

    public QuestionCreatePage(ApplicationContext context) {
        questionComponent = context.getBean(QuestionComponent.class);
        questionComponent.init(QuestionComponentType.EDITABLE, null);

        setSizeFull();
        add(questionComponent);
        setAlignSelf(Alignment.END, questionComponent);
    }
}
