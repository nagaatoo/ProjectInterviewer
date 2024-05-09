package ru.numbdev.interviewer;

import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.component.page.Push;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing(auditorAwareRef = "auditorAware")
@Push
public class InterviewerApplication implements AppShellConfigurator {

    public static void main(String[] args) {
        SpringApplication.run(InterviewerApplication.class, args);
    }

}
