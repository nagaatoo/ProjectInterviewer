package ru.numbdev.interviewer.page.component.abstracts;

import java.util.UUID;

import com.vaadin.flow.component.Component;

public interface CustomComponent {
    @SuppressWarnings("all")
    default UUID getIdAsUUID() {
        return UUID.fromString(((Component) this).getId().get());
    }
    void setReadOnlyMode();
}
