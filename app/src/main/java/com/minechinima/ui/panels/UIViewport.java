package com.minechinima.ui.panels;

import com.minechinima.model.BBContext;
import com.minechinima.operator.BBOperatorContext;
import com.minechinima.ui.UIContext;
import com.minechinima.ui.elements.UISpace;
import com.minechinima.ui.elements.UIWindow;
import com.minechinima.ui.utils.Area;
import imgui.ImGui;
import imgui.ImVec2;
import org.jetbrains.annotations.Contract;
import org.joml.Matrix3d;
import org.joml.Matrix4d;
import org.joml.Vector3d;

import java.util.Vector;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.*;

public class UIViewport extends UISpace {
    private int fbo;
    private int texture;
    private int depthBuffer;

    /**
     * in degrees
     */
    protected float yaw = 180;
    /**
     * in degrees
     */
    protected float pitch = 0;
    /**
     * Full vertical fov in degrees.
     */
    protected float fov = 45;
    /**
     * in degrees
     */
    protected float roll;
    protected final Vector3d pos = new Vector3d(0, 0, -10);
    protected final Vector3d orbitPos = new Vector3d(0, 0, 0);
    protected final Area area = new Area();


    public UIViewport(String panelName) {
        super(panelName);
        this.setupFBO(800, 600);
    }

    //TODO just for testing, put these in UI super classes or so
    public Area getArea() {
        return this.area.copy();
    }

    /**
     * @return copy
     */
    public Matrix4d getProjection() {
        Matrix4d proj = new Matrix4d();
        proj.perspective(Math.toRadians(this.fov), 800.0f / 600.0f, 0.1f, 100.0f);
        return proj;
    }

    /**
     * Rotation of the camera view.
     * <br>
     * camera without rotation faces +Z
     * positive pitch turns upwards.
     * @return copy
     */
    public Matrix3d getViewRotation() {
        Matrix3d rot = new Matrix3d();
        rot.rotateY(Math.toRadians(this.yaw));
        rot.rotateX(Math.toRadians(this.pitch));
        return rot;
    }

    public Vector3d getViewVector() {
        return this.getViewRotation().transform(new Vector3d(0,0, -1));
    }

    public float getYaw() {
        return yaw;
    }

    public void setYaw(float yaw) {
        this.yaw = yaw;
    }

    public float getPitch() {
        return pitch;
    }

    public void setPitch(float pitch) {
        this.pitch = pitch;
    }

    public float getRoll() {
        return roll;
    }

    public void setRoll(float roll) {
        this.roll = roll;
    }

    public Vector3d getPos() {
        return pos;
    }

    public Vector3d getOrbitPos() {
        return orbitPos;
    }

    @Override
    public void drawMenuBarItems(UIContext context) {

    }

    @Override
    public void drawBody(UIContext context) {
        if (ImGui.isWindowFocused()) {
            context.getContextBuilder().setClickedUISpace(this);
        }

        this.renderToFBO(context);
        ImVec2 pos = ImGui.getCursorPos();
        ImGui.image(texture, 800, 600, 0, 1, 1, 0); // Show the FBO texture
        this.area.setHeight(600);
        this.area.setWidth(800);
        this.area.setX((int) pos.x);
        this.area.setY((int) pos.y);
    }

    private void renderCube() {
        glBegin(GL_QUADS);
        glFrontFace(GL_CCW);
        // Front face
        glColor3f(0.5f, 0.5f, 0.5f); // Red
        glVertex3f(-1.0f, -1.0f, 1.0f);
        glVertex3f(1.0f, -1.0f, 1.0f);
        glVertex3f(1.0f, 1.0f, 1.0f);
        glVertex3f(-1.0f, 1.0f, 1.0f);

        // Back face
        glColor3f(0.3f, 0.3f, 0.3f); // Green
        glVertex3f(-1.0f, -1.0f, -1.0f);
        glVertex3f(-1.0f, 1.0f, -1.0f);
        glVertex3f(1.0f, 1.0f, -1.0f);
        glVertex3f(1.0f, -1.0f, -1.0f);

        // Left face
        glColor3f(0.3f, 0.3f, 0.3f); // Blue
        glVertex3f(-1.0f, -1.0f, -1.0f);
        glVertex3f(-1.0f, -1.0f, 1.0f);
        glVertex3f(-1.0f, 1.0f, 1.0f);
        glVertex3f(-1.0f, 1.0f, -1.0f);

        // Right face
        glColor3f(0.75f, 0.75f, 0.75f); // Yellow
        glVertex3f(1.0f, -1.0f, -1.0f);
        glVertex3f(1.0f, 1.0f, -1.0f);
        glVertex3f(1.0f, 1.0f, 1.0f);
        glVertex3f(1.0f, -1.0f, 1.0f);

        // Top face
        glColor3f(1.0f, 1F, 1F); // Magenta
        glVertex3f(-1.0f, 1.0f, -1.0f);
        glVertex3f(-1.0f, 1.0f, 1.0f);
        glVertex3f(1.0f, 1.0f, 1.0f);
        glVertex3f(1.0f, 1.0f, -1.0f);

        // Bottom face
        glColor3f(0.1f, 0.1f, 0.1f); // Cyan
        glVertex3f(-1.0f, -1.0f, -1.0f);
        glVertex3f(1.0f, -1.0f, -1.0f);
        glVertex3f(1.0f, -1.0f, 1.0f);
        glVertex3f(-1.0f, -1.0f, 1.0f);

        glEnd();
    }

    private void renderToFBO(UIContext context) {
        glBindFramebuffer(GL_FRAMEBUFFER, fbo);
        glViewport(0, 0, 800, 600);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        glEnable(GL_CULL_FACE); // Enable face culling
        glCullFace(GL_BACK);    // Cull back faces
        glEnable(GL_DEPTH_TEST);

        glPushMatrix();
        glLoadIdentity();
        glMatrixMode(GL_PROJECTION_MATRIX);
        glMultMatrixd(this.getProjection().get(new double[16]));

        glPushMatrix();
        glMatrixMode(GL_MODELVIEW_MATRIX);
        glRotatef(-this.pitch, 1, 0, 0);
        glRotatef(-this.yaw, 0, 1, 0);
        glTranslatef(-(float) this.pos.x, -(float) this.pos.y, -(float) this.pos.z);

        glColor3f(1.0f, 1.0f, 0.0f);
        glLineWidth(2.0f);
        glBegin(GL_LINES);
        glVertex3f(0f, 0.0f, 0.0f);
        glVertex3f( 2f, 0.0f, 0.0f);
        glEnd();
        glColor3f(0.0f, 1.0f, 0.0f);
        glBegin(GL_LINES);
        glVertex3f(0f, 0.0f, 0.0f);
        glVertex3f( 0f, 2.0f, 0.0f);
        glEnd();
        glColor3f(0.0f, 0.0f, 1.0f);
        glBegin(GL_LINES);
        glVertex3f(0f, 0.0f, 0.0f);
        glVertex3f( 0f, 0.0f, 10.0f);
        glEnd();

        UIWindow window = context.getCurrentWindow();
        BBContext ctx = ((BBOperatorContext) context.getOperatorContext()).bbContext();
        ctx.database.objects.getChildren().forEach(object -> {
            glPushMatrix();
            glTranslatef(object.translation.x.get(), object.translation.y.get(), object.translation.z.get());
            glRotatef((float) object.rotation.x.get(), 1, 0, 0);
            glRotatef((float) object.rotation.y.get(), 0, 1, 0);
            glRotatef((float) object.rotation.z.get(), 0, 0, 1);
            this.renderCube();
            glPopMatrix();
        });

        glPopMatrix();
        glPopMatrix();
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }

    private void gluPerspective(float fov, float aspect, float zNear, float zFar) {
        float ymax = zNear * (float) Math.tan(Math.toRadians(fov / 2));
        float ymin = -ymax;
        float xmin = ymin * aspect;
        float xmax = ymax * aspect;

        glFrustum(xmin, xmax, ymin, ymax, zNear, zFar);
    }

    private void setupFBO(int width, int height) {
        // Create FBO
        fbo = glGenFramebuffers();
        glBindFramebuffer(GL_FRAMEBUFFER, fbo);

        // Create texture for color attachment
        texture = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, texture);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, width, height, 0, GL_RGB, GL_UNSIGNED_BYTE, 0);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, texture, 0);

        // Create renderbuffer for depth
        depthBuffer = glGenRenderbuffers();
        glBindRenderbuffer(GL_RENDERBUFFER, depthBuffer);
        glRenderbufferStorage(GL_RENDERBUFFER, GL_DEPTH_COMPONENT, width, height);
        glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_RENDERBUFFER, depthBuffer);

        // Check FBO completeness
        if (glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE) {
            throw new RuntimeException("Framebuffer is not complete!");
        }

        glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }
}
