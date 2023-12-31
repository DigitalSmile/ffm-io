package org.ds.io.gpio.model;

import org.ds.io.core.NativeMemoryAccess;

import java.lang.foreign.MemoryLayout;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.VarHandle;

public record GPIOInfo(byte[] name, byte[] label, int lines) implements NativeMemoryAccess {
    private static final MemoryLayout LAYOUT = MemoryLayout.structLayout(
            MemoryLayout.sequenceLayout(32, ValueLayout.JAVA_BYTE).withName("name"),
            MemoryLayout.sequenceLayout(32, ValueLayout.JAVA_BYTE).withName("label"),
            ValueLayout.JAVA_INT.withName("lines")
    );

    private static final MethodHandle MH_NAME = LAYOUT.sliceHandle(MemoryLayout.PathElement.groupElement("name"));
    private static final MethodHandle MH_LABEL = LAYOUT.sliceHandle(MemoryLayout.PathElement.groupElement("label"));
    private static final VarHandle VH_LINES = LAYOUT.varHandle(MemoryLayout.PathElement.groupElement("lines"));

    @Override
    public MemoryLayout getMemoryLayout() {
        return LAYOUT;
    }

    @Override
    public GPIOInfo fromBytes(MemorySegment buffer) throws Throwable {
        return new GPIOInfo(
                invokeExact(MH_NAME, buffer).toArray(ValueLayout.JAVA_BYTE),
                invokeExact(MH_LABEL, buffer).toArray(ValueLayout.JAVA_BYTE),
                (int) VH_LINES.get(buffer)
        );
    }

    @Override
    public void toBytes(MemorySegment buffer) throws Throwable {
        var tmp = invokeExact(MH_NAME, buffer);
        for (int i = 0; i < name.length; i++) {
            tmp.setAtIndex(ValueLayout.JAVA_INT, i, name[i]);
        }
        tmp = invokeExact(MH_LABEL, buffer);
        for (int i = 0; i < label.length; i++) {
            tmp.setAtIndex(ValueLayout.JAVA_INT, i, label[i]);
        }
        VH_LINES.set(buffer, lines);
    }

    private static MemorySegment invokeExact(MethodHandle handle, MemorySegment buffer) throws Throwable {
        return ((MemorySegment) handle.invokeExact(buffer));
    }

    @Override
    public String toString() {
        return "GPIOInfo{" +
                "name=" + new String(name) +
                ", label=" + new String(label) +
                ", lines=" + lines +
                '}';
    }
}
