package ru.numbdev.interviewer.page;

import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

@Route("/login")
@PageTitle("Interview login")
@AnonymousAllowed
public class LoginPage extends HorizontalLayout {

    public LoginPage() {
        super();

        var loginForm = new LoginForm();
        loginForm.getElement().executeJs("this.$.vaadinLoginUsername.value = $0;", "admin");
        loginForm.getElement().executeJs("this.$.vaadinLoginPassword.value = $0;", "123");
        loginForm.setAction("login");

        add(loginForm);
        setSizeFull();
        setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
    }
}
