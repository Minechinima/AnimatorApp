package com.minechinima.eventbus;

/**
 * A constraint that can be evaluated during event dispatch to determine whether a listener method
 * should be called for the given context.
 * <br><br>
 * Implementations of the general-purpose event framework can define concrete base classes of this
 * interface to allow event listener annotations to declare constraint types with IDE auto-suggest.
 * For example:
 * <pre>{@code
 * public abstract class MyConstraint implements IEventConstraint<MyContext> {}
 *
 * public class ActiveConstraint extends MyConstraint {
 *     @Override
 *     public boolean matches(MyContext context) { return context.isActive(); }
 * }
 *
 * @MyEventListener(constraints = {ActiveConstraint.class})
 * public void onEvent(MyEvent event) { ... }
 * }</pre>
 *
 * @param <C> the type of the constraint context that is provided by the event bus at dispatch time
 */
public interface IEventConstraint<C extends IEventContext> {
    /**
     * Returns true if this constraint is satisfied for the given dispatch context.
     *
     * @param constraintContext the context provided by the event bus at dispatch time
     * @return true if the listener should receive the event
     */
    boolean matches(C constraintContext);
}
