package com.minechinima.model.logging.datainterfaces;

import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public record ListElement<T>(@NotNull UUID uuid, @NotNull T value) {
}
