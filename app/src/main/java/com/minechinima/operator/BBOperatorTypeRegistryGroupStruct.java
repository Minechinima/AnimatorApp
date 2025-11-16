package com.minechinima.operator;

public class BBOperatorTypeRegistryGroupStruct extends OperatorTypeRegistry<BBOperatorContext> {
    public final OTTranslateObject move = new OTTranslateObject("translate", "translate", "Move given object");
    public final OTDeleteObject delete = new OTDeleteObject("delete", "delete object", "Delete");
    public final OTOrbitViewport orbitViewport = new OTOrbitViewport("orbit", "Orbit Viewport", "Move viewport.");
    public final OTPanViewport panViewport = new OTPanViewport("pan", "Pan Viewport", "Move viewport.");
    public final OTZoomViewport zoomViewport = new OTZoomViewport("zoom", "Zoom Viewport", "Move viewport.");

    public BBOperatorTypeRegistryGroupStruct(String id) {
        super(id);
        this.addChild(this.move);
        this.addChild(this.delete);
        this.addChild(this.orbitViewport);
        this.addChild(this.panViewport);
        this.addChild(this.zoomViewport);
    }
}
