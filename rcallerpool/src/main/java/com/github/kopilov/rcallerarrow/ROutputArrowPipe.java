package com.github.kopilov.rcallerarrow;

import com.github.rcaller.io.ROutputParserArrow;
import org.apache.arrow.memory.BufferAllocator;
import org.apache.arrow.vector.FieldVector;

public class ROutputArrowPipe extends ROutputParserArrow {
//    protected RCallerArrow(RCode rCode, ROutputParser parser, RStreamHandler rOutput, RStreamHandler rError, MessageSaver messageSaver, TempFileService tempFileService, RCallerOptions rCallerOptions) {
//        super(rCode, parser, rOutput, rError, messageSaver, tempFileService, rCallerOptions);
//    }
    public ROutputArrowPipe(BufferAllocator allocator) {
        super();
        bridge = new IntegratedArrowBridge(allocator);
    }

    public FieldVector getVector(String vectorName) {
        return ((IntegratedArrowBridge)bridge).findVector(vectorName);
    }

}
