# Concept
The controller layer is the interface between application and data layer to perform operations.
This decouples how the application interacts with the database. For example, this allows to send data over the network to a remote database.
<br><br>
Another important aspect is error handling. Controller layer catches errors and reverts the database to ensure consistent data.

# TODO goals
Enforce declaring controller factory in Model layer and hide the data operations so the application layer is forced to use controller commands.
Con: OperatorProperties are client only and for the modal mode are not even part of database. For UI Mode are part of database for undo redo system.
The operatorPropertyModifier command should only modifier the properties and not return controller commands that modify other database parts.
For the future it would be better to separate Model from 
Ideal would be using java module system so operators/ui only have access to controller factory and to hide away the complexity of the model system.
# ControllerManager
Is the central API instance for executing controller commands. Every controller command is executed in the context of a `Transaction`.
The system is designed after database transactions. Only one transaction can be executed at a time. This used to ensure the order of operations,
which may be important when reverting changes if an error happens. It is also important for synchronisation, as network stuff should happen serially.
The order of operations is important e.g. "Add object A, set pointer to A", the pointer cannot be set to A if it is not added first.
# ControllerCommand
`IControllerCommand` is the interface for controller commands.

# ControllerFactory
Application layer (UI/Operators/Game logic) should only modify the database with `IControllerCommand`. 
Therefore a model class should expose the desired data operations via a ControllerFactory. 
The ControllerFactory represents the public setter interface for modifying the data.

## Important when declaring and using ControllerFactor generics
When implementing `IControllerFactoryProvider<ControllerFactory<?>>` do not bind the type `<T>` parameter of `ControllerFactory<T>`,
as it will make extending and overriding the methods with different factories harder because of generics and clashes with
the type parameters.
<br><br>
This means you also declare the factory provider method with a raw type, maybe with an extends, to allow for subclasses
to override the method.
<br><br>
Example:
```java
public class ValuePointer<T extends BaseStructure> extends BaseStructure implements IValue<T>, 
                                                                                    IControllerFactoryProvider<ValueControllerFactory<T, ?>> {
    @Override
    public ValueControllerFactory<T, ? extends ValuePointer<T>> controller() {
        return this.controller;
    }
}
```

