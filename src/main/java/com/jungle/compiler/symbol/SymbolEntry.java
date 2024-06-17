package com.jungle.compiler.symbol;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.jungle.compiler.operand.OperandType;

public class SymbolEntry {
    // identifier type
    @NotNull
    private final OperandType type;

    // index of the local variable array
    private final int index;

    public SymbolEntry(int index, @NotNull OperandType type) {
        super();
        this.index = index;
        this.type = type;
    }

    public int getIndex() {
        return index;
    }

    @NotNull
    public OperandType getType() {
        return type;
    }

    @Override
    public boolean equals(@Nullable Object other) {
        if (other == null) return false;
        if (!(other instanceof SymbolEntry)) return false;
        SymbolEntry otherSymbolEntry = (SymbolEntry) other;
        return new EqualsBuilder()
                .append(getIndex(), otherSymbolEntry.getIndex())
                .append(getType(), otherSymbolEntry.getType())
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(getIndex())
                .append(getType())
                .toHashCode();
    }
}
