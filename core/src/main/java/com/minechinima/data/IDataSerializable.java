package com.minechinima.data;

import com.minechinima.data.types.BaseType;

public interface IDataSerializable <T extends BaseType<?>>
{
    T toData();

    void fromData(T data);
}
