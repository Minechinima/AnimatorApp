# Animator App documentation
Welcome to the Animator App, a very general purpose framework for applications 
that require similar data management and animation capabilities like Blender.
<br><br>
The Framework is designed in such a way, that it only requires an external system to provide a render engine
and update logic.

## Outline and structure
The documentation will outline the general architectures and their design rationales, explain
concrete modules, define code guidelines for design and how to work with it.
This document will outline the project structure:
- the project is split up into `core` and `app`, as these are separate frameworks.

### Core module
The `core` framework is a very general purpose framework that defines independent modules. 
Anything slightly related to a concrete application or necessary for the specific context of an application 
should be carefully considered whether it can go into `core`.
The `core` module is supposed to be kept to a minimum, flexible and extensible, without any application domain knowledge.
This allows decoupling of both modules and makes changes to either of them easier and more understandable.
Using implementation knowledge of the application is forbidden to be used for implementing stuff in the `core`. Instead,
you should focus on the high-level requirements and how a general purpose system can allow them to be fulfilled.
<br><br>
The `core` allows for any application to be built with it, that has the following high-level requirements:
- central database management: the application has complex datastructures. The instances of these structures need to be in
central places to be shared with other parts of the application instead of copied. This allows changes to data to 
propagate through the application immediately, as they share the same references.
- change management: the application requires a history management system for undo-redo. However, the app is too complex
to use the command-pattern on user action level for undo-redo, as there will be a lot of user commands in the future. Any changes to the data
need to be automatically tracked and able to undo/redo.
- any data in the application needs to be addressable at runtime with unique locators, like string paths.
- the application needs a pointer-like structure, which is automatically cleared when the pointer target data is 
removed from the application. This allows for data to reference other data and automatically clear 
that reference when the application wants to delete it.
- there can be multiple serialization formats in the future that the data is stored in.
- the architecture needs to be layered.
- networking and syncing of the database might be a future use case.
- animating any datastructure independent of the animation owner, meaning you can exchange animations between different owners.
- a UI, with adjustable panels/windows, that immediately reflects the data changes.
- every user action should be encapsulated in an operator that is reusable, registered to keybinds, customizable in the config 
and automatically triggered. Additionally, operators should be automatically displayed in the context menu for the UI 
they are available in and can be triggered on with a keybind. This is the same system as in Blender.

### App module
The `app` is the concrete animation app that is built with the `core`. 
It has the concrete data definitions that the application needs, like camera, models, poses, objects, animation.
It also has concrete user actions and UI classes needed for the specific use cases of the application.
