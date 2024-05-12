package ru.numbdev.interviewer.page.crud;

import java.util.UUID;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;
import org.springframework.context.ApplicationContext;
import ru.numbdev.interviewer.page.MainPage;
import ru.numbdev.interviewer.page.component.CandidateComponent;

@Route(value = "/candidates/:identifier", layout = MainPage.class)
@PageTitle("Обновить кандидата")
@PermitAll
public class CandidateUpdatePage extends VerticalLayout implements BeforeEnterObserver {

    private CandidateComponent candidateComponent;
    private final ApplicationContext context;

    public CandidateUpdatePage(ApplicationContext context) {
        this.context = context;
    }

    private void initCandidateUpdatePage(UUID candidateId) {
        candidateComponent = context.getBean(CandidateComponent.class);
        candidateComponent.init(candidateId);

        setSizeFull();
        add(candidateComponent);
        setAlignSelf(Alignment.END, candidateComponent);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        initCandidateUpdatePage(UUID.fromString(event.getLocation().getSegments().get(1)));
    }
}
