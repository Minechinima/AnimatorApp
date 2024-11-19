Builds on X core concepts
- Any data operation, which changes the tree state or a value, is either:
    - Successful AND logged, making it revert-able
    - Unsuccessful, not logged, and the tree / values did not change
    - Successful but logging failed, letting the app crash

