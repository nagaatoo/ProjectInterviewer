package ru.numbdev.interviewer.page.crud;


import com.vaadin.flow.component.*;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.*;
import jakarta.annotation.security.PermitAll;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;
import ru.numbdev.interviewer.jpa.entity.*;
import ru.numbdev.interviewer.page.MainPage;
import ru.numbdev.interviewer.page.RoomPage;
import ru.numbdev.interviewer.service.InterviewService;
import ru.numbdev.interviewer.service.crud.CandidateCrudService;
import ru.numbdev.interviewer.service.crud.QuestionsCrudService;
import ru.numbdev.interviewer.service.crud.UserCrudService;
import ru.numbdev.interviewer.utils.SecurityUtil;

import java.time.LocalDateTime;

@Route(value = "/room/create", layout = MainPage.class)
@PageTitle("Создать команту")
@PermitAll
public class CreateRoomPage extends VerticalLayout {

    private final QuestionsCrudService questionsCrudService;
    private final InterviewService interviewService;
    private final UserCrudService userCrudService;
    private final CandidateCrudService candidateCrudService;

    private TextField nameField;
    private Select<TemplateEntity> templateList;
    private Select<QuestionnaireEntity> questionnaireList;
    private ComboBox<UserEntity> users;
    private ComboBox<CandidateEntity> candidates;
    private Button createButton;

    public CreateRoomPage(QuestionsCrudService questionsCrudService, InterviewService interviewService,
                          UserCrudService userCrudService, CandidateCrudService candidateCrudService) {
        this.questionsCrudService = questionsCrudService;
        this.interviewService = interviewService;
        this.userCrudService = userCrudService;
        this.candidateCrudService = candidateCrudService;

        createRoomButton();
        add(page());
        add(createButton);
    }

    private Component page() {
        var vl = new VerticalLayout();

        nameField = new TextField();
        nameField.setPlaceholder("Название интервью");
        nameField.setWidth("300px");
        nameField.setMinWidth("10%");
        nameField.setValueChangeMode(ValueChangeMode.EAGER);
        nameField.addValueChangeListener(e -> createButton.setEnabled(StringUtils.isNotBlank(e.getValue())));
        vl.add(nameField);

        candidates = new ComboBox<>("Кандидат");
        candidates.setItemLabelGenerator(CandidateEntity::getFio);
        candidates.setItemsWithFilterConverter(
                e -> candidateCrudService.findCandidateByName(
                        e.getFilter().get(),
                        e.getPage(),
                        e.getLimit()
                ),
                term -> term
        );
        vl.add(candidates);

        users = new ComboBox<>("Интервьювер");
        users.setItems(userCrudService.getByRole(Role.INTERVIEWER));
        users.setItemLabelGenerator(UserEntity::getFio);
        vl.add(users);

        var questions = questionsCrudService.getAvailableQuestions(SecurityUtil.getUserName());
        questionnaireList = new Select<>();
        questionnaireList.setLabel("Опросники");
        questionnaireList.setItemLabelGenerator(QuestionnaireEntity::getName);
        if (CollectionUtils.isEmpty(questions)) {
            questionnaireList.setEnabled(false);
        } else {
            questionnaireList.setItems(questions);
        }
        vl.add(questionnaireList);

        return vl;
    }

    private void createRoomButton() {
        createButton = new Button("Создать");
        createButton.setEnabled(false);
        createButton.addClickListener(e -> {
            var idRoom = createInterview();
            var url = RouteConfiguration.forApplicationScope().getUrl(RoomPage.class, new RouteParameters(
                    new RouteParam("identifier", idRoom)
            ));
            Anchor roomAnchor = new Anchor(url);
            roomAnchor.setText("Ссылка на команту");
            add(roomAnchor);
            add(new Text(roomAnchor.getText()));
        });
    }

    private String createInterview() {
        return interviewService.createInterview(
                nameField.getValue(),
                users.getOptionalValue().get().getLogin(),
                SecurityUtil.isHr() ? SecurityUtil.getUserName() : null,
                LocalDateTime.now(),
                null, //templateList.getValue() != null ? templateList.getValue().getId() : null
                questionnaireList.getValue() != null ? questionnaireList.getValue().getId() : null
        );
    }
}
