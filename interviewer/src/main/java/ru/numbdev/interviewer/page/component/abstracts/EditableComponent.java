package ru.numbdev.interviewer.page.component.abstracts;

import java.util.Map;

public interface EditableComponent extends CustomComponent {
    void setDiff(String actualState);
    Map<Integer, String> getDiff();
    void offerDiff(Map<Integer, String> diff, long eventTime);
}
