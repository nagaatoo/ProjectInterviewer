package ru.numbdev.interviewer.component;

import ru.numbdev.interviewer.dto.Message;
import ru.numbdev.interviewer.page.component.abstracts.CustomComponent;

import java.util.UUID;

public interface RoomObserver extends CustomComponent {

    UUID getInterviewId();
    void doAction(Message message);
}
