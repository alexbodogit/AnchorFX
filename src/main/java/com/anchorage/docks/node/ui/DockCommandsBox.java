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
    
    private void createCloseButton()
    {
        Image closeImage = new Image("resources/close.png");
        closeButton = new Button() {
            @Override
            public void requestFocus() {
            }
        };

        closeButton.setGraphic(new ImageView(closeImage));
        closeButton.getStyleClass().add("docknode-command-button-close");
        closeButton.setOnAction(e -> node.undock());
        
        node.containerProperty().addListener((observer, oldValue, newValue) -> {

            DockContainer container = (DockContainer) newValue;

            closeButton.setOpacity((container == null || !(container instanceof SingleDockContainer)) ? 1 : 0.4);
            closeButton.setMouseTransparent((container == null || !(container instanceof SingleDockContainer)) ? false : true);

        });
        
        getChildren().add(closeButton);
    }
    
    private void createMaxRestoreButton()
    {
        Image maximizeImage = new Image("resources/maximize.png");
        Image restoreImage = new Image("resources/restore.png");
 
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
        
        maximizeRestoreButton.setOnAction(e->node.maximizeOrRestore());
        
        getChildren().add(maximizeRestoreButton);
    }

    private void buildUI() {
        
        createMaxRestoreButton();
        if (!(node instanceof DockSubStation))
            createCloseButton();
        
    }
}
