package ru.numbdev.interviewer.page.component;

import de.f0rce.ace.AceEditor;
import de.f0rce.ace.enums.AceMode;
import de.f0rce.ace.enums.AceTheme;
import io.micrometer.common.util.StringUtils;
import org.springframework.util.CollectionUtils;
import ru.numbDev.common.constant.ValueConstants;
import ru.numbDev.common.utils.ElementUtils;
import ru.numbdev.interviewer.page.component.abstracts.EditableComponent;

import java.util.AbstractMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

public class CustomEditor extends AceEditor implements EditableComponent {

    private final Map<Integer, String> rows = new ConcurrentHashMap<>();
    private final Map<Integer, String> diff = new ConcurrentHashMap<>();
    private final Lock lock = new ReentrantLock();

    private long lastEventTime;
    private boolean nextEventIsBug = false;

    public CustomEditor(String id, String value) {
        setId(id);
        var init = ElementUtils.buildRowsElement(value);

        rows.putAll(init);
        setValue(value);
        setTheme(AceTheme.github);
        setMode(AceMode.java);

        lastEventTime = System.currentTimeMillis();
    }

    @Override
    public void setDiff(String actualState) {
        try {
            lock.lock();
            if (StringUtils.isEmpty(actualState) && nextEventIsBug) {
                nextEventIsBug = false;
                return;
            }

            nextEventIsBug = true;
            var actualRows = ElementUtils.buildRowsElement(actualState);
            if (actualState.endsWith("\n")) {
                actualRows.put(actualRows.size() + 1, "");
            }

            if (actualRows.size() >= rows.size()) {
                var protoDiff = actualRows
                        .entrySet()
                        .stream()
                        .filter(es -> {
                            var row = rows.get(es.getKey());
                            return !es.getValue().equals(row) || !rows.containsKey(es.getKey());
                        })
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
                diff.putAll(protoDiff);
            } else {
                var emptyDiff = rows
                        .keySet()
                        .stream()
                        .filter(s -> !actualRows.containsKey(s))
                        .collect(Collectors.toMap(s -> s, s -> ValueConstants.NULL_ROW_TAG));

                var protoDiff = rows
                        .entrySet()
                        .stream()
                        .filter(es -> {
                            var row = actualRows.get(es.getKey());
                            return row != null && !row.equals(es.getValue());
                        })
                        .map(es -> {
                            var row = actualRows.get(es.getKey());

                            return row == null
                                    ? new AbstractMap.SimpleEntry<Integer, String>(es.getKey(), ValueConstants.NULL_ROW_TAG)
                                    : new AbstractMap.SimpleEntry<>(es.getKey(), actualRows.get(es.getKey()));
                        })
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

                protoDiff.putAll(emptyDiff);
                diff.putAll(protoDiff);
            }

            rows.clear();
            rows.putAll(actualRows);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public Map<Integer, String> getDiff() {
        try {
            lock.lock();
            var saved =  Map.copyOf(diff);
            diff.clear();
            return saved;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void offerDiff(Map<Integer, String> diff, long eventTime) {
        if (CollectionUtils.isEmpty(diff) || isNotDifferent(diff) || lastEventTime > eventTime) {
            return;
        }

        try {
            lock.lock();
            lastEventTime = eventTime;
            saveResult(diff);
            addToComponent();
        } finally {
            lock.unlock();
        }
    }

    private boolean isNotDifferent(Map<Integer, String> diff) {
        return diff
                .entrySet()
                .stream()
                .noneMatch(es -> {
                    var row = rows.get(es.getKey());
                    return row == null || !row.equals(es.getValue());
                });
    }

    private void saveResult(Map<Integer, String> diff) {
        diff.forEach((rowIdx, value) -> {
            if (ValueConstants.NULL_ROW_TAG.equals(value)) {
                rows.remove(rowIdx);
            } else {
                rows.put(rowIdx, value);
            }
        });
    }

    private void addToComponent() {
        var currentRow = getCursorPosition().getRow();
        var currentCol = getCursorPosition().getColumn();

        setValue(String.join(ValueConstants.SPLIT, rows.values()));
        setCursorPosition(currentRow, currentCol, false);
    }

    @Override
    public void setReadOnlyMode() {
        setReadOnly(true);
    }
}
