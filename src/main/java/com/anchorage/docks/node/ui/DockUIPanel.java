/*
 * Copyright 2015-2016 Alessio Vinerbi. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301  USA
 */
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.anchorage.docks.node.ui;

import com.anchorage.docks.node.DockNode;
import com.anchorage.system.AnchorageSystem;
import java.util.Objects;
import javafx.beans.property.StringProperty;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.CacheHint;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

/**
 *
 * @author Alessio
 */
public final class DockUIPanel extends Pane {

    public static final double BAR_HEIGHT = 25;

    private Node nodeContent;
    private Label titleLabel;

    private Pane barPanel;
    private StackPane contentPanel;

    private DockCommandsBox commandsBox;

    private DockNode node;

    private Point2D deltaDragging;

    private boolean subStationStype;
    
    private ImageView iconView;

    private DockUIPanel() {

    }

    public DockUIPanel(String title, Node nodeContent, boolean subStationStype, Image imageIcon) {

        getStylesheets().add("anchorfx.css");
        
        this.subStationStype = subStationStype;

        Objects.requireNonNull(nodeContent);
        Objects.requireNonNull(title);

        this.nodeContent = nodeContent;

        buildNode(title,imageIcon);

        installDragEventMananger();

    }
    
    public void setIcon(Image icon)
    {
        Objects.requireNonNull(icon);
        iconView.setImage(icon);
    }

    private void makeCommands() {
        commandsBox = new DockCommandsBox(node);
        barPanel.getChildren().add(commandsBox);

        commandsBox.layoutXProperty().bind(barPanel.prefWidthProperty().subtract(commandsBox.getChildren().size() * 30 + 10));
        commandsBox.setLayoutY(0);

        titleLabel.prefWidthProperty().bind(commandsBox.layoutXProperty().subtract(10));
    }

    public void setDockNode(DockNode node) {
        this.node = node;
        makeCommands();
    }

    public StringProperty titleProperty() {
        return titleLabel.textProperty();
    }

    private void installDragEventMananger() {
        
        barPanel.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
                 node.maximizeOrRestore();
            }
        });

        barPanel.setOnMouseDragged(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                manageDragEvent(event);
            }
        });
        barPanel.setOnMouseReleased(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                manageReleaseEvent();
            }
        });
    }

    private void manageDragEvent(MouseEvent event) {
        if (!node.draggingProperty().get()) {

            if (!node.maximizingProperty().get()) {
                Bounds bounds = node.localToScreen(barPanel.getBoundsInLocal());

                deltaDragging = new Point2D(event.getScreenX() - bounds.getMinX(),
                                            event.getScreenY() - bounds.getMinY());

                node.enableDraggingOnPosition(event.getScreenX() - deltaDragging.getX(), event.getScreenY() - deltaDragging.getY());
            }

        }
        else {
            if (node.getFloatableStage() != null && !node.getFloatableStage().inResizing() && node.draggingProperty().get()) {

                if (!node.maximizingProperty().get()) {
                    node.moveFloatable(event.getScreenX() - deltaDragging.getX(),
                                       event.getScreenY() - deltaDragging.getY());

                    //node.stationProperty().get().searchTargetNode(event.getScreenX(), event.getScreenY());
                    AnchorageSystem.searchTargetNode(event.getScreenX(), event.getScreenY());
                }
            }
        }
    }

    private void manageReleaseEvent() {
        if (node.draggingProperty().get() && !node.maximizingProperty().get()) {
            AnchorageSystem.finalizeDragging();
        }
    }

    private void buildNode(String title, Image iconImage) {

        Objects.requireNonNull(iconImage);
        Objects.requireNonNull(title);
        
        barPanel = new Pane();

        String titleBarStyle = (!subStationStype) ? "docknode-title-bar" : "substation-title-bar";

        barPanel.getStyleClass().add(titleBarStyle);

        barPanel.setPrefHeight(BAR_HEIGHT);
        barPanel.relocate(0, 0);
        barPanel.prefWidthProperty().bind(widthProperty());

        titleLabel = new Label(title);

        String titleTextStyle = (!subStationStype) ? "docknode-title-text" : "substation-title-text";
        
        iconView = new ImageView(iconImage);
        iconView.setFitWidth(15);
        iconView.setFitHeight(15);
        iconView.setPreserveRatio(false);
        iconView.setSmooth(true);
        iconView.relocate(1,(BAR_HEIGHT-iconView.getFitHeight()) / 2);
         
        titleLabel.getStyleClass().add(titleTextStyle);
        barPanel.getChildren().addAll(iconView,titleLabel);
        titleLabel.relocate(25, 5);

        contentPanel = new StackPane();
        contentPanel.getStyleClass().add("docknode-content-panel");
        contentPanel.relocate(0, BAR_HEIGHT);
        contentPanel.prefWidthProperty().bind(widthProperty());
        contentPanel.prefHeightProperty().bind(heightProperty().subtract(BAR_HEIGHT));
        contentPanel.getChildren().add(nodeContent);
        
        contentPanel.setCache(true);
        contentPanel.setCacheHint(CacheHint.SPEED);
        
        if (nodeContent instanceof Pane)
        {
            Pane nodeContentPane = (Pane)nodeContent;
            nodeContentPane.setMinHeight(USE_COMPUTED_SIZE);
            nodeContentPane.setMinWidth(USE_COMPUTED_SIZE);
            nodeContentPane.setMaxWidth(USE_COMPUTED_SIZE);
            nodeContentPane.setMaxHeight(USE_COMPUTED_SIZE);
        }

        getChildren().addAll(barPanel, contentPanel);
    }

    public StackPane getContentContainer()
    {
        return contentPanel;
    }
    /**
     * Get the value of nodeContent
     *
     * @return the value of nodeContent
     */
    public Node getNodeContent() {
        return nodeContent;
    }
 
    public boolean isMenuButtonEnable(){
        return commandsBox.isMenuButtonEnable();
    }
 
}
