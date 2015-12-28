/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.anchorage.docks.node.ui;

import com.anchorage.docks.containers.SingleDockContainer;
import com.anchorage.docks.containers.interfaces.DockContainer;
import com.anchorage.docks.node.DockNode;
import com.anchorage.docks.stations.DockSubStation;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

/**
 *
 * @author Alessio
 */
public class DockCommandsBox extends HBox {

    private Button closeButton;
    private Button maximizeRestoreButton;
    private final DockNode node;

    public DockCommandsBox(DockNode node) {
        this.node = node;
        buildUI();
        setSpacing(0);
        setStyle("-fx-background-color:transparent;");
    }

    private void changeStateOfCloseButton() {

        if (!(node instanceof DockSubStation)) {
            if (node.containerProperty().get() == null || !(node.containerProperty().get() instanceof SingleDockContainer)) {
                if (node.closeableProperty().get()) {
                    closeButton.setMouseTransparent(false);
                    closeButton.setOpacity(1);
                }
                else {
                    closeButton.setMouseTransparent(true);
                    closeButton.setOpacity(0.4);
                }
            }
        }
        else {
            closeButton.setMouseTransparent(true);
            closeButton.setOpacity(0.4);
        }
    }

    private void createCloseButton() {
        Image closeImage = new Image("close.png");
        closeButton = new Button() {
            @Override
            public void requestFocus() {
            }
        };

        closeButton.setGraphic(new ImageView(closeImage));
        closeButton.getStyleClass().add("docknode-command-button-close");
        closeButton.setOnAction(e -> node.undock());

        node.closeableProperty().addListener((observer, oldValue, newValue) -> changeStateOfCloseButton());
        node.containerProperty().addListener((observer, oldValue, newValue) -> changeStateOfCloseButton());

        getChildren().add(closeButton);
    }

    private void createMaxRestoreButton() {
        
        Image maximizeImage = new Image("maximize.png");
        Image restoreImage = new Image("restore.png");

        maximizeRestoreButton = new Button() {
            @Override
            public void requestFocus() {
            }
        };

        maximizeRestoreButton.setGraphic(new ImageView(maximizeImage));
        maximizeRestoreButton.getStyleClass().add("docknode-command-button");

        node.maximizingProperty().addListener((observer, oldValue, newValue) -> {

            if (newValue) {
                maximizeRestoreButton.setGraphic(new ImageView(restoreImage));
            }
            else {
                maximizeRestoreButton.setGraphic(new ImageView(maximizeImage));
            }
        });

        maximizeRestoreButton.setOnAction(e -> node.maximizeOrRestore());

        getChildren().add(maximizeRestoreButton);

        node.maximizableProperty().addListener((observer, oldValue, newValue) -> {

            if (newValue) {
                maximizeRestoreButton.setMouseTransparent(false);
                maximizeRestoreButton.setOpacity(1);
            }
            else {
                maximizeRestoreButton.setMouseTransparent(true);
                maximizeRestoreButton.setOpacity(0.4);
            }
        });
    }

    private void buildUI() {

        createMaxRestoreButton();
        createCloseButton();

    }
}
