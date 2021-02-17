package com.mrcrayfish.controllable;

import com.mrcrayfish.controllable.joycon.Report;

import java.util.HashMap;

public class ButtonStates {
    private HashMap<Report.Buttons, Boolean> states;

    public ButtonStates() {
        this.states = new HashMap<>();
    }

    public ButtonStates(Report report) {
        HashMap<Report.Buttons, Boolean> states = new HashMap<>();

        for (Report.Buttons button: Report.Buttons.values()) {
            states.put(button, report.pressedButtons.contains(button));
        }

        this.states = states;
    }

    public boolean getState(Report.Buttons button) {
        Boolean value = this.states.get(button);

        if (value != null) {
            return value;
        } else {
            return false;
        }
    }

    public void setState(Report.Buttons button, boolean state) {
        this.states.put(button, state);
    }
}
