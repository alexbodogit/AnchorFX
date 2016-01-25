/*
 * Copyright 2015-2016 Alessio Vinerbi. All rights reserved.
 *
 *  * This library is free software; you can redistribute it and/or
 *  * modify it under the terms of the GNU Lesser General Public
 *  * License as published by the Free Software Foundation; either
 *  * version 2.1 of the License, or (at your option) any later version.
 *  *
 *  * This library is distributed in the hope that it will be useful,
 *  * but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  * Lesser General Public License for more details.
 *  *
 *  * You should have received a copy of the GNU Lesser General Public
 *  * License along with this library; if not, write to the Free Software
 *  * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 *  * MA 02110-1301  USA
 */
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.anchorage.docks.node.ui;

import com.anchorage.docks.containers.SingleDockContainer;
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

    private void changeCommandsState() {

        if (node instanceof DockSubStation || !node.closeableProperty().get()) {
            closeButton.setMouseTransparent(true);
            closeButton.setOpacity(0.4);
        }
        else {
            closeButton.setMouseTransparent(false);
            closeButton.setOpacity(1);
        }

        if (node.getParentContainer() instanceof SingleDockContainer) {
            if (node.maximizableProperty().get()) {
                maximizeRestoreButton.setMouseTransparent(true);
                maximizeRestoreButton.setOpacity(0.4);
            }

        }
        else {
            if (node.maximizableProperty().get()) {
                maximizeRestoreButton.setMouseTransparent(false);
                maximizeRestoreButton.setOpacity(1);
            }
        }
    }

    private void changeStateForFloatingState() {
        if (node.maximizableProperty().get() && node.floatingProperty().get()) {
            maximizeRestoreButton.setMouseTransparent(false);
            maximizeRestoreButton.setOpacity(1);
        }
        else {
            maximizeRestoreButton.setMouseTransparent(true);
            maximizeRestoreButton.setOpacity(0.4);
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
        closeButton.setOnAction(e -> {

            if (node.getCloseRequestHandler() != null) {
                if (node.getCloseRequestHandler().canClose()) {
                    node.undock();
                }
            }
            else {
                node.undock();
            }

        });

        node.closeableProperty().addListener((observer, oldValue, newValue) -> changeCommandsState());
        node.containerProperty().addListener((observer, oldValue, newValue) -> changeCommandsState());
        node.floatingProperty().addListener((observer, oldValue, newValue) -> changeStateForFloatingState());
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
