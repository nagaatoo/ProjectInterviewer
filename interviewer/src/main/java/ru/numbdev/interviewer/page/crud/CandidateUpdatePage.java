package ru.numbdev.interviewer.page.crud;

import java.util.UUID;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MultiFileMemoryBuffer;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;
import org.apache.commons.lang3.StringUtils;
import ru.numbdev.interviewer.jpa.entity.FileEntity;
import ru.numbdev.interviewer.page.MainPage;
import ru.numbdev.interviewer.page.list.CandidatesListPage;
import ru.numbdev.interviewer.service.CandidateService;
import ru.numbdev.interviewer.service.crud.CandidateCrudService;

@Route(value = "/candidates/:identifier", layout = MainPage.class)
@PageTitle("Обновить кандидата")
@PermitAll
public class CandidateUpdatePage extends VerticalLayout implements BeforeEnterObserver {

    private final CandidateService candidateService;
    private final CandidateCrudService candidateCrudService;

    private UUID candidateId;

    private TextField fioField;
    private TextArea descriptionField;
    private Button downloadFile;
    private String fileName;
    private Button createButton;

    private MultiFileMemoryBuffer buffer;

    public CandidateUpdatePage(CandidateService candidateService, CandidateCrudService candidateCrudService) {
        this.candidateService = candidateService;
        this.candidateCrudService = candidateCrudService;
    }

    private void initCandidateUpdatePage(UUID candidateId) {
        this.candidateId = candidateId;

        var entity = candidateCrudService.getById(candidateId);
        createRoomButton();
        add(page(entity.getFile()));
        add(createButton);

        fioField.setValue(entity.getFio());
        descriptionField.setValue(entity.getDescription());

        if (entity.getFile() != null) {
            fileName = entity.getFile().getFileName();
        }
    }

    private Component page(FileEntity fileEntity) {
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
        createButton = new Button("Сохранить");
        createButton.setEnabled(false);
        createButton.addClickListener(e -> {
            createInterview();

            UI.getCurrent().navigate(CandidatesListPage.class);
        });
    }

    private void createInterview() {
        var file = buffer.getOutputBuffer(fileName);
        candidateService.updateCandidate(
                candidateId,
                fioField.getValue(),
                descriptionField.getValue(),
                file.size() > 0 ? fileName : null,
                file.size() > 0 ? file.toByteArray() : null
        );
    }

    private Upload createUpload() {
        buffer = new MultiFileMemoryBuffer();
        var upload = new Upload(buffer);
        upload.setDropAllowed(false);
        upload.addSucceededListener(event -> fileName = event.getFileName());

        return upload;
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        initCandidateUpdatePage(UUID.fromString(event.getLocation().getSegments().get(1)));
    }
}
