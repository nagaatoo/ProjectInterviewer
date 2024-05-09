package ru.numbdev.interviewer.page.component;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasSize;
import com.vaadin.flow.component.html.NativeLabel;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import lombok.Getter;

public class CurrentTaskComponent extends VerticalLayout {

    @Getter
    private Component current;

    public CurrentTaskComponent(String initMessage) {
        setComponent(new NativeLabel(initMessage));
    }

    public Component changeTask(Component newTask) {
        var link = current;
        this.current = newTask;
        setComponent(newTask);

        return link;
    }

    private void setComponent(Component component) {
        removeAll();
        add(component);
        var sized = (HasSize) component;

        sized.setSizeFull();
        setAlignSelf(Alignment.CENTER, component);
        setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        setSizeFull();
    }
}
