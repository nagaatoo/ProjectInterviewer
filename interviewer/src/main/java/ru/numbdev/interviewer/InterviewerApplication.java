package ru.numbdev.interviewer;

import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.component.page.Push;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableJpaAuditing(auditorAwareRef = "auditorAware")
@Push
@EnableScheduling
public class InterviewerApplication implements AppShellConfigurator {

    public static void main(String[] args) {
        SpringApplication.run(InterviewerApplication.class, args);
    }

}
