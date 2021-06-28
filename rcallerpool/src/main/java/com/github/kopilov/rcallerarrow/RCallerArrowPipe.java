package com.github.kopilov.rcallerarrow;

import com.github.rcaller.MessageSaver;
import com.github.rcaller.TempFileService;
import com.github.rcaller.rstuff.*;
import org.apache.arrow.memory.BufferAllocator;
import org.apache.arrow.vector.FieldVector;

public class RCallerArrowPipe {

    private static class RCallerArrow extends  RCaller {
        protected RCallerArrow(RCode rCode, ROutputArrowPipe parser, RStreamHandler rOutput, RStreamHandler rError, MessageSaver messageSaver, TempFileService tempFileService, RCallerOptions rCallerOptions) {
            super(rCode, parser, rOutput, rError, messageSaver, tempFileService, rCallerOptions);
        }
    }

    protected static RCaller connectRCaller(
            BufferAllocator allocator,
            RCode rCode,
            RStreamHandler rOutput,
            RStreamHandler rError,
            MessageSaver messageSaver,
            TempFileService tempFileService,
            RCallerOptions rCallerOptions
    ) {
        return new RCallerArrow(rCode, new ROutputArrowPipe(allocator), rOutput, rError, messageSaver, tempFileService, rCallerOptions);
    }

    public static RCaller create(BufferAllocator allocator) {
        RCallerOptions rCallerOptions = switchOptionsToArrow(RCallerOptions.create());
        return connectRCaller(allocator, RCode.create(), new RStreamHandler(null, "Output"), new RStreamHandler(null, "Error"), new MessageSaver(), new TempFileService(), rCallerOptions);
    }

    public static RCaller create(BufferAllocator allocator, RCallerOptions rCallerOptionsInit) {
        RCallerOptions rCallerOptions = switchOptionsToArrow(rCallerOptionsInit);
        return connectRCaller(allocator, RCode.create(rCallerOptions), new RStreamHandler(null, "Output"), new RStreamHandler(null, "Error"), new MessageSaver(), new TempFileService(), rCallerOptions);
    }

    public static FieldVector getVector(RCaller rcaller, String name) {
        return ((ROutputArrowPipe)rcaller.getParser()).getVector(name);
    }

    private static RCallerOptions switchOptionsToArrow(RCallerOptions rCallerOptions) {
        rCallerOptions.setUseArrowIfAvailable(true);
        rCallerOptions.setFailIfArrowNotAvailable(true);
        return rCallerOptions;
    }
}
