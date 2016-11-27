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
package com.anchorage.docks.containers;

import static com.anchorage.docks.containers.common.AnchorageSettings.FLOATING_NODE_DROPSHADOW_RADIUS;
import static com.anchorage.docks.containers.common.AnchorageSettings.FLOATING_NODE_MINIMUM_HEIGHT;
import static com.anchorage.docks.containers.common.AnchorageSettings.FLOATING_NODE_MINIMUM_WIDTH;
import com.anchorage.docks.node.DockNode;
import com.anchorage.docks.stations.DockStation;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;

/**
 *
 * @author avinerbi
 */
public class StageFloatable extends Stage {

    private Scene scene;
    private DockNode node;
    private StackPane transparentRootPanel;
    private StackPane stackPanelContainer;
    private Window owner;

    private Point2D mousePositionStart;
    private EventHandler<MouseEvent> eventsHandler;

    private double startX;
    private double startWidth;

    private double startY;
    private double startHeight;

    private ImageView imageView;
    private WritableImage ghostImage;

    private StageFloatable() {

    }

    public StageFloatable(DockNode node, Window owner, double startX, double startY) {
        this.node = node;
        this.owner = owner;

        buildUI(startX, startY);

        setAlwaysOnTop(true);
    }

    private void setupMouseEvents() {
        eventsHandler = event -> {

            if (event.getEventType() == MouseEvent.MOUSE_PRESSED) {
                mousePositionStart = new Point2D(event.getScreenX(), event.getScreenY());
                startWidth = getWidth();
                startX = getX();

                startHeight = getHeight();
                startY = getY();
            }

            if (event.getEventType() == MouseEvent.MOUSE_MOVED) {

                boolean sizeRight = valueInRange(event.getX(), stackPanelContainer.getWidth() - stackPanelContainer.getPadding().getLeft(), stackPanelContainer.getWidth());
                boolean sizeLeft = valueInRange(event.getX(), 0, stackPanelContainer.getPadding().getRight());
                boolean sizeTop = valueInRange(event.getY(), 0, stackPanelContainer.getPadding().getTop());
                boolean sizeBottom = valueInRange(event.getY(), stackPanelContainer.getHeight() - stackPanelContainer.getPadding().getBottom(), stackPanelContainer.getHeight());

                Cursor cursor = changeCursor(sizeLeft, sizeRight, sizeTop, sizeBottom);

                getScene().setCursor(cursor);
            }

            if (event.getEventType() == MouseEvent.MOUSE_DRAGGED && (getScene().getCursor() != null && getScene().getCursor() != Cursor.DEFAULT)) {

                if (getScene().getCursor() == Cursor.E_RESIZE || getScene().getCursor() == Cursor.SE_RESIZE || getScene().getCursor() == Cursor.NE_RESIZE) {
                    if (event.getScreenX() - getX() + FLOATING_NODE_DROPSHADOW_RADIUS > FLOATING_NODE_MINIMUM_WIDTH) {
                        setWidth(event.getScreenX() - getX() + FLOATING_NODE_DROPSHADOW_RADIUS);
                    }
                }
                else if (getScene().getCursor() == Cursor.S_RESIZE || getScene().getCursor() == Cursor.SE_RESIZE || getScene().getCursor() == Cursor.SW_RESIZE) {
                    if (event.getScreenY() - getY() + FLOATING_NODE_DROPSHADOW_RADIUS > FLOATING_NODE_MINIMUM_HEIGHT) {
                        setHeight(event.getScreenY() - getY() + FLOATING_NODE_DROPSHADOW_RADIUS);
                    }
                }
                else if (getScene().getCursor() == Cursor.W_RESIZE || getScene().getCursor() == Cursor.NW_RESIZE || getScene().getCursor() == Cursor.SW_RESIZE) {

                    double newX = event.getScreenX() - FLOATING_NODE_DROPSHADOW_RADIUS;
                    double newWidth = startX - newX + startWidth;

                    if (newWidth > FLOATING_NODE_MINIMUM_WIDTH) {
                        setX(newX);
                        setWidth(newWidth);
                    }

                }
                else if (getScene().getCursor() == Cursor.N_RESIZE || getScene().getCursor() == Cursor.NW_RESIZE || getScene().getCursor() == Cursor.NE_RESIZE) {

                    double newY = event.getScreenY() - FLOATING_NODE_DROPSHADOW_RADIUS;
                    double newHeight = startY - newY + startHeight;

                    if (newHeight > FLOATING_NODE_MINIMUM_HEIGHT) {
                        setY(newY);
                        setHeight(newHeight);
                    }

                }

            }
            /*
            else if (event.getEventType() == MouseEvent.MOUSE_RELEASED) {
                // TODO: handle this event?
            }
            */
        };

        stackPanelContainer.addEventFilter(MouseEvent.MOUSE_PRESSED, eventsHandler);
        stackPanelContainer.addEventFilter(MouseEvent.MOUSE_MOVED, eventsHandler);
        stackPanelContainer.addEventFilter(MouseEvent.MOUSE_DRAGGED, eventsHandler);
        stackPanelContainer.addEventFilter(MouseEvent.MOUSE_RELEASED, eventsHandler);
    }

    public boolean inResizing() {
        return (getScene().getCursor() != null && getScene().getCursor() != Cursor.DEFAULT);
    }

    private Cursor changeCursor(boolean sizeLeft, boolean sizeRight, boolean sizeTop, boolean sizeBottom) {
        Cursor cursor = Cursor.DEFAULT;
        if (sizeLeft) {
            if (sizeTop) {
                cursor = Cursor.NW_RESIZE;
            }
            else if (sizeBottom) {
                cursor = Cursor.SW_RESIZE;
            }
            else {
                cursor = Cursor.W_RESIZE;
            }
        }
        else if (sizeRight) {
            if (sizeTop) {
                cursor = Cursor.NE_RESIZE;
            }
            else if (sizeBottom) {
                cursor = Cursor.SE_RESIZE;
            }
            else {
                cursor = Cursor.E_RESIZE;
            }
        }
        else if (sizeTop) {
            cursor = Cursor.N_RESIZE;
        }
        else if (sizeBottom) {
            cursor = Cursor.S_RESIZE;
        }

        return cursor;
    }

    private boolean valueInRange(double value, double min, double max) {
        return (value >= min && value <= max);
    }

    private void removeMouseEvents() {
        removeEventFilter(MouseEvent.MOUSE_PRESSED, eventsHandler);
        removeEventFilter(MouseEvent.MOUSE_MOVED, eventsHandler);
        removeEventFilter(MouseEvent.MOUSE_DRAGGED, eventsHandler);
        removeEventFilter(MouseEvent.MOUSE_RELEASED, eventsHandler);
    }

    private void buildUI(double startX, double startY) {

        initOwner(owner);

        setX(startX - FLOATING_NODE_DROPSHADOW_RADIUS);
        setY(startY - FLOATING_NODE_DROPSHADOW_RADIUS);

        createContainerPanel();

        initStyle(StageStyle.TRANSPARENT);

        scene = new Scene(transparentRootPanel, node.getWidth() + FLOATING_NODE_DROPSHADOW_RADIUS * 2,
                          node.getHeight() + FLOATING_NODE_DROPSHADOW_RADIUS * 2,
                          Color.TRANSPARENT);

        setOnShown(e -> {

            setWidth(getWidth() + stackPanelContainer.getPadding().getLeft() + stackPanelContainer.getPadding().getRight());
            setHeight(getHeight() + stackPanelContainer.getPadding().getTop() + stackPanelContainer.getPadding().getBottom());

        });

        setScene(scene);

    }

    private void createContainerPanel() {

        ghostImage = node.snapshot(new SnapshotParameters(), null);

        imageView = new ImageView(ghostImage);

        stackPanelContainer = new StackPane(imageView);

        transparentRootPanel = new StackPane(stackPanelContainer);

        transparentRootPanel.setPadding(new Insets(FLOATING_NODE_DROPSHADOW_RADIUS));

        transparentRootPanel.setStyle("-fx-background-color:rgba(0,0,0,0);");

        stackPanelContainer.getStyleClass().add("docknode-floating-stack-container-panel");

        stackPanelContainer.setEffect(new DropShadow(BlurType.GAUSSIAN, new Color(0, 0, 0, 0.6), FLOATING_NODE_DROPSHADOW_RADIUS, 0.2, 0, 0));

        stackPanelContainer.relocate(FLOATING_NODE_DROPSHADOW_RADIUS, FLOATING_NODE_DROPSHADOW_RADIUS);
    }

    public void move(double x, double y) {
        setX(x);
        setY(y);
    }

    public void makeNodeActiveOnFloatableStage() {

        DockStation station = node.stationProperty().get();  // save the station

        node.undock();

        node.stationProperty().set(station); // resume station

        stackPanelContainer.getChildren().remove(imageView);

        stackPanelContainer.getChildren().add(node);

        if (node.resizableProperty().get()) {
            setupMouseEvents();
        }
        
         
    }

    public void closeStage() {

        transparentRootPanel.getChildren().removeAll();
        setScene(null);
        hide();
    }

    public void makeFloatable() {

    }

    public Insets getPaddingOffset() {
        return stackPanelContainer.getPadding();
    }

}
