package com.minechinima.ui;

import com.minechinima.input.raw.lwjgl.LWJGLInputEventManager;
import com.minechinima.ui.adapters.lwjgl.ImGuiImplGlfw;
import imgui.*;
import imgui.flag.ImGuiConfigFlags;
import imgui.gl3.ImGuiImplGl3;
import imgui.internal.ImGuiContext;
import org.lwjgl.opengl.GL32;

import static org.lwjgl.glfw.GLFW.glfwGetCurrentContext;
import static org.lwjgl.glfw.GLFW.glfwMakeContextCurrent;

public class ImguiLoader {
    private boolean initialized = false;
    protected long windowID;
    protected ImGuiContext context;
    /**
     * Temporary variable for {@link #beginFrame()} to restore the context that may have been used before our imgui stuff.
     * <br>
     * Might help with compatibility when other mods also use imgui?
     */
    protected ImGuiContext oldContext;
    protected ImGuiImplGlfw imGuiGlfw;
    protected ImGuiImplGl3 imGuiGl3 = new ImGuiImplGl3();
    protected ImGuiIO io;
    protected boolean beginFrame;
    protected ImFont font;
    protected final LWJGLInputEventManager lwjglInputEventManager;

    public ImguiLoader(LWJGLInputEventManager lwjglInputEventManager) {
        this.lwjglInputEventManager = lwjglInputEventManager;
    }

    public ImFont getFont() {
        return this.font;
    }

    public ImGuiImplGlfw getGLFWBackend() {
        return this.imGuiGlfw;
    }

    public void init(long windowID) {
        if (this.initialized) {
            throw new IllegalStateException("Imgui is already initialized, wtf are you doing!?");
        }

        imGuiGlfw = new ImGuiImplGlfw(this.lwjglInputEventManager);

        this.windowID = windowID;
        ImGuiContext oldImGuiContext = ImGui.getCurrentContext();
        this.context = ImGui.createContext();
        ImGui.setCurrentContext(this.context);
        this.io = ImGui.getIO();

        this.io.setIniFilename(null);
        this.io.addConfigFlags(ImGuiConfigFlags.NavEnableKeyboard);
        this.io.addConfigFlags(ImGuiConfigFlags.DockingEnable);
        this.io.addConfigFlags(ImGuiConfigFlags.ViewportsEnable);
        this.io.setConfigDragClickToInputText(true);

        this.imGuiGlfw.init(this.windowID, true);
        this.imGuiGl3.init();

        this.initialized = true;

        this.initFonts();
        //ImGui.setCurrentContext(oldImGuiContext);
    }

    private void initFonts() {
        if (!this.initialized || this.io == null) return;
        this.io.getFonts().setFreeTypeRenderer(true);

        //this.io.getFonts().addFontDefault();

        final ImFontGlyphRangesBuilder rangesBuilder = new ImFontGlyphRangesBuilder(); // Glyphs ranges provide
        rangesBuilder.addRanges(this.io.getFonts().getGlyphRangesDefault());
        //rangesBuilder.addRanges(this.io.getFonts().getGlyphRangesCyrillic());
        //rangesBuilder.addRanges(this.io.getFonts().getGlyphRangesJapanese());
        //rangesBuilder.addRanges(BBIconUnicodes.RANGES);
        //rangesBuilder.addRanges(FontAwesomeIcons._IconRange);

        final ImFontConfig fontConfig = new ImFontConfig();

        final short[] glyphRanges = rangesBuilder.buildRanges();
        //try {
            //this.font = this.io.getFonts().addFontFromMemoryTTF(ResourceLocationUtils.readBytes(new ResourceLocation(BlockbusterForge.MOD_ID, "font/latoregular.ttf")), 14, fontConfig, glyphRanges);
            //fontConfig.setMergeMode(true);
            //this.io.getFonts().addFontFromMemoryTTF(ResourceLocationUtils.readBytes(new ResourceLocation(BlockbusterForge.MOD_ID, "font/tahoma.ttf")), 14, fontConfig, glyphRanges);
            //this.io.getFonts().addFontFromMemoryTTF(ResourceLocationUtils.readBytes(new ResourceLocation(BlockbusterForge.MOD_ID, "font/notosanscjkjp-medium.otf")), 14, fontConfig, glyphRanges);
            //this.io.getFonts().addFontFromMemoryTTF(ResourceLocationUtils.readBytes(new ResourceLocation(BlockbusterForge.MOD_ID, "font/bb-icons_v01.otf")), 14, fontConfig, glyphRanges);
            //this.io.getFonts().addFontFromMemoryTTF(ResourceLocationUtils.readBytes(new ResourceLocation(BlockbusterForge.MOD_ID, "font/fa_regular_400_modified.ttf")), 14, fontConfig, glyphRanges);
            //this.io.getFonts().addFontFromMemoryTTF(ResourceLocationUtils.readBytes(new ResourceLocation(BlockbusterForge.MOD_ID, "font/fa_solid_900.ttf")), 14, fontConfig, glyphRanges);
        //} catch (IOException e) {
        //    throw new RuntimeException(e);
        //}
        this.io.getFonts().build();

        fontConfig.destroy();
    }

    /**
     * @return true when an imgui frame has begun and drawing to imgui is safe.
     */
    public boolean isBeginFrame() {
        return this.beginFrame;
    }

    public void beginFrame() {
        GL32.glClear(GL32.GL_COLOR_BUFFER_BIT | GL32.GL_DEPTH_BUFFER_BIT);
        this.oldContext = ImGui.getCurrentContext();
        ImGui.setCurrentContext(this.context);
        imGuiGl3.newFrame();
        imGuiGlfw.newFrame();
        ImGui.newFrame();
        this.beginFrame = true;
    }

    public void endFrame() {
        try {
            ImGui.render();
            imGuiGl3.renderDrawData(ImGui.getDrawData());

            if (ImGui.getIO().hasConfigFlags(ImGuiConfigFlags.ViewportsEnable)) {
                final long backupWindowPtr = glfwGetCurrentContext();
                ImGui.updatePlatformWindows();
                ImGui.renderPlatformWindowsDefault();
                glfwMakeContextCurrent(backupWindowPtr);
            }
        } finally {
            if (this.oldContext == null) return;
            ImGui.setCurrentContext(this.oldContext);
            this.oldContext = null;
            this.beginFrame = false;
        }
    }
}
