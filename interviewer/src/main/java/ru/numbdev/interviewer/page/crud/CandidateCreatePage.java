package ru.numbdev.interviewer.page.crud;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;
import org.springframework.context.ApplicationContext;
import ru.numbdev.interviewer.page.MainPage;
import ru.numbdev.interviewer.page.component.CandidateComponent;

@Route(value = "/candidates/create", layout = MainPage.class)
@PageTitle("Внести кандидата")
@PermitAll
public class CandidateCreatePage extends VerticalLayout {

    private CandidateComponent candidateComponent;

    public CandidateCreatePage(ApplicationContext context) {
        candidateComponent = context.getBean(CandidateComponent.class);
        candidateComponent.init(null);

        setSizeFull();
        add(candidateComponent);
        setAlignSelf(Alignment.END, candidateComponent);
    }
}
