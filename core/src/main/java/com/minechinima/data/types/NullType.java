package com.minechinima.data.types;

import com.minechinima.data.io.IDataReader;
import com.minechinima.data.io.IDataWriter;

import java.io.IOException;

public class NullType extends BaseType<Void> {
    public NullType() {
        super();
    }

    @Override
    public Void getValue() {
        return null;
    }

    @Override
    protected void toData(IDataWriter writer) throws IOException {

    }

    @Override
    protected void fromData(IDataReader reader) throws IOException {

    }
}
