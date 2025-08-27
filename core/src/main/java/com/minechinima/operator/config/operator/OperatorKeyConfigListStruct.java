package com.minechinima.operator.config.operator;

import com.minechinima.data.types.BaseType;
import com.minechinima.model.exceptions.BaseTypeCannotBeParsed;
import com.minechinima.model.values.base.BaseValueList;
import com.minechinima.operator.IContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

/**
 * list of operator configs
 * @param <C>
 */
public class OperatorKeyConfigListStruct<C extends IContext> extends BaseValueList<OperatorKeyConfigItemGroupStruct<C>> {
    public OperatorKeyConfigListStruct(String id) {
        super(id);
    }

    @Override
    protected @NotNull OperatorKeyConfigItemGroupStruct<C> instantiate(String id, @Nullable UUID uuid, BaseType<?> map) throws BaseTypeCannotBeParsed {
        return null;
    }
}
