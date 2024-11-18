package com.minechinima.model.base;

import com.minechinima.data.types.BaseType;
import com.minechinima.data.types.ByteArrayType;
import com.minechinima.data.types.MapType;
import com.minechinima.model.dataoperations.datainterfaces.IDMutable;
import com.minechinima.model.exceptions.SetParentNotAllowed;
import com.minechinima.model.utils.ValuePath;
import com.minechinima.model.values.DoubleValue;
import com.minechinima.utils.UUIDUtils;

import java.util.*;

public class DummyValueGroup extends BaseMutableValueHierarchy<BaseStructure> implements IDMutable {
    private final Set<BaseStructure> children = new HashSet<>();
    public final DoubleValue x = new DoubleValue("x", 0D);
    public final DoubleValue y = new DoubleValue("y", 0D);
    public final DoubleValue z = new DoubleValue("z", 0D);

    public DummyValueGroup(String id) {
        super(id, UUID.randomUUID(), ValuePath.Format.PATH);
        this.addChildren(x,y,z);
    }

    public DummyValueGroup(String id, MapType data) {
        super(id, UUIDUtils.readUUIDOrCreateRandom(data, "uuid"), ValuePath.Format.PATH);
        this.addChildren(x,y,z);
        this.fromData(data);
    }

    public void addChildren(BaseStructure... children) {
        for (BaseStructure child : children) {
            this.children.add(child);
            try {
                child.setParent(this);
            } catch (SetParentNotAllowed e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void add(BaseStructure... children) {
        this.addChildren(children);
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
    public BaseType<?> toData() {
        Map<String, BaseType<?>> map = new HashMap<>();

        for (BaseStructure child : this.children) {
            map.put(child.getIdentifier(), child.toData());
        }

        return new MapType(map);
    }

    @Override
    public void fromData(BaseType<?> data) {
        MapType type;
        if ((type = data.asMapType()) == null) return;
        for (Map.Entry<String, BaseType<?>> entry : type) {
            for (BaseStructure child : this.children) {
                if (child.getIdentifier().equals(entry.getKey())) {
                    child.fromData(entry.getValue());
                    break;
                }
            }
        }
    }

    @Override
    public boolean addChild(BaseStructure child) {
        return false;
    }

    @Override
    public boolean removeChild(BaseStructure child) {
        return false;
    }

    @Override
    public Collection<BaseStructure> getChildren() {
        return new HashSet<>(this.children);
    }

    @Override
    public boolean isDirectChild(BaseStructure child) {
        return this.children.contains(child);
    }
}
