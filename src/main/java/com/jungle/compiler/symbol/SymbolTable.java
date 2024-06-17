package com.jungle.compiler.symbol;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.jungle.compiler.operand.OperandType;

import java.util.*;

/* The purpose of this SymbolTable class is to map an identifier to a local variable array index
 * Resources:
 * - https://alvinalexander.com/scala/fp-book/recursion-jvm-stacks-stack-frames/
 */

public class SymbolTable {
    protected final Stack<Map<String, SymbolEntry>> stackFrame = new Stack<>();

    public void enterFrame() {
        // When entering a frame:
        // - begin a new mapping, but
        // - preserve the previous mapping.
        stackFrame.push(new HashMap<>());
    }

    public void exitFrame() {
        // When exiting a frame:
        // - discard the current mapping, and
        // - restore the previous mapping.
        stackFrame.pop();
    }

    public Map<String, SymbolEntry> getCurrentFrame() {
        return stackFrame.peek();
    }

    public SymbolTable() {
        super();
        // Initialize top-level frame
        enterFrame();
    }

    @Nullable
    public SymbolEntry get(@NotNull String name) {
        Map<String, SymbolEntry> table = getCurrentFrame();
        return table.get(name);
    }

    public SymbolEntry set(@NotNull String name, @NotNull OperandType type) {
        SymbolEntry newEntry = new SymbolEntry(getCurrentFrame().size(), type);
        getCurrentFrame().put(name, newEntry);
        return newEntry;
    }
}
