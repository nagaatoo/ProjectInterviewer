package ru.numbdev.interviewer.page;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;
import org.springframework.context.ApplicationContext;
import ru.numbdev.interviewer.page.component.TemplateComponent;

@Route(value = "/template/create", layout = MainPage.class)
@PageTitle("Создать шаблон")
@PermitAll
public class TemplateCreatePage extends VerticalLayout {

    private TemplateComponent templateComponent;

    public TemplateCreatePage(ApplicationContext context) {
        templateComponent = context.getBean(TemplateComponent.class);
        templateComponent.init(true, null);

        setSizeFull();
        add(templateComponent);
        setAlignSelf(Alignment.END, templateComponent);
    }

}
