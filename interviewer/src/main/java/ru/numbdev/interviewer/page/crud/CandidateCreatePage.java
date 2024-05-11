package ru.numbdev.interviewer.page.crud;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MultiFileMemoryBuffer;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;
import org.apache.commons.lang3.StringUtils;
import ru.numbdev.interviewer.page.MainPage;
import ru.numbdev.interviewer.page.list.CandidatesListPage;
import ru.numbdev.interviewer.service.CandidateService;

@Route(value = "/candidates/create", layout = MainPage.class)
@PageTitle("Внести кандидата")
@PermitAll
public class CandidateCreatePage extends VerticalLayout {

    private final CandidateService candidateService;

    private TextField fioField;
    private TextArea descriptionField;
    private String fileName;
    private Button createButton;

    private MultiFileMemoryBuffer buffer;

    public CandidateCreatePage(CandidateService candidateService) {
        this.candidateService = candidateService;

        createRoomButton();
        add(page());
        add(createButton);
    }

    private Component page() {
        var vl = new VerticalLayout();

        fioField = new TextField();
        fioField.setPlaceholder("ФИО кандидата");
        fioField.setWidth("300px");
        fioField.setMinWidth("10%");
        fioField.setValueChangeMode(ValueChangeMode.EAGER);
        fioField.addValueChangeListener(e -> createButton.setEnabled(StringUtils.isNotBlank(e.getValue())));
        vl.add(fioField);

        descriptionField = new TextArea();
        descriptionField.setPlaceholder("Описание");
        descriptionField.setWidth("300px");
        descriptionField.setMinWidth("10%");
        descriptionField.setValueChangeMode(ValueChangeMode.EAGER);
        vl.add(descriptionField);

        vl.add(createUpload());

        return vl;
    }

    private void createRoomButton() {
        createButton = new Button("Создать");
        createButton.setEnabled(false);
        createButton.addClickListener(e -> {
            createInterview();

            UI.getCurrent().navigate(CandidatesListPage.class);
        });
    }

    private void createInterview() {
        var file = buffer.getOutputBuffer(fileName);
        candidateService.createCandidate(
                fioField.getValue(),
                descriptionField.getValue(),
                file.size() > 0 ? fileName : null,
                file.size() > 0 ? file.toByteArray() : null
        );
    }

    private Upload createUpload() {
        var buffer = new MultiFileMemoryBuffer();
        var upload = new Upload(buffer);

        upload.addSucceededListener(event -> fileName = event.getFileName());

        return upload;
    }
}
