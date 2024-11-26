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
This module should be very decoupled of any other modules and information, e.g. how logging, events, controller or history management works.
Classes in `base` are kept simple and only perform data operations and important safety checks to ensure the structural integrity
of the tree.
<br><br>
There are two core methods, which are final, that are responsible for securing tree structure: 
- `BaseStructure.setParent()`
  - is responsible for ensuring that there is no way to insert a node / build a tree with an illegal structure.
- `BaseStructure._setID()`
  - is responsible for ensuring the paths stay valid and unique without path collisions.

A lot of other methods are final in `base` module that are built upon the implementation knowledge of the safety mechanisms.
This ensures that they cannot be overridden and change the tree behavior in a bad way, e.g. the algorithm for finding nodes with a path
is final.

### Important notes
- avoid lambdas / callbacks in the model modules that could be used to reference 
another BaseStructure node and cause a memory leak.