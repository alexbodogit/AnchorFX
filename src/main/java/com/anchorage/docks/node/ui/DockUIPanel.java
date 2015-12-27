/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.anchorage.docks.node.ui;

import com.anchorage.docks.containers.SingleDockContainer;
import com.anchorage.docks.node.DockNode;
import java.util.Objects;
import javafx.beans.property.StringProperty;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

/**
 *
 * @author Alessio
 */
public final class DockUIPanel extends Pane {

    private static final double BAR_HEIGHT = 30;

    private Parent nodeContent;
    private Label titleLabel;

    private Pane barPanel;
    private StackPane contentPanel;

    private DockCommandsBox commandsBox;

    private DockNode node;

    private Point2D deltaDragging;

    private boolean subStationStype;

    private DockUIPanel() {

    }

    public DockUIPanel(String title, Parent nodeContent, boolean subStationStype) {

        this.subStationStype = subStationStype;

        Objects.requireNonNull(nodeContent);
        Objects.requireNonNull(title);

        this.nodeContent = nodeContent;

        buildNode(title);

        installDragEventMananger();

    }

    private void makeCommands() {
        commandsBox = new DockCommandsBox(node);
        barPanel.getChildren().add(commandsBox);

        commandsBox.layoutXProperty().bind(barPanel.prefWidthProperty().subtract(commandsBox.getChildren().size() * 30 + 20));
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

        barPanel.setOnMouseDragged(event -> manageDragEvent(event));
        barPanel.setOnMouseReleased(event -> manageReleaseEvent());
    }

    private void manageDragEvent(MouseEvent event) {
        if (!node.draggingProperty().get() && !(node.getParentContainer() instanceof SingleDockContainer)) {

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

                    node.getStation().searchTargetNode(event.getScreenX(), event.getScreenY());
                }
            }
        }
    }

    private void manageReleaseEvent() {
        if (node.draggingProperty().get() && !node.maximizingProperty().get()) {
            node.getStation().finalizeDrag();
        }
    }

    private void buildNode(String title) {

        barPanel = new Pane();

        String titleBarStyle = (!subStationStype) ? "docknode-title-bar" : "substation-title-bar";

        barPanel.getStyleClass().add(titleBarStyle);

        barPanel.setPrefHeight(BAR_HEIGHT);
        barPanel.relocate(0, 0);
        barPanel.prefWidthProperty().bind(widthProperty());

        titleLabel = new Label(title);

        String titleTextStyle = (!subStationStype) ? "docknode-title-text" : "substation-title-text";

        titleLabel.getStyleClass().add(titleTextStyle);
        barPanel.getChildren().add(titleLabel);
        titleLabel.relocate(10, 7);

        contentPanel = new StackPane();
        contentPanel.getStyleClass().add("docknode-content-panel");
        contentPanel.relocate(0, BAR_HEIGHT);
        contentPanel.prefWidthProperty().bind(widthProperty());
        contentPanel.prefHeightProperty().bind(heightProperty().subtract(BAR_HEIGHT));
        contentPanel.getChildren().add(nodeContent);

        getChildren().addAll(barPanel, contentPanel);
    }

    /**
     * Get the value of nodeContent
     *
     * @return the value of nodeContent
     */
    public Parent getNodeContent() {
        return nodeContent;
    }

}
