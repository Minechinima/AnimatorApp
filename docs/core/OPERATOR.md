# Concept
The concept is inspired by Blender's Operators, see (see [Blender Operators documentation](https://developer.blender.org/docs/features/interface/operators)).
The design principle is "Command". 
One Operator is a functionality the user can perform, e.g. dragging a UI slider. 
Operators operate on a context and might have input parameters.
<br><br>
Operators are the main abstraction of user functionality and connect the UI with the data.
They separate the business-logic / controllers from the UI and also separate the UI from most of the UX logic. This allows the UI
to focus on rendering logic.
<br><br>
In contrast to Blender, the Operator layer is not equal to the Controller Layer, as our App further separates the "controller" logic from Blender into Controller and Operator Layer.
<br><br>
Operators are separated into `Operator`, the logic / action command, and `OperatorType`, the declaration and registration.

# Reasoning and advantages
Operators are registered in a central place and can be easily handled at runtime. The HotKey system maps an Operator to a
key combination and a UI space / region. This abstraction allows to automatically fetch all Operators / user functionality in a UI
and automatically build the right-click context menu. It allows to dynamically build the UI, construct a header with all available functions in dropdown menus and display useful information for
the user. A user can right click on a button that has an operator and assign a shortcut to that operator.

# Operators and Context
Just like in Blender, an Operator is executed with a context. Since our app is separated into core frameworks and app, our context has to
be a generic object and is defined as `IContext`. However, every context will always provide a `UIOperatorContext`, which gives UI information like
clicked UI elements, hovered UI elements etc. Implementations of `IContext` will represent the specific application context that provides more data
like focused object, selected models, available database etc.
<br><br>

## Important notes about design
Operators should not be able to access `IContext` objects through the UI, as this will lead to coupling and spaghetti code. 
Operators only access the context by the method parameter in the interfaces.
The `IContext` will be dynamically passed through the UI and Operator Manager and should not be accessible via a getter / attribute in some object / class.
<br><br>
Operators should also not be able to access the database through the UI or any other indirect way so UIOperators are seperated from Operators. 
UIOperators should not be abused to perform complex operations on the database as they should only interact with the UI and the data the respective UI element provides/handles.

# OperatorManager
Operators are handled in `OperatorManager` and need to be executed through it. OperatorManager is the API for the Controller Layer and sits on top of it. For simple tasks you can use the `OperatorManager` API to execute controllers. Therefore, the only way to execute database changes is via `OperatorManager`.
<br><br>
To execute an Operator you pass the `OperatorType` to `invoke(...)` or `execute(...)`.

## Executing Operators
When executing an Operator the `execute(...)` function is called and the Operator is only called once and finishes.

## Invoking Operators
Invoking an Operator is used in most places of the UI and by the hotkey engine when a user triggers an action. Invoking allows the operator to decide whether to open a UI popup, transition into modal mode or just finish after executing its logic.
Modal mode is defined as a continuous running user action, for example when the user moves an object around the move operator will run and capture the mouse movement to translate the object. See more information in the modal section.

## Event blockers
While an Operator is running in modal mode, the user might hold down keys. When the Operator finishes modal mode, these keys that are still held down should not trigger a new Operator until they are released. This is needed to prevent Operators from beeing executed immediately again after finishing, which would be bad UX.
<br><br>
Example: the user presses G (holds down) to move the object, presses right click to abort but G is still held down which would trigger the Operator again and would start moving the object again.
<br><br>
Implement `ICachedEventBlocker` to create a strategy how to block events and when to unblock and register it to the OperatorManager.
`ICachedEventBlocker` is called for every cached event which allows to build a state machine and use custom cache variables, see `KeyMapMouseDragBlocker`.

## History
The OperatorManager manages the user history and encapsulates it. It has API functions for undo and redo.

## Important notes about design.
- Do not expose `ControllerManager`, only `OperatorManager` should access it.
- Do not expose `HistoryManager` as the `OperatorManager` manages it in the context of currently running Operators.

# OperatorType
The app needs to have a registry of all known Operators and how to handle them. The `OperatorType` is used as a declaration for the Operators without the user logic. It is essentially the blueprint for constructing the actual Operator and how the engine should handle it. The seperation of type and command also allows to map an Operator to multiple OperatorTypes. OperatorTypes are part of the data tree to
make use of the path system for unique identifiers. This way, the identifier can be used to reference an OperatorType. So the UI only references the OperatorType and can already use that to query information about the Operator, like the name, tooltip or if the operator can be executed in the current context (determined by calling the poll(...) method) (see [Blender documentation](https://developer.blender.org/docs/features/interface/operators/#operator-types)).
<br><br>
There are many interfaces with special flags for different kinds of OperatorTypes that need to be implemented for the engine to handle it correctly.
<br><br>
OperatorTypes instantiate the Operator commands. Operator commands are kept alive as long as they run. After they finished they are
garbage collected to prevent memory leaks from temporary cache variables inside the Operator.

## UIOperatorType
Use `UIOperatorType` for Operators that can only access the UI and should be executed while an Operator is run with a UI popup that blocks other Operators.
<br><br>
The `UIOperatorType` is for Operators that only use the `UIOperatorContext`. This is required for Operators that only act on the UI and therefore
can be executed while another Operator is executed in blocking mode with a UI popup. They can only access the UI and should not be able to
access the entire database or any other app data that is not directly part of the UI rendering, e.g. it can only access the Float data object of the
active UI slider.

## poll(context)
The poll function in the OperatorType is for checking if the Operator can be executed / invoked. This can be used to prevent the Operator command from being instantiated and throw a meaningful error for the user.

## Macros
Every `OperatorType` can define a list of other operator types that should be executed after the current one finishes successfully.
This allows to create a chain of operators and reuse other operators.
<br><br>
Macro Operators can be other macro operators which can lead to a tree of macro operators that will be executed recursively.
When a macro operator enters into UI or Modal mode, the invocation of the other macro operators will be paused until the operator finishes.
<br><br>
Example use case: duplicate_move operator will first duplicate the object and then directly invoke the translate modal operator.

# Operators with input parameters
OperatorType needs to extend `OperatorTypePropertyProvider` or `UIOperatorTypePropertyProvider`, which have pre existing utility code for the data management.
Alternatively you can implement `IOTPropertyProvider` yourself and write your own data objects.
<br><br>
`Operator` needs to extend `PropertyOperator`.
<br><br>
Operators can use `OperatorProperties` for input parameters and modal keymapping. The property object needs to be instantiated in the OperatorType (factory pattern).

# Modal Operators
OperatorType needs to impelement `IOTModal`. This provides flags for the OperatorManager and for the HotKey configuration engine.
<br><br>
Operator needs to implement `IModalOperator`
<br><br>
Modal Operators are user actions that are invoked once and continously run for every input event until it is confirmed or cancelled.
<br><br>
Example use cases: Moving/rotating/scaling a model, dragging a UI slider.

## Modal Keymap
To create a command that modifies the operator properties, implement `IPropertyModifierCommand` for the logic, `IPropertyModifierReleasableCommand` if the command should act on a key release.
<br><br>
Extend `PropertyModifierConfig` to implement the factory for the specific command and provide configuration for one command.
<br><br>
To let the engine automatically create the configuration category, implement `IOTModalConfigProvider`. As soon as an OperatorType is registered, the engine will fetch the registry from that interface
and create a configuration with it.
<br><br>
Since modal Operators run continously the user can press other keys to influence the Operator. 
This is done indirectly through `OperatorProperties` and `IPropertyModifierCommand`. The hotkey triggers a property modifier command which
changes the attributes of the operator properties which then influences the modal logic. The modal method of the Operator is called after the property modifier commands.
<br><br>
The property modifier commands and their configurations are mapped to the `OperatorProperties` class, which makes them reusable for all
operators that use that `OperatorProperties` class.

## Modal OperatorType switching
A modal OperatorType `IOTModal` can provide a set of other operatortypes that the user can switch to while the operator is running in modal mode.
The engine will automatically display the available operatortypes and hotkeys for switching.
<br><br>
The switching to another type is declared with a `SwitchType` object. To handle switching with `OperatorProperties` the class `SwitchTypeProperties` can be used. The `SwitchTypeProperties` allows
to write a custom method to create the `OperatorProperties` for the next OperatorType, which allows to either take all the input parameters or just a part of it.
<br><br>
Example use case: The transform operators (translate, rotate, scale) can be switched between each other.
The transform operators also use the `OperatorProperties` of the previous instance but without the actual xyz value, so parameters like local and axis transformation are persistent.

## Important notes about design
- Modal Operators should have no way to access hotkey configurations to avoid hotkey checks inside the modal logic. This should decouple hotkeys from the actual user logic.
- the property modifier commands should not modify anything other than the properties. <b>TODO: currently no way to enforce as controller commands are returned</b>
- the separation with property modifier commands is supposed to make the Operator Modal logic less cluttered with helper variables, hotkey logic and anything other than the actual data modification. Keeping the Modal keymap separated makes it more clear how a Modal Operator is influenced without analyzing the Operator code.

# Adjustable Operators
OperatorType needs to implement `IOTAdjustable`.
<br><br>
Operators that are adjustable can be adjusted after they finished execution.

# Repeatable Operators
OperatorType needs to implement `IOTRepeatable`.
<br><br>
Operators that are repeatable can be executed multiple times.

# Custom Undo/Redo Operators
Operator needs to implement `IUndoOperator` and create a `IUndoOperatorCommand`. It is very important that `IUndoOperatorCommand` is
implemented as a separate class or <b>static</b> inner class and does not contain any references to the original Operator that spawned it, so it can be
garbage collected.
<br><br>
Operators can have custom undo redo logic that is executed besides the history of data operations. This allows to undo things in the UI.
<br><br>
The instance of `IUndoOperator` is also not cached in the history to avoid memory leaks, only the `IUndoOperatorCommand` is cached.

# Custo dynamic Undo title
Operator needs to implement `ITitleProvider`.
<br><br>
This allows to provide a title for the history log based on the execution of the Operator.
