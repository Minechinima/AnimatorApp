package com.minechinima.ui.elements;

import com.minechinima.model.values.StringValue;
import com.minechinima.operator.IContext;
import com.minechinima.ui.UIContext;
import imgui.ImGui;
import imgui.ImGuiInputTextCallbackData;
import imgui.callback.ImGuiInputTextCallback;
import imgui.flag.ImGuiInputTextFlags;
import imgui.type.ImString;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

public class UITextEditor<C extends IContext> extends UIValueElement<String, StringValue, C> {
    private ImString textBuffer = new ImString("", 1024*16);
    private boolean isEditing = false;
    /**
     * Whether the text input changed at least once.
     */
    private boolean changed = false;
    private boolean isActiveNotEditing = false;
    /**
     * Whether the user changed the cursor while not editing
     */
    private boolean changedCursorNotEditing = false;
    private int firstCursorChangePos = 0;
    private int prevCursorPosition = 0;
    private Operation currentOperation = null;
    private static final Set<Character> BREAKS = new HashSet<>(List.of(' ', '\n', '\u0009', '\u000B', '\u2409', '\u240B', '\u0D0A', '\r'));
    private String label;

    public UITextEditor(String label) {
        this.label = label;
    }

    @Override
    protected void _onClose() {
        this.isEditing = false;
        this.changed = false;
        this.isActiveNotEditing = false;
        this.changedCursorNotEditing = false;
        this.firstCursorChangePos = 0;
        this.prevCursorPosition = 0;
        this.currentOperation = null;
    }

    @Override
    protected void drawElement(UIContext<C> context) {
        this.textBuffer.set(this.value.get());
        TextInputCallbackAdapter callback = new TextInputCallbackAdapter((data) -> {
            /*
             * With CallbackAlways and CallbackEdit flags enabled, the CallbackAlways will
             * will only be called when the textinput is not edited.
             * Then we can check if the underlying string value changed because of something else than the
             * textinput and update.
             */
            if (data.hasEventFlag(ImGuiInputTextFlags.CallbackAlways)) {
                this.callbackActiveNotEditing(context, data);
            } else if (data.hasEventFlag(ImGuiInputTextFlags.CallbackEdit)) {
                this.callbackEditing(context, data);
            }
        });

        int flags = ImGuiInputTextFlags.CallbackEdit |
                ImGuiInputTextFlags.CallbackAlways |
                ImGuiInputTextFlags.NoUndoRedo;

        ImGui.inputTextMultiline(this.label, this.textBuffer, flags, callback);
    }

    private void callbackActiveNotEditing(UIContext<C> context, ImGuiInputTextCallbackData data) {
        if (data.getCursorPos() != this.prevCursorPosition) {
            /* when user changes the cursor for the first time while not editing, store the last text as undo */
            if (!this.changedCursorNotEditing) {
                if(this.changed) {
                    //app.getController().executeNewTransactionToHistory("",
                    //        () -> BaseValueController.setValue(this.value, this.value.get()));
                }
                this.firstCursorChangePos = data.getCursorPos();
                this.changedCursorNotEditing = true;
            }

           // app.getController().executeNewTransaction("",
           //         () -> BaseValueController.setValue(context.getContextData().currentInputTextCursor, data.getCursorPos()));
        }

        if (!data.getBuf().equals(this.value.get())) {
            data.deleteChars(0, data.getBuf().getBytes().length);
            data.insertChars(0, this.value.get());
            if (!this.value.get().isEmpty()) {
                //data.setCursorPos(MathUtils.clamp(context.getContextData().currentInputTextCursor.get().intValue(), 0, this.value.get().length()));
            }
        }

        this.isActiveNotEditing = true;
        this.isEditing = false;
        this.prevCursorPosition = data.getCursorPos();
    }

    private void callbackEditing(UIContext<C> context, ImGuiInputTextCallbackData data) {
        Operation prevOperation = this.currentOperation;

        if (data.getBufTextLen() < this.value.get().length()) {
            this.currentOperation = Operation.DELETING;
        } else if(data.getBufTextLen() - this.value.get().length() > 1) {
            this.currentOperation = Operation.PASTING;
        } else {
            this.currentOperation = Operation.WRITING;
        }

        /* only change the data when this value has already been edited at least once */
        if (this.changed) {
            /*
             * Cases when to start a new "word" / undo step:
             *  * User switches the operation exception.g. was deleting and now pastes or writes.
             *  * User is pasting continuously -> every new paste is a new undo step
             *  * User is writing
             */
            if (prevOperation != this.currentOperation) {
                //app.getMainController().executeNewTransactionToHistory("",
               //         () -> BaseValueController.setValue(this.value, this.value.get()));
            } else if (BREAKS.contains(data.getBuf().charAt(data.getBufTextLen() - 1)) && this.currentOperation == Operation.WRITING) {
                //app.getMainController().executeNewTransactionToHistory("",
                //        () -> BaseValueController.setValue(this.value, this.value.get()));
            } else if (prevOperation == Operation.PASTING && this.currentOperation == Operation.PASTING) {
                //app.getMainController().executeNewTransactionToHistory("",
                //        () -> BaseValueController.setValue(this.value, this.value.get()));
            }
        }

        //app.getMainController().startOrContinueTransaction("",
        //        () -> BaseValueController.setValue(this.value, data.getBuf()));
        //app.getMainController().startOrContinueTransaction("",
        //        () -> BaseValueController.setValue(context.getContextData().currentInputTextCursor, data.getCursorPos()));

        this.isEditing = true;
        this.prevCursorPosition = data.getCursorPos();
        this.changed = true;
        this.changedCursorNotEditing = false;
    }

    public static class TextInputCallbackAdapter extends ImGuiInputTextCallback {
        private Consumer<ImGuiInputTextCallbackData> callback;

        public TextInputCallbackAdapter(Consumer<ImGuiInputTextCallbackData> consumer) {
            this.callback = consumer;
        }

        @Override
        public void accept(ImGuiInputTextCallbackData imGuiInputTextCallbackData) {
            this.callback.accept(imGuiInputTextCallbackData);
        }
    }

    private enum Operation {
        WRITING,
        DELETING,
        PASTING,
    }
}
