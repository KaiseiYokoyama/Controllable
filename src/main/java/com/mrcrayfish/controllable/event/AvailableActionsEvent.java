package com.mrcrayfish.controllable.event;

import com.mrcrayfish.controllable.client.Action;
import com.mrcrayfish.controllable.joycon.Report;
import net.minecraftforge.eventbus.api.Event;

import java.util.Map;

/**
 * Author: MrCrayfish
 */
@Deprecated
public class AvailableActionsEvent extends Event
{
    private Map<Report.Buttons, Action> actions;

    public AvailableActionsEvent(Map<Report.Buttons, Action> actions)
    {
        this.actions = actions;
    }

    public Map<Report.Buttons, Action> getActions()
    {
        return this.actions;
    }
}
