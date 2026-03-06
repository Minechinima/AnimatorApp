package com.minechinima.operator.config;

/**
 * Interface for commands / configs that are used with {@link ReleaseConfirmHotKey}.<br><br>
 * The command that is configured needs to implement this, so the HotKey configuration updates the release
 * hotkey correctly.
 */
public interface IReleasableModule {
    /**
     * @return true if the release event should be used to trigger another onRelease method.
     * Can be used for various purposes: confirming an operator on release, executing another method on release,
     * Removing the effect of a property modifier on release.
     * TODO this can be user configured, this is just default value. Add a framework for user configurable flags and whether they can be overridden by the user.
     */
    boolean useReleaseEvent();
}
