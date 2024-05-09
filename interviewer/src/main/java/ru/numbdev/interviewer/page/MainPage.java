package ru.numbdev.interviewer.page;

import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;
import jakarta.annotation.security.PermitAll;

@Route("")
@PageTitle("Меню")
@PermitAll
public class MainPage extends AppLayout {

    public MainPage() {
//        UI.getCurrent()
        DrawerToggle toggle = new DrawerToggle();

        H1 title = new H1("Интервьювер");
        title.getStyle().set("font-size", "var(--lumo-font-size-l)")
                .set("margin", "0");
        SideNav nav = getSideNav();

        Scroller scroller = new Scroller(nav);
        scroller.setClassName(LumoUtility.Padding.SMALL);

        addToDrawer(scroller);
        addToNavbar(toggle, title);
    }

    private SideNav getSideNav() {
        SideNav nav = new SideNav();
        nav.addItem(
                new SideNavItem("Создать команту", "/room/create",
                        VaadinIcon.PACKAGE.create()),
                new SideNavItem("Шаблоны", "/templates",
                        VaadinIcon.RECORDS.create()),
                new SideNavItem("Собеседвоания", "/interviews",
                        VaadinIcon.LIST.create()),
                new SideNavItem("Опросники", "/questions",
                        VaadinIcon.QUESTION.create())
                );
        return nav;
    }
}
