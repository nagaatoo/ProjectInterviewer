package ru.numbdev.interviewer.page.component;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MultiFileMemoryBuffer;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.server.StreamResource;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import ru.numbDev.common.dto.ElementValues;
import ru.numbDev.common.utils.DateTimeUtils;
import ru.numbdev.interviewer.enums.CandidateSolution;
import ru.numbdev.interviewer.jpa.entity.FileEntity;
import ru.numbdev.interviewer.jpa.entity.InterviewEntity;
import ru.numbdev.interviewer.page.list.CandidatesListPage;
import ru.numbdev.interviewer.service.CandidateService;
import ru.numbdev.interviewer.service.FileService;
import ru.numbdev.interviewer.service.crud.CandidateCrudService;
import ru.numbdev.interviewer.service.crud.InterviewCrudService;
import ru.numbdev.interviewer.service.crud.UserCrudService;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@RequiredArgsConstructor
public class CandidateComponent extends VerticalLayout {

    @Value("${server.port}")
    private String port;

    @Value("${server.domain-name}")
    private String domainName;

    private final CandidateService candidateService;
    private final CandidateCrudService candidateCrudService;
    private final InterviewCrudService interviewCrudService;
    private final FileService fileService;
    private final UserCrudService userCrudService;

    private UUID candidateId;
    private boolean isNew;

    private TextField fioField;
    private TextArea descriptionField;
    private Select<String> solution;
    private Button createButton;

    private String fileName;
    private String fileLink;

    private MultiFileMemoryBuffer buffer;

    public void init(UUID candidateId) {
        this.candidateId = candidateId;
        isNew = candidateId == null;

        add(page());
        FileEntity fileEntity = null;
        if (!isNew) {
            var entity = candidateCrudService.getById(candidateId);
            fileEntity = entity.getFile();
            fioField.setValue(entity.getFio());
            descriptionField.setValue(entity.getDescription());

            if (entity.getFile() != null) {
                fileName = entity.getFile().getFileName();
            }

            add(createSolution(entity.getCandidateSolution()));

            if (!CollectionUtils.isEmpty(entity.getInterviews())) {
                add(
                        createInterviewList(
                                Optional.ofNullable(entity.getInterviews())
                                        .orElseGet(List::of)
                                        .stream()
                                        .sorted(Comparator.comparing(InterviewEntity::getCreatedDate, Comparator.reverseOrder()))
                                        .toList()
                        )
                );
            }
        }
        add(fileControls(false, fileEntity));
        add(createOrUpdateCandidateButton());

        addBehavior();
    }

    private Select<String> createSolution(CandidateSolution currentSolution) {
        solution = new Select<>();
        solution.setLabel("Решение");
        solution.setItems(CandidateSolution.getSolutionNames());
        if (currentSolution != null) {
            solution.setValue(currentSolution.getText());
        }

        return solution;
    }

    private Grid<InterviewEntity> createInterviewList(List<InterviewEntity> interviews) {
        var grid = new Grid<>(InterviewEntity.class, false);
        grid.setItems(interviews);
        grid.addColumn(InterviewEntity::getName).setHeader("Название");
        grid.addColumn(e -> userCrudService.getByLogin(e.getInterviewerLogin()).getFio()).setHeader("Интервьювер");
        grid.addColumn(e -> DateTimeUtils.parteToString(e.getCreatedDate())).setHeader("Дата завершения");
        grid.addItemDoubleClickListener(e -> {
            var url = MessageFormat.format("http://{0}:{1}/room/{2}", domainName, port,
                    e.getItem().getRoom().getId().toString());
            UI.getCurrent().getPage().open(url);
        });
        return grid;
    }

    public void initReadOnly(UUID candidateId) {
        this.candidateId = candidateId;

        var entity = candidateCrudService.getById(candidateId);
        add(page());

        fioField.setValue(entity.getFio());
        descriptionField.setValue(entity.getDescription());

        if (entity.getFile() != null) {
            add(fileControls(true, entity.getFile()));
            fileLink = entity.getFile().getLink();
        }
    }

    private com.vaadin.flow.component.Component page() {
        var vl = new VerticalLayout();

        fioField = new TextField("ФИО кандидата");
        fioField.setPlaceholder("ФИО кандидата");
        fioField.setWidth("300px");
        fioField.setMinWidth("10%");
        fioField.setValueChangeMode(ValueChangeMode.EAGER);
        vl.add(fioField);

        descriptionField = new TextArea("Описание");
        descriptionField.setPlaceholder("Описание");
        descriptionField.setWidth("300px");
        descriptionField.setMinWidth("10%");
        descriptionField.setValueChangeMode(ValueChangeMode.EAGER);
        vl.add(descriptionField);

        return vl;
    }

    private com.vaadin.flow.component.Component fileControls(boolean isReadOnly, FileEntity fileEntity) {
        var hl = new HorizontalLayout();

        if (!isReadOnly) {
            hl.add(createUpload());
        }

        if (fileEntity != null) {
            fileName = fileEntity.getFileName();
            fileLink = fileEntity.getLink();

            StreamResource streamResource = new StreamResource(fileLink, this::getFileStream);
            Anchor link = new Anchor(streamResource, "Скачать CV");
            link.getElement().setAttribute("download", true);
            hl.add(link);
        }

        return hl;
    }

    private InputStream getFileStream() {
        var file = buffer.getOutputBuffer(fileName);
        return file.size() > 0
                ? new ByteArrayInputStream(file.toByteArray())
                : new ByteArrayInputStream(fileService.download(fileLink));
    }

    private Button createOrUpdateCandidateButton() {
        createButton = new Button("Сохранить");
        createButton.setEnabled(false);
        createButton.addClickListener(e -> {
            createInterview();

            UI.getCurrent().navigate(CandidatesListPage.class);
        });

        return createButton;
    }

    private void createInterview() {
        var file = buffer.getOutputBuffer(fileName);

        if (isNew) {
            candidateService.createCandidate(
                    fioField.getValue(),
                    descriptionField.getValue(),
                    file.size() > 0 ? fileName : null,
                    file.size() > 0 ? file.toByteArray() : null
            );
        } else {
            candidateService.updateCandidate(
                    candidateId,
                    fioField.getValue(),
                    descriptionField.getValue(),
                    StringUtils.isNotBlank(solution.getValue()) ? CandidateSolution.getSolution(solution.getValue())
                            : null,
                    file.size() > 0 ? fileName : null,
                    file.size() > 0 ? file.toByteArray() : null
            );
        }
    }

    private Upload createUpload() {
        buffer = new MultiFileMemoryBuffer();
        var upload = new Upload(buffer);
        upload.setDropAllowed(false);
        upload.addSucceededListener(event -> fileName = event.getFileName());

        return upload;
    }

    private void addBehavior() {
        if (!isNew) {
            createButton.setEnabled(true);
        }

        fioField.addValueChangeListener(e -> createButton.setEnabled(StringUtils.isNotBlank(e.getValue())));
    }
}
