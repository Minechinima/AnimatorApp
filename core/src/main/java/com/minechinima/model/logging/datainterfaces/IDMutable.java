package com.minechinima.model.logging.datainterfaces;

import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * This does not expose changing the ID instance directly, as internals are responsible for it.
 */
public interface IDMutable {
    /**
     * Exposes changing the identifier value of a BaseStructure node.
     * @param identifier
     */
    void setID(String identifier);

    /**
     * @return a UUID that persistently identifies the object implementing this interface.
     * Is never null.
     */
    @NotNull
    UUID getUUID();
}
