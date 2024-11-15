package com.minechinima.model.base;

import com.minechinima.model.exceptions.UserException;
import com.minechinima.data.types.BaseType;
import com.minechinima.model.values.DoubleValue;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.UUID;

public class BaseValueMapTest {
    @Test
    public void addMultipleEmptyIdentifiersNotAllowed() {
        BaseValueMap<DoubleValue> test = getAnonymousTestMap("test");
        test.add(new DoubleValue(""));
        Assertions.assertThrows(Exception.class, () -> test.add(new DoubleValue("")));
    }

    @Test
    public void addMultipleEmptyIdentifiersAllowed() {
        BaseValueMap<DummyMutableIDValue> test = new BaseValueMap<DummyMutableIDValue>("test") {
            @Override
            protected @NotNull DummyMutableIDValue instantiate(String id, @Nullable UUID uuid, BaseType<?> data) {
                DummyMutableIDValue v = uuid == null ? new DummyMutableIDValue(id) : new DummyMutableIDValue(id, uuid);
                v.fromData(data);
                return v;
            }

            @Override
            public boolean canAdd(Object value) throws UserException {
                return value instanceof DummyMutableIDValue;
            }
        };

        test.add(new DummyMutableIDValue(""));
        test.add(new DummyMutableIDValue(""));
        test.add(new DummyMutableIDValue(""));
        Assertions.assertEquals(3, test.getChildren().size());
    }

    public static BaseValueMap<DoubleValue> getAnonymousTestMap(String id) {
        return new BaseValueMap<DoubleValue>(id) {
            @Override
            protected @NotNull DoubleValue instantiate(String id, @Nullable UUID uuid, BaseType<?> data) {
                DoubleValue v = new DoubleValue(id);
                v.fromData(data);
                return v;
            }

            @Override
            public boolean canAdd(Object value) throws UserException {
                return value instanceof DoubleValue;
            }
        };
    }
}
