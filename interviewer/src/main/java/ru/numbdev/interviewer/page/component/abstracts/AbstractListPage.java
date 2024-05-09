package ru.numbdev.interviewer.page.component.abstracts;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.ItemDoubleClickEvent;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.function.ValueProvider;

public abstract class AbstractListPage<T> extends VerticalLayout {

    private final Class<T> clazz;

    private Grid<T> grid;
    private final TextField filter = buildSearchField();
    private final Button createButton = new Button();
    private final Button removeButton = new Button();

    protected abstract DataProvider<T, String> buildDataProvider();

    protected AbstractListPage(Class<T> clazz) {
        this.clazz = clazz;
    }

    protected void initPage() {
        initPage(true);
    }

    protected void initPage(boolean withButtons) {
        initGrid();
        add(filter);
        add(grid);

        if (withButtons) {
            add(initButtons());
        }
    }

    private void initGrid() {
        grid =  new Grid<>(clazz, false);
        grid.setDataProvider(buildDataProvider());
        grid.addItemDoubleClickListener(chooseElement());
    }

    protected void addColumn(ValueProvider<T, ?> valueProvider, String headerName) {
        grid.addColumn(valueProvider).setHeader(headerName);
    }

    protected String getFilterValue() {
        return filter.getValue();
    }

    private HorizontalLayout initButtons() {
        var hl = new HorizontalLayout();

        createButton.setText("Создать");
        createButton.addClickListener(addAction());

        removeButton.setText("Удалить");
        removeButton.addClickListener(removeAction());

        hl.add(createButton);
        hl.add(removeButton);

        return hl;
    }

    protected T getSelectedElement() {
        return grid.getSelectedItems().stream().findFirst().get();
    }

    protected void refresh() {
        grid.getDataProvider().refreshAll();
    }

    protected abstract ComponentEventListener<ClickEvent<Button>> addAction();
    protected abstract ComponentEventListener<ClickEvent<Button>> removeAction();

    protected abstract ComponentEventListener<ItemDoubleClickEvent<T>> chooseElement();

    private TextField buildSearchField() {
        TextField searchField = new TextField();
        searchField.setWidth("10%");
        searchField.setMinWidth("200px");
        searchField.setPlaceholder("Search");
        searchField.setPrefixComponent(new Icon(VaadinIcon.SEARCH));
        searchField.setValueChangeMode(ValueChangeMode.EAGER);
        searchField.addValueChangeListener(e -> refresh());

        return searchField;
    }
}
