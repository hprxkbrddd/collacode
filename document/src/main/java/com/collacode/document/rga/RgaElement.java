package com.collacode.document.rga;

import com.collacode.document.crdt.VectorClock;
import lombok.*;

@Setter
@AllArgsConstructor
public class RgaElement<T> {
    @Getter
    private String id;
    private T value;
    @Getter
    private VectorClock vectorClock;
    @Getter
    private boolean isTombstone;

    public T getValue() {
        return isTombstone ? null : value;
    }

}
