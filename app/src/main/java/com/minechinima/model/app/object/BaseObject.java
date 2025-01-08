package com.minechinima.model.app.object;

import com.minechinima.model.app.BBDatabase;
import com.minechinima.model.values.base.ValueGroup;
import com.minechinima.model.values.BooleanValue;
import com.minechinima.model.values.vectors.Vector3fValue;
import com.minechinima.ui.annotation.InGui;
import com.minechinima.ui.annotation.InUIGroup;
import com.minechinima.ui.elements.UIAccordion;
import com.minechinima.ui.elements.UICheckBox;
import com.minechinima.ui.elements.UIDragFloat;
import com.minechinima.ui.elements.UIVectorElement;
import com.minechinima.ui.elements.properties.UIObjectProperties;
import com.minechinima.ui.panels.UIPropertiesPanel;

import java.util.UUID;

public class BaseObject extends ValueGroup {
    @InUIGroup(key = "base_object.transformation", as = UIAccordion.class)
    @InGui(entrypoint = UIObjectProperties.UIObjectPropertiesHandler.class,
            translationPrefix = "base_object.transformation.translation", as = UIVectorElement.class)
    public final Vector3fValue translation = new Vector3fValue("translation");

    @InUIGroup(key = "base_object.transformation", as = UIAccordion.class)
    @InGui(entrypoint = UIObjectProperties.UIObjectPropertiesHandler.class,
            translationPrefix = "base_object.transformation.rotation", as = UIVectorElement.class)
    public final Vector3fValue rotation = new Vector3fValue("rotation");

    @InUIGroup(key = "base_object.transformation", as = UIAccordion.class)
    @InGui(entrypoint = UIObjectProperties.UIObjectPropertiesHandler.class,
            translationPrefix = "base_object.transformation.scale", as = UIVectorElement.class)
    public final Vector3fValue scale = new Vector3fValue("scale");

    @InUIGroup(key = "base_object.visibility", as = UIAccordion.class)
    @InGui(entrypoint = UIObjectProperties.UIObjectPropertiesHandler.class,
            translationPrefix = "base_object.transformation.visible_viewport", as = UICheckBox.class)
    public final BooleanValue viewportVisibility = new BooleanValue("visible_viewport", true);

    @InUIGroup(key = "base_object.visibility", as = UIAccordion.class)
    @InGui(entrypoint = UIObjectProperties.UIObjectPropertiesHandler.class,
            translationPrefix = "base_object.transformation.visible_render", as = UICheckBox.class)
    public final BooleanValue renderVisibility = new BooleanValue("visible_render", true);

    public final ObjectParentPointer parent;
    /**
     * The special data that defines this object like model, camera, entity, snowstorm etc.
     */
    public final ObjectData data = new ObjectData("data");

    public BaseObject(String id, BBDatabase database) {
        super(id);
        this.parent = new ObjectParentPointer("parent", this, database);
        this.init();
    }

    public BaseObject(String id, UUID uuid, BBDatabase database) {
        super(id, uuid);
        this.parent = new ObjectParentPointer("parent", this, database);
        this.init();
    }

    private void init() {
        this.addChild("translation", this.translation);
        this.addChild("rotation", this.rotation);
        this.addChild("scale", this.scale);
        this.addChild("parent", this.parent);
        this.addChild("data", this.data);
        this.addChild("viewport_visibility", this.viewportVisibility);
        this.addChild("render_visibility", this.renderVisibility);
    }
}
