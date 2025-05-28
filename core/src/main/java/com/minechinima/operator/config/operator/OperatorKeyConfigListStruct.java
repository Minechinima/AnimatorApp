package com.minechinima.operator.config.operator;

import com.minechinima.data.types.BaseType;
import com.minechinima.model.exceptions.BaseTypeCannotBeParsed;
import com.minechinima.model.values.base.BaseValueList;
import com.minechinima.operator.IContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

/**
 * Category of key config items.
 * @param <C>
 */
public class OperatorKeyConfigListStruct<C extends IContext> extends BaseValueList<OperatorKeyConfigItemGroupStruct<C>> {
    /* TODO space, region ui stuff
     * public final Class<? extends UI...> spacetype;
     * public final Class<? extends UI...> regiontype;
     */
    public final String translationKey;

    public OperatorKeyConfigListStruct(String id, String translationKey) {
        super(id);
        this.translationKey = translationKey;
    }

    @Override
    protected @NotNull OperatorKeyConfigItemGroupStruct<C> instantiate(String id, @Nullable UUID uuid, BaseType<?> map) throws BaseTypeCannotBeParsed {
        return null;
    }
}
