### Builds on these core concepts:
- Any operation, which changes the tree state or a value, is either:
    - Successful AND logged, making it revert-able
    - Unsuccessful (exception was thrown preventing follow-up operations), not logged, and the tree / values did not change
    - Successful but logging failed, letting the app crash
- anything in model is free to throw a lot of exceptions for any illegal arguments, things that could go wrong etc.
Programming in that layer should follow the paradigm "Never trust user input" 
but in this case, the "user input" are the variables of the program. Always perform safety checks to ensure that
the state your code works on is valid and what is expected.
<br><br>
You should also preemptively throw exceptions for states that indicate a wrong usage
of the code, e.g., wrong arguments. This will help to find bugs in the app in the future.
- Any exception in the logging code to record operations will terminate the program. This is important, because logging
is a core aspect of the app. If it has issues / programmer bugs, these need to be noticed and the underlying cause fixed!
If logging failed we cannot revert changes and the user might be left with invalid data.
- The Controller layer catches all Exceptions that happen during operations. If there is an exception, the changes during
the database transaction will be reverted. If reverting fails, the app must crash.
- It is impossible to produce an invalid tree state, e.g., path collisions, wrong parent-child linkage or other structural issues.

### Base Module
The sole purpose of the `base` module is to build a tree that allows to retrieve any node with a path. Nodes in a tree
can hold data and expose data operations.
<br><br>
This module should be very decoupled of any other modules and information, e.g. how the app, logging, events, controller or history management works.
Classes in `base` are kept simple and only perform data operations and important safety checks to ensure the structural integrity
of the tree.
<br><br>
Following aspects are the only scope of `base` classes:
- build a tree of nodes
- ensure no invalid tree structure can be produced e.g., path collisions, missing parent-child linkage, parent-recursion etc.
- trigger events and bubble events up the tree (this is also used for logging).
  However, only tree traversal code is in `base` and event and logging logic is in external modules.
- manage pointers (ValuePointer is a core `base` class). The `base` classes also ensure that no 
  invalid pointers can be inserted into the tree.
- search algorithms for finding nodes with paths.
<br><br>

There are two core methods, which are final, that are responsible for securing tree structure:
- `BaseStructure.setParent()`
  - is responsible for ensuring that there is no way to insert a node / build a tree with an illegal structure.
  - This method will never throw anything else than a `SetParentNotAllowed` Exception, which is a checked Exception.
    This forces the caller to catch it, and reminds them that any previous intermediary tree/value changes need to be reverted manually
    if they were not logged.<br>If `BaseStructure.setParent()` throws something else than `SetParentNotAllowed`, the app must terminate.
- `BaseStructure._setID()`
  - is responsible for ensuring the paths stay valid and unique without path collisions.
<br><br>

A lot of other methods are final in `base` module that are built upon the implementation knowledge of the safety mechanisms.
This ensures that they cannot be overridden and change the tree behavior in a bad way, e.g. the algorithm for finding nodes with a path
is final.

<br><br>
A node can be categorized as either a subclass of `BaseStructure` or of `BaseValueHierarchy`.
- subclasses of `BaseValueHierarchy` are the only types of nodes that can have children.
- subclasses of `BaseStructure` are considered leafs/ends of the data tree.

<br>

#### Important notes
- avoid lambdas / callbacks in the model modules that could be used to reference 
another BaseStructure node and cause a memory leak.
- Always perform checks whether the state your code works on is a valid state you expect it to be and throw
  exceptions if it is not valid.
  This should help with finding bugs that are rooted in core modules,
- Always document `@Nullable` if a field or method return can be null.
  By default, you can assume that a method or field that is not annotated with `@Nullable` will not be null.
- For clarity and in important cases,
  you may use `@NotNull` if a method never returns null or if a field/parameter should never be null.
  However, if something is annotated with `@NotNull`,
  it is still appropriate to throw exceptions if it is null for clarity
  and debugging purposes to find bugs and wrong values.
- Performance intensive methods:
  - `setParent()` performs a lot of checks and logic. It bubbles events up the tree and down the tree. Either we find a way to optimize it, or
    you should carefully consider whether you want to perform frequent tree updates to a node in the data tree for every tick/render frame. 
    Updating in batches is better.
    Another way of improving tree performance is by keeping subtrees to a minimum. 
    Try to avoid creating lots of layers of nodes and keep the tree shallow.
  - `setID()`
  - all the methods that retrieve a node with a path.
- BaseImmutableHierarchy
  - Is a BaseValueHierarchy that is initialized once and never changes children after that.
  - Important when implementing a subclass: do not call initChildren() in the constructor, to leave the
  possibility open for further subclasses to add more nodes.
  - When adding an immutable hierarchy node to the tree, you need to call initChildren() on that node.


### Events module
The `events` module contains standard event types,
event-type registry code and the code for dispatching events and managing event subscribers.
<br>
This module is also very decoupled from the `base` tree classes. The only information link of these modules is that
an `EventBus`, which you subscribe to, has a `BaseStructure` owner.
<br><br>

Events can be categorized as two types: subclasses of `ChangeEvent` or of `Event`.
- Subclasses of `ChangeEvent` are events that were triggered by changes to the tree/data that can be reverted. 
  These are usually changes that could be caused by user edits. Every `ChangeEvent` therefore contains the respective
  `IDataOperation`.
- Subclasses of `Event` can be any kind of general event that is needed to notify subscribers of general events.
  `RemoveFromTreeEvent` is such a general event, as it cannot be reverted but is used to clear references to a node.
<br><br>

The only way to dispatch an event to subscribers is to dispatch it in an `EventBus`. 
This acts as a bridge between the `base` tree classes and the event management code, making
the two modules independent of each-other.
<br>

The event subscription works by declaring a method with the `@SubscribeEvent` annotation and subscribe the object that
listens to events to an `EventBus` in the `EventBusManager`.
<br><br>
The public interface of `EventBusManager` does not contain any references to `base` classes. 
This design decision is on purpose to only couple `EventBusManager` with `EventBus`.
<br><br>
An `EventBusManager` can only manage subscriptions to `EventBus` of nodes in the tree the manager is assigned to.
This is necessary as `EventBusManager` is part of reference management, which can only work on one tree at a time.

### Logging module
The `logging` module is responsible for logging data operations that are revert-able and reproducible.
<br><br>
The module is divided into `datainterfaces` and `operations`.
- `datainterfaces` are all the interfaces for classes that can be modified by a `IDataOperation`
- `operations` contains all the concrete data operation logic that modify implementations of the `datainterfaces`.
<br>

This design decouples `base` from data operations
and allows any object to be part of logging and data operation management.


The only way of logging an operation is by registering a log to `DataOperationLogging` and dispatch 
an operation through the `DataOperationLogging` object. 
This secures the log from external systems trying to inject operations that were not logged by the owner.
<br><br>
A `DataOperationLog` can be paused and resumed, but once it is closed,
it will be removed from all logging objects permanently and cannot be registered anymore.
Closing a log will freeze it.

#### UserException
The `datainterfaces` provide a default method that check if an operation can be performed and if not optionally throw a `UserException`.
The interface `IDataOperation` also throws the checked `UserException` when the operation is performed / undone.
<br><br>
It is important to note that this is meant for the application data layer that can define checks based
on own data rules for specific values.
<br><br>
These rules are about consistent data for the application and not whether the datastructures and tree
are structurally intact. The methods that throw `UserExceptions` are also never called in the `base` data classes.
They cannot be used to prevent changing the data in general.
<br><br>
The `UserException` exception should never be used for internal structural issues. It is for errors
that can make sense for the user.
<br><br>
Example when to use it:
- A Model has a parent model. If the parent pointer is set to another model that creates a loop in parents, this throws
a UserException explaining it for the user. The pointer itself is still structurally intact, it just stores another model,
but in the context of the application the specific value should not be allowed.

### Memory module
The `memory` module is responsible for general management of references to nodes of a tree. 
As soon as a node is removed from the tree, all references to it should be cleared. 
<br><br>
The reference management system allows caching the cleared references 
to later reapply them in case a transaction gets reverted. 
This allows a full recovery of database and internal app references.
<br><br>
A `ReferenceManager` is assigned to a data tree root and manages all references to that tree.
Any class that wants to participate in automatic reference management needs to extend `BaseStructureReferences`.
At the moment this is an abstract class to enforce that every implementation has a `ReferenceManager` that manages it.
Depending on future use cases this might need to be rewritten to an interface.
<br><br>
Any exceptions or wrong reference clearing logic will cause a `MemoryLeakError` which will terminate the app.

### DateTreeManager
This object combines all management classes and the root of a data tree into one object for easier access and management.