package com.minechinima.operator.defaults;

import com.minechinima.model.base.BaseStructure;
import com.minechinima.model.exceptions.UserException;
import com.minechinima.operator.IContext;
import com.minechinima.operator.ot.commands.*;
import com.minechinima.operator.ot.commands.flags.OPReturnValue;
import com.minechinima.operator.ot.commands.flags.OpExecuteFlag;
import com.minechinima.operator.ot.commands.flags.OpInvokeFlag;
import com.minechinima.ui.elements.window.UIScreen;
import com.minechinima.ui.elements.window.UISpace;

public class ChangeSpaceTypeOperator extends IPropertyOperator<IContext, ChangeSpaceTypeProp> implements IUndoOperator<IContext> {
    private UIScreen screenChanged;
    private UISpace spaceBefore;

    public ChangeSpaceTypeOperator(ChangeSpaceTypeProp props) {
        super(props);
    }

    //TODO remove pair as return type - own type with more
    @Override
    public OPReturnValue<OpExecuteFlag> execute(IContext context) throws UserException {
        if (context.getUIContext().clickedUIScreen() != null) {
            this.screenChanged = context.getUIContext().clickedUIScreen();
            this.spaceBefore = this.screenChanged.getPanel();
            context.getUIContext().clickedUIScreen().setPanel(this.props().getPanel());

            return new OPReturnValue<>(OpExecuteFlag.FINISHED);
        }

        return new OPReturnValue<>(OpExecuteFlag.CANCELLED);
    }

    @Override
    public OPReturnValue<OpExecuteFlag> onUIPropChange(IContext context, BaseStructure changedProp) throws UserException {
        return null;
    }

    @Override
    public OPReturnValue<OpInvokeFlag> invoke(IContext context) throws UserException {
        return null;
    }

    public IUndoOperatorCommand<IContext> getUndoCommand() {
        return new UndoCommand(this.screenChanged, this.spaceBefore, this.props.getPanel());
    }

    public static class UndoCommand implements IUndoOperatorCommand<IContext> {
        private final UIScreen screenChanged;
        private final UISpace spaceBefore;
        private final UISpace spaceAfter;

        public UndoCommand(UIScreen screenChanged, UISpace spaceBefore, UISpace spaceAfter) {
            this.screenChanged = screenChanged;
            this.spaceBefore = spaceBefore;
            this.spaceAfter = spaceAfter;
        }

        @Override
        public void undo(IContext context) {
            if (this.screenChanged != null) {
                this.screenChanged.setPanel(this.spaceBefore);
            }
        }

        @Override
        public void redo(IContext context) {
            if (this.screenChanged != null) {
                this.screenChanged.setPanel(this.spaceAfter);
            }
        }
    }
}
