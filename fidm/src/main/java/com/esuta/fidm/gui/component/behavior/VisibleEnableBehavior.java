package com.esuta.fidm.gui.component.behavior;

import org.apache.wicket.Component;
import org.apache.wicket.behavior.Behavior;

/**
 *  @author shood
 * */
public class VisibleEnableBehavior extends Behavior {

    public boolean isVisible(){
        return true;
    }

    public boolean isEnabled(){
        return true;
    }

    @Override
    public void onConfigure(Component component) {
        component.setEnabled(isEnabled());

        boolean isVisible = isVisible();
        component.setVisible(isVisible);
        component.setVisibilityAllowed(isVisible);
    }
}
