package com.collacode.document.rga;

import com.collacode.document.component.IdGenerator;
import com.collacode.document.crdt.VectorClock;
import lombok.Getter;

import java.util.LinkedList;
import java.util.List;

@Getter
public class RGA<T> {
    private final List<RgaElement<T>> elements;
    private final VectorClock documentVector;

    public RGA(VectorClock documentVector) {
        this.elements = new LinkedList<>();
        this.documentVector = documentVector;
    }

    public void insert(T value, String anchorId, VectorClock vectorClock) {
        int insertIndex = findInsertIndex(anchorId);
        elements.add(insertIndex, new RgaElement<>(
                vectorClock.toString() + ":" + IdGenerator.genId(),
                value,
                vectorClock,
                false)
        );
    }

    private int findInsertIndex(String anchorId) {
        // Если anchorId не указан (вставка в начало), возвращаем 0
        if (anchorId == null) return 0;

        // Ищем anchor-элемент (даже если он tombstone)
        for (int i = 0; i < elements.size(); i++) {
            if (elements.get(i).getId().equals(anchorId)) {
                return i + 1; // Вставка ПОСЛЕ anchor
            }
        }

        // Если anchor не найден, вставляем в конец
        return elements.size();
    }

    public void remove(String elementId, VectorClock opClock) {
        for (RgaElement<T> el : elements) {
            if (el.getId().equals(elementId)) {
                el.setTombstone(true);
                el.getVectorClock().merge(opClock);
                documentVector.merge(opClock);
                return;
            }
        }
    }

    public List<T> toList() {
        return elements.stream().filter(
                        el -> !el.isTombstone()
                ).map(RgaElement::getValue)
                .toList();
    }
}
