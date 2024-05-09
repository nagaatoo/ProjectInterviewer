package ru.numbdev.interviewer.page.component.abstracts;

import java.util.Map;

public interface EditableComponent extends CustomComponent {
    Map<Integer, String> getDiff(String actualState);
    void offerDiff(Map<Integer, String> diff);
}
