# UIHander Generator Framework
The generator framework allows to declare how a dataclass (BaseStructure subclass) is rendered in the UI without code. The pattern is Serivce Provider Interface.
The AnnotationProcesser generates a subclass of the Interface UIHandler and the code to display a dataclass.

## How to use
`@InGui`<br>
The Annotation `@InGui` defines which attribute of the dataclass is displayed in the UI and with what UIElement. This allows to define what is rendered in the UI and how the attributes are rendered by defining different UIElements. 
<br><br>
The parameter entrypoint is optional, it allows to declare in which part of the UI the attributes are rendered. Some parts of the UI might define UIHandler subclasses for this purpose. These defined UIHandler subclasses might provide flags via methods for their UI. They can also be empty and simply exist for the purpose of structuring a dataclass into multiple UI parts.
A dataclass can be divided into multiple entrypoints rendering parts of the attributes. This allows more flexibility with structuring the UI.
<br><br>
Example use case:
The UIPanel for showing model properties is divided into subtabs, each with an icon. (TODO BLENDER SCREENSHOT) Therefore it provides an entrypoint with an icon flag.
```java
public interface ModelProperties extends IUIHandler<ObjectData> {
  default String getIcon() {
      return "NULL";
  }
}
```
Entrypoint interfaces cannot have a generic as the AnnotationProcessor does not have the capabilities to parse and generate code for it.
Generics are not necessary as the generated interface implementation will return to which dataclass it belongs.
<br><br>
The ModelProperties UIHandler interface allows to render multiple tabs with an icon for one dataclass. For this you need to manually declare the interfaces for each tab to return the respective icon.
<br><br>
Entrypoint is optional, if it is not defined in the annotation @InGui, it will generate an `UnnamedUIHandler`. This might be useful for very simple and repetitive dataclasses.
Example use case:
Each user action command has a properties object which allows to control it. These need to be rendered in the hotkey config, UI popups etc., it does not make sense to define an entrypoint for it, as it has no flags and no special UI place or structure.
<br><br>
`@InUIGroup`<br>
This annotation allows to group multiple attributes of a dataclass. The groups are assigned with a String key. The parameter `as=IUIGroup` allows to define how the group is displayed in the UI, for example accordions or simply a seperating line.

# Notes about software design
AnnotationProcessor code is in a separate module for the future to separate UI and Data model model more, I think... The plan was to use javas module system to restrict UI access to BaseStructure code to reduce dependencies.

