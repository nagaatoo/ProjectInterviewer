package ru.numbdev.interviewer.page.component;

import de.f0rce.ace.AceEditor;
import de.f0rce.ace.enums.AceMode;
import de.f0rce.ace.enums.AceTheme;
import io.micrometer.common.util.StringUtils;
import org.springframework.util.CollectionUtils;
import ru.numbdev.interviewer.page.component.abstracts.EditableComponent;

import java.util.AbstractMap;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

public class CustomEditor extends AceEditor implements EditableComponent {

    private static final String SPLIT = "\n";
    private static final String NULL_ROW_TAG = "#<NULL_STR>#";
    private final Map<Integer, String> rows = new ConcurrentHashMap<>();
    private final Lock lock = new ReentrantLock();

    public CustomEditor(String id, String value) {
        setId(id);
        var seq = new AtomicInteger();
        var init = Arrays
                .stream(value.split("\n"))
                .collect(
                        Collectors.toConcurrentMap(
                                e -> seq.incrementAndGet(),
                                e -> e
                        )
                );

        rows.putAll(init);
        setValue(value);
        setTheme(AceTheme.github);
        setMode(AceMode.java);
    }

    // Есть 2 бага, к которым не вижу решение для Ace Editor:
    // 1) При достаточно быстром наборе текста лок не успевает за корректкой (слетает)
    // 2) Ace Editor генерирует событие из клиента с пустым значением перед или после ввода. Решение - игнорируем полную отчистку (костыль)
    @Override
    public Map<Integer, String> getDiff(String actualState) {
        if (StringUtils.isEmpty(actualState)) {
            return Map.of();
        }

        try {
            lock.lock();
            var seq = new AtomicInteger();
            var actualRows = Arrays
                    .stream(actualState.split("\n"))
                    .collect(
                            Collectors.toConcurrentMap(
                                    e -> seq.incrementAndGet(),
                                    e -> e
                            )
                    );
            if (actualState.endsWith("\n")) {
                actualRows.put(actualRows.size() + 1, "");
            }

            if (actualRows.size() >= rows.size()) {
                var diff = actualRows
                        .entrySet()
                        .stream()
                        .filter(es -> {
                            var row = rows.get(es.getKey());
                            return !es.getValue().equals(row);
                        })
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
                rows.putAll(diff);
                return diff;
            } else {
                var emptyDiff = rows
                        .keySet()
                        .stream()
                        .filter(s -> !actualRows.containsKey(s))
                        .collect(Collectors.toMap(s -> s, s -> NULL_ROW_TAG));

                var diff = rows
                        .entrySet()
                        .stream()
                        .filter(es -> {
                            var row = actualRows.get(es.getKey());
                            return row != null && !row.equals(es.getValue());
                        })
                        .map(es -> {
                            var row = actualRows.get(es.getKey());

                            return row == null
                                    ? new AbstractMap.SimpleEntry<Integer, String>(es.getKey(), NULL_ROW_TAG)
                                    : new AbstractMap.SimpleEntry<>(es.getKey(), actualRows.get(es.getKey()));
                        })
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

                diff.putAll(emptyDiff);
                diff.forEach((key, value) -> {
                    if (NULL_ROW_TAG.equals(value)) {
                        rows.remove(key);
                    } else {
                        rows.put(key, value);
                    }
                });
                return diff;
            }
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void offerDiff(Map<Integer, String> diff) {
        if (CollectionUtils.isEmpty(diff) || isNotDifferent(diff)) {
            return;
        }

        try {
            lock.lock();
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
                    return row != null && !row.equals(es.getValue());
                });
    }

    private void saveResult(Map<Integer, String> diff) {
        diff.forEach((rowIdx, value) -> {
            if (NULL_ROW_TAG.equals(value)) {
                rows.remove(rowIdx);
            } else {
                rows.put(rowIdx, value);
            }
        });
    }

    private void addToComponent() {
        var currentRow = getCursorPosition().getRow();
        var currentCol = getCursorPosition().getColumn();

        setValue(String.join(SPLIT, rows.values()));
        setCursorPosition(currentRow, currentCol, false);
    }

    @Override
    public void setReadOnlyMode() {
        setReadOnly(true);
    }
}
