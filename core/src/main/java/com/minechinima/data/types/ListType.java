package com.minechinima.data.types;

import com.minechinima.data.io.IDataReader;
import com.minechinima.data.io.IDataWriter;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ListType extends BaseType<List<BaseType<?>>> implements Iterable<BaseType<?>> {
    private List<BaseType<?>> value;

    protected ListType() {
        super();
    }

    public ListType(List<BaseType<?>> value) {
        this.value = new ArrayList<>(value);
    }

    @Override
    protected void toData(IDataWriter writer) throws IOException {
        writer.writeList(value);
    }

    @Override
    protected void fromData(IDataReader reader) throws IOException {
        value = reader.readNextList();
    }

    @Override
    public List<BaseType<?>> getValue() {
        return value;
    }

    /**
     * @param i
     * @throws IndexOutOfBoundsException
     * @return
     */
    public BaseType<?> get(int i) {
        return this.value.get(i);
    }

    public int size() {
        return this.value.size();
    }

    @NotNull
    @Override
    public Iterator<BaseType<?>> iterator() {
        return this.value.iterator();
    }
}
