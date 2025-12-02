# Concept
The concept is inspired by Blender's Operators, see https://developer.blender.org/docs/features/interface/operators/.
The design principle is "Command". 
One Operator is a functionality the user can perform, e.g. dragging a UI slider. 
Operators operate on a context and might have input parameters.
<br><br>
Operators are the main abstraction of user functionality and connect the UI with the data.
They separate the business-logic / controllers from the UI and also separates the UI from most of the UX logic. This allows the UI
to focus on rendering logic.
<br><br>
In contrast to Blender, the Operator layer is not equal to the Controller Layer, as our App is further separated into Controller Layer.
<br><br>
Operators are separated into `Operator`, the logic / action command and `OperatorType`.

# Reasoning and advantages
Operators are registered in a central place and can be easily handled at runtime. The HotKey system maps an Operator to a
key combination and a UI space / region. This abstraction allows to automatically fetch all Operators / user functionality in a UI
and automatically automatically build the right-click context menu. It allows to dynamically build the UI and display useful information for
the User. A user can right click on a button that has an operator and assign a shortcut to that operator.

# Operators and Context
Just like in Blender, an Operator is executed with a context. Since our app is separated into core frameworks and app, our context has to
be a generic object and is defined as `IContext`. However, every context will always provide a `UIOperatorContext`, which gives UI information like
clicked UI elements, hovered UI elements etc. Implementations of `IContext` will represent the specific application context that provides more data
like focues object, selected models, available database etc.
<br><br>
## Important notes about access restrictions
Operators should not be able to access `IContext` objects through the UI, as this will lead to coupling and spaghetti code. 
Operators only access the context by the method parameter in the interfaces.
The `IContext` will be dynamically passed through the UI and Operator Manager and should not be accessible via a getter / attribute in some object / class.
<br><br>
Operators should also not be able to access the database through the UI or any other indirect way to seperate UIOperators from Operators for
the entire app data.

# OperatorManager API
Operators are handled in `OperatorManager` and need to be executed through it. OperatorManager is the API for the Controller Layer and sits on top of it.
Therefore, the only way to execute database changes is via `OperatorManager`.
<br><br>
To execute an Operator you pass the `OperatorType` to `invoke(...)` or `execute(...)`.

## Executing Operators

## Invoking Operators

## Important notes about design.
Do not expose `ControllerManager`, only `OperatorManager` should access it.

# OperatorType
The `OperatorType` is used as a declaration for user functionality without the user logic. It allows to map one Operator to multiple OperatorTypes.
OperatorTypes provide information how the engine should handle the Operator.
<br><br>
There are many interfaces with special flags for different kinds of OperatorTypes that need to be implemend for the engine to handle it correctly.

## UIOperatorType
Use `UIOperatorType` for Operators that can only access the UI and should be executed while an Operator is run with blocking UI popup.
<br><br>
The `UIOperatorType` is for Operators that only use the `UIOperatorContext`. This is required for Operators that only act on the UI and therefore
can be executed while another Operator is executed in blocking mode with a UI popup. They can only access the UI and should not be able to
access the entire database or any other app data that is not directly part of the UI rendering, e.g. it can only access the Float data object of the
active UI slider.

## Operators with Properties
OperatorType needs to extend `OperatorTypePropertyProvider` or `UIOperatorTypePropertyProvider`, which have pre existing utility code for the data management.
Alternatively you can implement `IOTPropertyProvider` yourself and write your own data objects.
<br><br>
`Operator` needs to extend `PropertyOperator`.
<br><br>
Operators can use `OperatorProperties` for input parameters and modal keymapping. These need to be instantiated in the OperatorType (factory pattern).

# Modal Operators
OperatorType needs to impelement `IOTModal`. This provides flags for the OperatorManager and for the HotKey configuration engine.
<br><br>
Modal Operators are user actions that are invoked once and continously run for every input event until it is confirmed or cancelled.
<br><br>
Example use cases: Moving/rotating/scaling a model, dragging a UI slider.

## Modal Keymap
Implement `IPropertyModifierCommand` for the logic, `IPropertyModifierReleasableCommand` if the command should act on a key release.<br>
Extend `PropertyModifierConfig` to implement the factory for the commands and provide configuration of the command.
<br><br>
Since modal Operators run continously the user can press other keys to influence the Operator. 
This is done indirectly through `OperatorProperties` and `IPropertyModifierCommand`. The hotkey triggers a property modifier command which
changes the attributes of the operator properties which then influences the modal logic.

## Important notes about access restrictions
Modal Operators should have no way to access hotkey configurations to avoid hotkey checks inside the modal logic. This should decouple
hotkeys from the actual user logic.

# Adjustable Operators
OperatorType needs to implement `IOTAdjustable`

# Repeatable Operators
OperatorType needs to implement `IOTRepeatable`

# Operator Command
