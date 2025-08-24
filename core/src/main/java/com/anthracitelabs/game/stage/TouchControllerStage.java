package com.anthracitelabs.game.stage;

import com.anthracitelabs.game.Constants;
import com.anthracitelabs.game.input.GameInput;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.Viewport;

public class TouchControllerStage extends Stage {
    private Button[] mControllerButtons;
    private Rectangle[] mControllerButtonRectangles;
    private Skin mSkin;
    private boolean[] mInputs;
    private boolean[] mPrevInputs;

    private Table buttonsTable, topTable;
    private Group mGroup;

    public TouchControllerStage(Viewport viewport, SpriteBatch batch, Skin skin, boolean[] inputs, boolean[] prevInputs) {
        super(viewport, batch);

        mSkin = skin;
        mControllerButtons = new Button[Constants.TOUCH_BUTTONS_COUNT];
        mControllerButtonRectangles = new Rectangle[Constants.TOUCH_BUTTONS_COUNT];
        mInputs = inputs;
        mPrevInputs = prevInputs;

        mGroup = new Group();
        mGroup.setWidth(getViewport().getWorldWidth());
        mGroup.setHeight(getViewport().getWorldHeight());
        this.addActor(mGroup);

        topTable = new Table();
        buttonsTable = new Table();
        //buttonsTable.setDebug(true);
        topTable.setFillParent(true);
        buttonsTable.setFillParent(true);
        buttonsTable.bottom();
        topTable.top();
        mGroup.addActor(buttonsTable);
        mGroup.addActor(topTable);

        createButtons();
    }

    private void createButtons() {
        mControllerButtons[Constants.TOUCH_BUTTON_PAUSE] = new Button(mSkin, "pause");
        mControllerButtons[Constants.TOUCH_BUTTON_LEFT] = new Button(mSkin, "left");
        mControllerButtons[Constants.TOUCH_BUTTON_RIGHT] = new Button(mSkin, "right");
        mControllerButtons[Constants.TOUCH_BUTTON_UP] = new Button(mSkin, "up");
        mControllerButtons[Constants.TOUCH_BUTTON_ACTION] = new Button(mSkin, "action");

        topTable.add().expandX();
        topTable.add(mControllerButtons[Constants.TOUCH_BUTTON_PAUSE]).width(this.getViewport().getWorldHeight() / 16).height(this.getViewport().getWorldHeight() / 16);

        float buttonSize = this.getViewport().getWorldHeight() / 4;
        buttonsTable.add().colspan(3).expandX();
        buttonsTable.add(mControllerButtons[Constants.TOUCH_BUTTON_UP]).width(buttonSize).height(buttonSize);
        buttonsTable.row();
        buttonsTable.add(mControllerButtons[Constants.TOUCH_BUTTON_LEFT]).width(buttonSize).height(buttonSize);
        buttonsTable.add(mControllerButtons[Constants.TOUCH_BUTTON_RIGHT]).width(buttonSize).height(buttonSize);
        buttonsTable.add().expandX();
        buttonsTable.add(mControllerButtons[Constants.TOUCH_BUTTON_ACTION]).width(buttonSize).height(buttonSize);

        this.draw();

        for(int i = 0; i < Constants.TOUCH_BUTTONS_COUNT; i++) {
            createRectangle(mControllerButtons[i], i);
        }

    }

    public void touchDown(Vector2 coordinates) {
        if (mControllerButtonRectangles[Constants.TOUCH_BUTTON_LEFT].contains(coordinates)) {
            mInputs[GameInput.GoLeft] = true;
        } else if (mControllerButtonRectangles[Constants.TOUCH_BUTTON_RIGHT].contains(coordinates)) {
            mInputs[GameInput.GoRight] = true;
        } else if (mControllerButtonRectangles[Constants.TOUCH_BUTTON_UP].contains(coordinates)) {
            mInputs[GameInput.GoUp] = true;
        } else if (mControllerButtonRectangles[Constants.TOUCH_BUTTON_ACTION].contains(coordinates)) {
            mInputs[GameInput.Action1] = true;
        } else if (mControllerButtonRectangles[Constants.TOUCH_BUTTON_PAUSE].contains(coordinates)) {
            mInputs[GameInput.Pause] = true;
        }
    }

    public void toggleButton() {
        if (mInputs[GameInput.GoLeft] != mPrevInputs[GameInput.GoLeft]) {
            mControllerButtons[Constants.TOUCH_BUTTON_LEFT].toggle();
        }
        if (mInputs[GameInput.GoRight] != mPrevInputs[GameInput.GoRight]) {
            mControllerButtons[Constants.TOUCH_BUTTON_RIGHT].toggle();
        }
        if (mInputs[GameInput.GoUp] != mPrevInputs[GameInput.GoUp]) {
            mControllerButtons[Constants.TOUCH_BUTTON_UP].toggle();
        }
        if (mInputs[GameInput.Action1] != mPrevInputs[GameInput.Action1]) {
            mControllerButtons[Constants.TOUCH_BUTTON_ACTION].toggle();
        }
        if (mInputs[GameInput.Pause] != mPrevInputs[GameInput.Pause]) {
            mControllerButtons[Constants.TOUCH_BUTTON_PAUSE].toggle();
        }
    }

    public void createRectangle(Button button, int index) {
        Vector2 coords = button.getStage().stageToScreenCoordinates(button.localToStageCoordinates(new Vector2(0, 0)));
        coords.y = Math.abs(coords.y - getViewport().getWorldHeight());
        mControllerButtonRectangles[index] = new Rectangle(coords.x, coords.y,
                button.getWidth(), button.getHeight());
    }
}
