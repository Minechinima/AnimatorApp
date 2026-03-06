package com.minechinima.operator;

import com.minechinima.Application;

public class BBOperatorTypeRegistryGroupStruct extends OperatorTypeRegistry<BBOperatorContext> {
    public final OTRotateObject rotate = new OTRotateObject("rotate", "rotate", "Rotate given object", Application.ROTATE_MODIFIER_REGISTRY);
    public final OTTranslateObject move = new OTTranslateObject("translate", "translate", "Move given object", Application.TRANSLATE_MODIFIER_REGISTRY);
    public final OTScaleObject scale = new OTScaleObject("scale", "scale", "Scale given object", Application.SCALE_MODIFIER_REGISTRY);
    public final OTDeleteObject delete = new OTDeleteObject("delete", "delete object", "Delete");
    public final OTOrbitViewport orbitViewport = new OTOrbitViewport("orbit", "Orbit Viewport", "Move viewport.");
    public final OTPanViewport panViewport = new OTPanViewport("pan", "Pan Viewport", "Move viewport.");
    public final OTZoomViewport zoomViewport = new OTZoomViewport("zoom", "Zoom Viewport", "Move viewport.");
    public final OTDuplicateObject duplicateObject = new OTDuplicateObject("duplicate", "Duplicate", "Ducpliate");
    public final OTDuplicateMoveObject duplicateMoveObject;

    public BBOperatorTypeRegistryGroupStruct(String id) {
        super(id);
        this.addChild(this.move);
        this.addChild(this.scale);
        this.addChild(this.delete);
        this.addChild(this.orbitViewport);
        this.addChild(this.panViewport);
        this.addChild(this.zoomViewport);
        this.addChild(this.duplicateObject);
        this.addChild(this.duplicateMoveObject = new OTDuplicateMoveObject("duplicate_move", "Duplicate", "Ducpliate",
                this.duplicateObject, this.move));
        this.addChild(this.rotate);

        this.rotate.addSwitchType(this.move);
        this.rotate.addSwitchType(this.scale);
        this.move.addSwitchType(this.rotate);
        this.move.addSwitchType(this.scale);
        this.scale.addSwitchType(this.move);
        this.scale.addSwitchType(this.rotate);
    }
}
