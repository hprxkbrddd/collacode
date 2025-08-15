package com.collacode.document.rga;

import com.collacode.document.crdt.VectorClock;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RgaElement<T> {
    private String id;
    private T value;
    private VectorClock vectorClock;
    private boolean isTombstone;

}
