package com.minechinima.ui.panels;

import com.minechinima.model.BBContext;
import com.minechinima.operator.BBOperatorContext;
import com.minechinima.ui.UIContext;
import com.minechinima.ui.elements.UISpace;
import com.minechinima.ui.elements.UIWindow;
import imgui.ImGui;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.*;

public class UIViewport extends UISpace {
    private float cameraX = 0, cameraY = 0, cameraZ = -5; // Camera position
    private float rotationX = 0, rotationY = 0;
    private int fbo;
    private int texture;
    private int depthBuffer;

    public UIViewport(String panelName) {
        super(panelName);
        this.setupFBO(800, 600);
    }

    @Override
    public void drawMenuBarItems(UIContext context) {

    }

    @Override
    public void drawBody(UIContext context) {
        this.renderToFBO(context);
        ImGui.image(texture, 800, 600); // Show the FBO texture
    }

    private void renderCube() {
        glBegin(GL_QUADS);

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
        glFrontFace(GL_CW);
        glEnable(GL_DEPTH_TEST);
        glLoadIdentity();
        gluPerspective(45.0f, 800.0f / 600.0f, 0.1f, 100.0f);
        glTranslatef(cameraX, cameraY, cameraZ);
        glRotatef(rotationX, 1, 0, 0);
        glRotatef(rotationY, 0, 1, 0);
        glScaled(1, -1, 1);

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
