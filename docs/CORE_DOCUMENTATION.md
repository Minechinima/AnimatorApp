WIP

model.base
- the model.base classes form a tree of nodes. Every node represents some kind of data structure,
be it simple values (leafs) or complex structures like lists that have children.
- the data tree repesents a closed system. It is like a database: removing from it means removing the node from memory.
- anything removed from the tree is cleared from memory. This means all IPointer interface implementations
pointing to the removed node are cleared. This automatically happens when parent is set to null.
- storing references to BaseStructure should be avoided if you are not registering that reference in the memory management system of the tree.
- even with exceptions thrown, never leave the tree in an unfinished state. Always repair it in the catch
- when doing a data operation in the tree, treat any method, that is not 100% guaranteed to not throw exceptions,
as a potential reason that the method is interrupted. This is important because when the method is interrupted, the tree
state should never be left in an unfinished state. Exceptions will be caught by the Controller layer and changes reverted. But only
successful changes can be reverted as they have been logged.
- wanting to remove a node from the tree (setting parent to null) is synonymous with deleting it from memory. 
The tree is like a closed system that represents a database/memory.
- exceptions and errors are a core part of the base module. Exceptions are always thrown when illegal arguments/states etc. happen.
This is expected, as a controller can catch them and revert changes when an error happened.
- any exception from base module is to be treated as something you can recover from i.e. the tree state will not change.
- Normally checked exceptions are for issues you can recover from, however, 
this would require lots of try catches and makes propagation of errors not so nice in the code. Also, the operations in base are
structured in such a way, that ANY issue can be recovered from. To distinguish between bugs you can recover from and serious unfixable issues,
Error subclasses are thrown. These should never be catched by a reasonable app according to java docs.
- any method in data operations in the base should be treated as a potential reason for failure that could interrupt the code and leave a bad tree state.
Protecting the tree state is the highest priority of the database! Outside code could catch any exceptions and keep the bad tree state. 
Therefore the code needs to be extra secure and always revert states if exceptions if an operation was not finished.
- to log a change, a change event needs to be triggered. If any exception happens during logging the change, an Error will be thrown.
This means you can safely assume that triggering a change event will log the change and let the app know how to undo the operation. 
If logging fails, the app will automatically crash, and you don't need to worry about tree states not being reverteable.
- anything in model, especially model.base, should be treated with special care regarding exceptions. 
Exceptions should not be interpreted as something that can crash the program, as Controller layer will catch them. This means: 
think about your code imperatively, what states it has at what line and whether it could be interrupted there, 
especially if you call another tree method that is more complex. Treat any methods as black boxes.
<br><br>
- performance heavy operations currently, that might need optimizations in the future:
  (when optimizing we still need to try to keep the logic of the units in their places adn separated, for example, not spread around path building logic all around the place)
  Also the performance heavy operations are almost always the safety critical methods. Optimizations should be done with care and a lot of unit tests.
  - setParent (and therefore anytime a node is added to the tree) - it checks for path collisions and other important things so you can never insert a node in the tree
  that leads to a bad tree structure
  - getChildByPath - these methods in BaseValueHierarchy use the ValuePath methods provided to build a clean path. 
  They create quite some temporary objects and have a lot of logic.
  - removing a node from the tree: clearNodeFromMemory() method is automatically called, which goes through all children of the removed node.
  There is not a lot of room for improvements, as every child node of a removed node needs to receive a call when it is removed, so every pointer to it is cleared.
  - _setID - it resolves path collisions


Terminology:
- node: any element in the tree, direct or indirect child of a hierarchy.
- child: the direct child of a hierarchy. A child is a tree node located in the children Collection of a given hierarchy.