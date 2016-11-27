/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.anchorage.docks.node.ui;

import javafx.animation.FadeTransition;
import javafx.beans.value.ChangeListener;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

/**
 *
 * @author Alessio
 */
public class Popover extends Stage {

    private final Pane content;
    private final Scene scene;
    private DockCommandsBox commandBox;

    private ChangeListener<Number> positionListener;
    private EventHandler<MouseEvent> eventFilter;

    public Popover(DockCommandsBox commandBox, Pane content) {

        this.content = content;
        this.commandBox = commandBox;
        scene = new Scene(content, Color.TRANSPARENT);
        setScene(scene);

        initStyle(StageStyle.TRANSPARENT);
        initOwner(commandBox.getScene().getWindow());

        positionListener = (obs, oldvalue, newvalue) -> hideAndNotify();
        eventFilter = evt -> hideAndNotify();

        setOnShown(e -> {

            Stage ownerStage = (Stage) commandBox.getScene().getWindow();

            commandBox.getScene().addEventFilter(MouseEvent.MOUSE_PRESSED, eventFilter);
            ownerStage.xProperty().addListener(positionListener);
            ownerStage.yProperty().addListener(positionListener);

            content.setOpacity(0);
            FadeTransition fade = new FadeTransition(Duration.seconds(0.2), content);
            fade.setToValue(1);
            fade.play();

        });
    }

    public void show(double x, double y) {
        commandBox.notifyOpenAction();

        setX(x);
        setY(y);
        show();
    }

    public void hideAndNotify() {
        Stage ownerStage = (Stage) commandBox.getScene().getWindow();

        ownerStage.xProperty().removeListener(positionListener);
        ownerStage.yProperty().removeListener(positionListener);
        commandBox.getScene().removeEventFilter(MouseEvent.MOUSE_PRESSED, eventFilter);

        System.out.println("hideAndNotify");

        hide();

        scene.setRoot(new Region()); // elimina il content dalla scena

        commandBox.notifyCloseAction();
    }
}
