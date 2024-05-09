package ru.numbdev.interviewer.page;

import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;
import org.springframework.context.ApplicationContext;
import ru.numbdev.interviewer.page.component.TemplateComponent;

import java.util.UUID;

@Route(value = "/template/:identifier", layout = MainPage.class)
@PageTitle("Обновить шаблон")
@PermitAll
public class TemplateUpdatePage extends HorizontalLayout implements BeforeEnterObserver {

    private TemplateComponent templateComponent;
    private final ApplicationContext context;

    public TemplateUpdatePage(ApplicationContext context) {
        this.context = context;
    }

    private void initTemplateUpdatePage(UUID templateId) {
        templateComponent = context.getBean(TemplateComponent.class);
        templateComponent.init(true, templateId);

        setSizeFull();
        add(templateComponent);
        setAlignSelf(Alignment.END, templateComponent);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        initTemplateUpdatePage(UUID.fromString(event.getLocation().getSegments().get(1)));
    }
}
