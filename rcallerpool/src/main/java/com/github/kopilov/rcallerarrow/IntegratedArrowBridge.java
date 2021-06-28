package com.github.kopilov.rcallerarrow;

import com.github.rcaller.io.arrow.ArrowImpl;
import org.apache.arrow.memory.BufferAllocator;
import org.apache.arrow.memory.RootAllocator;
import org.apache.arrow.vector.FieldVector;

/**
 * {@link com.github.rcaller.io.ArrowBridge} implementation based on original {@link com.github.rcaller.io.arrow.ArrowImpl}
 * for using with other Arrow-based code with common memory managing
 */
public class IntegratedArrowBridge extends ArrowImpl {

    BufferAllocator allocator;
    public IntegratedArrowBridge(BufferAllocator allocator) {
        this.allocator = allocator;
    }

    @Override
    protected BufferAllocator getAllocator() {
        return allocator;
    }

    @Override
    protected void free() {
        //Do nothing
    }

    public FieldVector findVector(String vectorName) {
        return super.findVector(vectorName);
    }
}
