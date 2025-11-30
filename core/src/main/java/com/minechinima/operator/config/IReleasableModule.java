package com.minechinima.operator.config;

/**
 * Interface for commands / configs that are used with {@link ReleaseConfirmHotKey}.<br><br>
 * The command that is configured needs to implement this, so the HotKey configuration updates the release
 * hotkey correctly.
 */
public interface IReleasableModule {
    boolean confirmOnReleaseDefault();
}
