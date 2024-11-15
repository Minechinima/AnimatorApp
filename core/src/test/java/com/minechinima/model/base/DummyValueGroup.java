package com.minechinima.model.base;

import com.minechinima.data.types.ByteArrayType;
import com.minechinima.data.types.MapType;
import com.minechinima.model.dataoperations.datainterfaces.IDMutable;
import com.minechinima.model.values.DoubleValue;
import com.minechinima.utils.UUIDUtils;

import java.util.UUID;

public class DummyValueGroup extends ValueGroup implements IDMutable {
    public final DoubleValue x = new DoubleValue("x", 0D);
    public final DoubleValue y = new DoubleValue("y", 0D);
    public final DoubleValue z = new DoubleValue("z", 0D);

    public DummyValueGroup(String id) {
        super(id, UUID.randomUUID());
        this.add(x,y,z);
    }

    public DummyValueGroup(String id, MapType data) {
        super(id, UUIDUtils.readUUIDOrCreateRandom(data, "uuid"));
        this.add(x,y,z);
        this.fromData(data);
    }

    public void add(BaseStructure... children) {
        super.addChildren(children);
    }

    @Override
    public void setID(String identifier) {
        this._setID(identifier);
    }

    @Override
    public UUID getUUID() {
        return this.uuid;
    }

    @Override
    public MapType toData() {
        MapType data = super.toData();
        data.getValue().put("uuid", new ByteArrayType(UUIDUtils.convertUUIDToBytes(this.uuid)));
        return data;
    }
}
