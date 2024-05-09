package ru.numbdev.interviewer.page.component.abstracts;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import lombok.Getter;
import org.springframework.util.CollectionUtils;
import ru.numbdev.interviewer.dto.ElementValues;
import ru.numbdev.interviewer.enums.EventType;
import ru.numbdev.interviewer.enums.InterviewComponentInitType;
import ru.numbdev.interviewer.page.component.CurrentTaskComponent;
import ru.numbdev.interviewer.page.component.CustomEditor;
import ru.numbdev.interviewer.page.component.CustomRadioButtonsGroup;
import ru.numbdev.interviewer.page.component.CustomTextArea;
import ru.numbdev.interviewer.service.GlobalCacheService;

import java.util.*;

public abstract class AbstractInterviewComponent extends AbstractBuilderComponent {

    @Getter
    private final List<Component> components = new ArrayList<>();
    private CurrentTaskComponent currentTaskComponent;

    private Button previewButton;
    private Button nextButton;

    @Getter
    private int currentIdx = 0;

    protected InterviewComponentInitType type;

    @Getter
    private UUID interviewerId;
    private UUID roomId;
    protected GlobalCacheService globalCacheService;

    public void enableCacheOperations(UUID interviewId, UUID roomId, GlobalCacheService globalCacheService) {
        this.globalCacheService = globalCacheService;
        this.interviewerId = interviewId;
        this.roomId = roomId;
    }

    public void init(InterviewComponentInitType type) {
        var msg = "Тут скоро что-то будет";

        switch (type) {
            case FULL -> initFull(msg);
            case CURRENT_ONLY -> initCurrentOnly(msg);
            case READ_FULL_ONLY -> initReadOnly();
        }
    }

    private void initCurrentOnly(String msg) {
        this.type = InterviewComponentInitType.CURRENT_ONLY;
        currentTaskComponent = new CurrentTaskComponent(msg);
        add(currentTaskComponent);
        setAlignSelf(Alignment.CENTER, currentTaskComponent);
        currentTaskComponent.setSizeFull();
    }

    private void initFull(String msg) {
        this.type = InterviewComponentInitType.FULL;
        currentTaskComponent = new CurrentTaskComponent(msg);
        currentTaskComponent.setSizeFull();

        var endInterviewButton = new Button("Завершить интервью");
        endInterviewButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);
        endInterviewButton.addClickListener(e -> finish());
        initControlButtons(true);

        if (components.isEmpty()) {
            nextButton.setEnabled(false);
            previewButton.setEnabled(false);
        }

        var buttonLayout = new HorizontalLayout();
        buttonLayout.add(endInterviewButton, previewButton, nextButton);

        var ruleLayout = new VerticalLayout();
        ruleLayout.add(currentTaskComponent);
        ruleLayout.add(buttonLayout);
        ruleLayout.setAlignSelf(Alignment.END, buttonLayout);

        add(ruleLayout);
        setAlignSelf(Alignment.CENTER, ruleLayout);
        ruleLayout.setSizeFull();
    }

    private void initReadOnly() {
        this.type = InterviewComponentInitType.READ_FULL_ONLY;
        currentTaskComponent = new CurrentTaskComponent("");
        currentTaskComponent.setSizeFull();

        var buttonLayout = new HorizontalLayout();
        initControlButtons(false);
        buttonLayout.add(previewButton, nextButton);

        var ruleLayout = new VerticalLayout();
        ruleLayout.add(currentTaskComponent);
        ruleLayout.add(buttonLayout);
        ruleLayout.setAlignSelf(Alignment.END, buttonLayout);

        add(ruleLayout);
        setAlignSelf(Alignment.CENTER, ruleLayout);
        ruleLayout.setSizeFull();
        setSizeFull();
    }

    private void initControlButtons(boolean withCache) {
        if (previewButton != null || nextButton != null) {
            return;
        }

        previewButton = new Button(new Icon(VaadinIcon.ARROW_LEFT));
        nextButton = new Button(new Icon(VaadinIcon.ARROW_RIGHT));
        nextButton.addClickListener(e -> {
            currentIdx += 1;
            currentTaskComponent.changeTask(components.get(currentIdx));

            previewButton.setEnabled(true);
            if (components.size() == currentIdx + 1) {
                nextButton.setEnabled(false);
            }

            if (globalCacheService != null && withCache) {
                globalCacheService.offerEvent(interviewerId, roomId, EventType.NEXT_COMPONENT);
            }
        });
        previewButton.addClickListener(e -> {
            if (components.size() == currentIdx + 1) {
                nextButton.setEnabled(true);
            }

            currentIdx -= 1;
            currentTaskComponent.changeTask(components.get(currentIdx));

            if (currentIdx == 0) {
                previewButton.setEnabled(false);
            }


            if (globalCacheService != null && withCache) {
                globalCacheService.offerEvent(interviewerId, roomId, EventType.PREVIOUS_COMPONENT);
            }
        });

        if (components.isEmpty()) {
            nextButton.setEnabled(false);
            previewButton.setEnabled(false);
        }
    }

    public void closeInterview() {
        if (globalCacheService != null) {
            if (type == InterviewComponentInitType.FULL) {
                globalCacheService.offerEvent(interviewerId, roomId, EventType.FINISH_INTERVIEW);
            }


            globalCacheService.endInterview(interviewerId, roomId);
            globalCacheService = null;
        }

        removeAll();
        setReadOnlyComponents();

        var buttonLayout = new HorizontalLayout();
        initControlButtons(false);
        currentIdx = 0;
        currentTaskComponent.changeTask(components.getFirst());
        previewButton.setEnabled(false);
        nextButton.setEnabled(true);

        buttonLayout.add(previewButton, nextButton);

        var label = new Span("Интервью завершено");
        var ruleLayout = new VerticalLayout();
        ruleLayout.add(label);
        ruleLayout.add(currentTaskComponent);
        ruleLayout.add(buttonLayout);
        ruleLayout.setAlignSelf(Alignment.CENTER, label);
        ruleLayout.setAlignSelf(Alignment.END, buttonLayout);

        add(ruleLayout);
        setAlignSelf(Alignment.CENTER, ruleLayout);
        ruleLayout.setSizeFull();
    }

    public void setReadOnlyComponents() {
        components.forEach(c -> ((CustomComponent) c).setReadOnlyMode());
    }

    public void offerDiff(UUID elementId, Map<Integer, String> diff) {
        components
                .stream()
                .filter(e -> UUID.fromString(e.getId().get()).equals(elementId))
                .findFirst()
                .ifPresent(e -> ((EditableComponent) e).offerDiff(diff));
    }

    protected abstract void finish();

    public void setData(Map<Integer, ElementValues> data) {
        if (CollectionUtils.isEmpty(data)) {
            return;
        }

        components.clear();
        data
                .entrySet()
                .stream()
                .sorted(Map.Entry.comparingByKey())
                .map(es -> buildElement(es.getValue()))
                .forEach(components::add);

        if (type == InterviewComponentInitType.READ_FULL_ONLY) {
            currentIdx = 0;
            currentTaskComponent.changeTask(components.get(currentIdx));
            previewButton.setEnabled(false);
            nextButton.setEnabled(true);
        } else if (type == InterviewComponentInitType.FULL) {
            currentIdx = components.size() <= 1 ? 0 : components.size() - 1;
            currentTaskComponent.changeTask(components.get(currentIdx));

            previewButton.setEnabled(true);
        }
    }

    public void addNewTask(ElementValues value) {
        var component = buildElement(value);
        components.add(component);
        currentTaskComponent.changeTask(component);
        currentIdx += 1;
        addCacheToCurrentElement();
    }

    protected List<ElementValues> getInterviewResult() {
        return components
                .stream()
                .map(this::buildValueFromComponent)
                .toList();
    }

    public void addTaskElement(ElementValues value) {
        if (currentIdx > 0 || components.size() == 1) {
            previewButton.setEnabled(true);
        }

        var component = buildElement(value);
        components.add(component);
        currentTaskComponent.changeTask(component);

        if (components.size() != 1) {
            currentIdx += 1;
        }
    }

    public void changeLastTaskElement(ElementValues value) {
        if (currentIdx == 0) {
            addTaskElement(value);
            return;
        }

        var component = buildElement(value);
        components.set(currentIdx, component);
        currentTaskComponent.changeTask(component);
    }

    public Component getCurrentElement() {
        return components.get(currentIdx);
    }

    public void addCacheToCurrentElement() {
        addCacheToTargetComponent(getCurrentElement());
    }

    public void addCacheToAllElements() {
        components.forEach(this::addCacheToTargetComponent);
    }

    public void doNext() {
        if (type == InterviewComponentInitType.FULL) {
            nextButton.click();
        } else {
            currentIdx += 1;
            currentTaskComponent.changeTask(components.get(currentIdx));
        }
    }

    public void doPreview() {
        if (type == InterviewComponentInitType.FULL) {
            previewButton.click();
        } else {
            currentIdx -= 1;
            currentTaskComponent.changeTask(components.get(currentIdx));
        }
    }

    private void addCacheToTargetComponent(Component component) {
        switch (component) {
            case CustomEditor e -> registerListenerForEditor(e);
            case CustomRadioButtonsGroup rb -> registerListenerForRadioButtons(rb);
            case CustomTextArea ta -> registerListenerForTextArea(ta);
            case null, default -> System.out.println("Unknown element type");
        }
    }

    private void registerListenerForEditor(CustomEditor editor) {
        editor.addAceChangedListener(e -> {
                    if (e.isFromClient() && globalCacheService != null) {
                        globalCacheService.offerDiff(
                                interviewerId,
                                roomId,
                                editor.getIdAsUUID(),
                                editor.getDiff(e.getValue())
                        );
                    }
                }
        );
    }

    private void registerListenerForRadioButtons(CustomRadioButtonsGroup group) {
        group.addValueChangeListener(e -> {
            if (e.isFromClient() && globalCacheService != null) {
                globalCacheService.offerDiff(
                        interviewerId,
                        roomId,
                        group.getIdAsUUID(),
                        group.getDiff(e.getValue())
                );
            }
        });
    }

    private void registerListenerForTextArea(CustomTextArea textArea) {
        textArea.addValueChangeListener(e -> {
                    if (e.isFromClient() && globalCacheService != null) {
                        globalCacheService.offerDiff(
                                interviewerId,
                                roomId,
                                textArea.getIdAsUUID(),
                                textArea.getDiff(e.getValue())
                        );
                    }
                }
        );
    }
}
