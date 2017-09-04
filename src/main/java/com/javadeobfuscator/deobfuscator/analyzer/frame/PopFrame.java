package com.javadeobfuscator.deobfuscator.analyzer.frame;

import java.util.Arrays;
import java.util.List;

public class PopFrame extends Frame {
    private List<Frame> removed;

    public PopFrame(int opcode, Frame... removed) {
        super(opcode);
        this.removed = Arrays.asList(removed);
        this.removed.forEach(frame -> frame.children.add(this));
    }
}
