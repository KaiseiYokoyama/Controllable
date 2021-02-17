package com.mrcrayfish.controllable.client;

import com.mrcrayfish.controllable.joycon.Report;
import net.minecraftforge.client.settings.KeyConflictContext;

/**
 * Author: MrCrayfish
 */
public class ButtonBindings {
    public static final ButtonBinding JUMP = new ButtonBinding(Report.Buttons.B, "key.jump", "key.categories.movement", KeyConflictContext.IN_GAME);
    public static final ButtonBinding SNEAK = new ButtonBinding(Report.Buttons.ZL, "key.sneak", "key.categories.movement", KeyConflictContext.IN_GAME);
    public static final ButtonBinding SPRINT = new ButtonBinding(Report.Buttons.LStick, "key.sprint", "key.categories.movement", KeyConflictContext.IN_GAME);
    public static final ButtonBinding INVENTORY = new ButtonBinding(Report.Buttons.Y, "key.inventory", "key.categories.inventory", KeyConflictContext.UNIVERSAL);
    public static final ButtonBinding SWAP_HANDS = new ButtonBinding(Report.Buttons.X, "key.swapOffhand", "key.categories.gameplay", KeyConflictContext.IN_GAME);
    public static final ButtonBinding DROP_ITEM = new ButtonBinding(Report.Buttons.Down, "key.drop", "key.categories.gameplay", KeyConflictContext.IN_GAME);
    public static final ButtonBinding USE_ITEM = new ButtonBinding(Report.Buttons.R, "key.use", "key.categories.gameplay", KeyConflictContext.IN_GAME);
    public static final ButtonBinding ATTACK = new ButtonBinding(Report.Buttons.ZR, "key.attack", "key.categories.gameplay", KeyConflictContext.IN_GAME);
    public static final ButtonBinding PICK_BLOCK = new ButtonBinding(Report.Buttons.Y, "key.pickItem", "key.categories.gameplay", KeyConflictContext.IN_GAME);
    public static final ButtonBinding PLAYER_LIST = new ButtonBinding(Report.Buttons.Plus, "key.playerlist", "key.categories.multiplayer", KeyConflictContext.IN_GAME);
    public static final ButtonBinding TOGGLE_PERSPECTIVE = new ButtonBinding(Report.Buttons.Minus, "key.togglePerspective", "key.categories.misc", KeyConflictContext.IN_GAME);
    public static final ButtonBinding CHAT = new ButtonBinding(Report.Buttons.Up, "key.chat", "key.categories.gameplay", KeyConflictContext.IN_GAME);
    public static final ButtonBinding SCREENSHOT = new ButtonBinding(Report.Buttons.Capture, "key.screenshot", "key.categories.misc", KeyConflictContext.UNIVERSAL);
    public static final ButtonBinding SCROLL_LEFT = new ButtonBinding(Report.Buttons.Left, "controllable.key.previousHotbarItem", "key.categories.gameplay", KeyConflictContext.IN_GAME);
    public static final ButtonBinding SCROLL_RIGHT = new ButtonBinding(Report.Buttons.Right, "controllable.key.nextHotbarItem", "key.categories.gameplay", KeyConflictContext.IN_GAME);
    public static final ButtonBinding PAUSE_GAME = new ButtonBinding(Report.Buttons.Home, "controllable.key.pauseGame", "key.categories.misc", KeyConflictContext.UNIVERSAL);
    public static final ButtonBinding NEXT_CREATIVE_TAB = new ButtonBinding(Report.Buttons.L, "controllable.key.previousCreativeTab", "key.categories.inventory", KeyConflictContext.GUI);
    public static final ButtonBinding PREVIOUS_CREATIVE_TAB = new ButtonBinding(Report.Buttons.R, "controllable.key.nextCreativeTab", "key.categories.inventory", KeyConflictContext.GUI);
    public static final ButtonBinding NEXT_RECIPE_TAB = new ButtonBinding(Report.Buttons.ZL, "controllable.key.previousRecipeTab", "key.categories.inventory", KeyConflictContext.GUI);
    public static final ButtonBinding PREVIOUS_RECIPE_TAB = new ButtonBinding(Report.Buttons.ZR, "controllable.key.nextRecipeTab", "key.categories.inventory", KeyConflictContext.GUI);
    public static final ButtonBinding NAVIGATE_UP = new ButtonBinding(Report.Buttons.Up, "controllable.key.moveUp", "key.categories.ui", KeyConflictContext.GUI);
    public static final ButtonBinding NAVIGATE_DOWN = new ButtonBinding(Report.Buttons.Down, "controllable.key.moveDown", "key.categories.ui", KeyConflictContext.GUI);
    public static final ButtonBinding NAVIGATE_LEFT = new ButtonBinding(Report.Buttons.Left, "controllable.key.moveLeft", "key.categories.ui", KeyConflictContext.GUI);
    public static final ButtonBinding NAVIGATE_RIGHT = new ButtonBinding(Report.Buttons.Right, "controllable.key.moveRight", "key.categories.ui", KeyConflictContext.GUI);
    public static final ButtonBinding PICKUP_ITEM = new ButtonBinding(Report.Buttons.A, "controllable.key.pickupItem", "key.categories.ui", KeyConflictContext.GUI, true);
    public static final ButtonBinding QUICK_MOVE = new ButtonBinding(Report.Buttons.B, "controllable.key.quickMove", "key.categories.ui", KeyConflictContext.GUI, true);
    public static final ButtonBinding SPLIT_STACK = new ButtonBinding(Report.Buttons.X, "controllable.key.splitStack", "key.categories.ui", KeyConflictContext.GUI, true);
}
