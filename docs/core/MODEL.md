Builds on X core concepts
- Any data operation, which changes the tree state or a value, is either:
    - Successful AND logged, making it revert-able
    - Unsuccessful, not logged, and the tree / values did not change
    - Successful but logging failed, letting the app crash

- anything in model is free to throw a lot of exceptions for any illegal arguments, things that could go wrong etc.
Programming in that layer should follow the paradigm "Never trust user input" 
but in this case, the "user input" are the variables of the program. Always perform safety checks to ensure that
the state your code works on is valid and what is expected.