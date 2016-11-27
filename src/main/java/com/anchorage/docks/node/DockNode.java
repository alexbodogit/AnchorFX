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
package com.anchorage.docks.node;

import com.anchorage.docks.containers.StageFloatable;
import static com.anchorage.docks.containers.common.AnchorageSettings.FLOATING_NODE_DROPSHADOW_RADIUS;
import com.anchorage.docks.node.ui.DockUIPanel;
import javafx.scene.layout.StackPane;

import com.anchorage.docks.containers.interfaces.DockContainableComponent;
import com.anchorage.docks.containers.interfaces.DockContainer;
import com.anchorage.docks.containers.subcontainers.DockTabberContainer;
import com.anchorage.docks.node.interfaces.DockNodeCloseRequestHandler;
import com.anchorage.docks.node.interfaces.DockNodeCreationListener;
import com.anchorage.docks.stations.DockStation;
import com.anchorage.docks.stations.DockSubStation;
import com.anchorage.system.AnchorageSystem;
import java.util.Objects;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.stage.Screen;
import javafx.stage.Window;

/**
 *
 * @author Alessio
 */
public class DockNode extends StackPane implements DockContainableComponent {

    public enum DockPosition {

        LEFT,
        RIGHT,
        TOP,
        BOTTOM,
        CENTER
    }

    private DockUIPanel content;

    private BooleanProperty floatableProperty;
    private BooleanProperty closeableProperty;
    private BooleanProperty resizableProperty;
    private BooleanProperty maximizableProperty;

    private ObjectProperty<DockStation> station;
    private ReadOnlyBooleanWrapper floatingProperty;
    private ReadOnlyBooleanWrapper draggingProperty;
    private ReadOnlyBooleanWrapper resizingProperty;
    private ReadOnlyBooleanWrapper maximizingProperty;

    private ReadOnlyObjectWrapper<DockContainer> container;

    private StageFloatable stageFloatable;

    private double floatingStateCoordinateX;
    private double floatingStateCoordinateY;
    private double floatingStateWidth;
    private double floatingStateHeight;

    private DockNodeCloseRequestHandler closeRequestHanlder;

    private DockNode() {

        station = new SimpleObjectProperty<>(null);

        floatableProperty = new SimpleBooleanProperty(true);
        closeableProperty = new SimpleBooleanProperty(true);
        resizableProperty = new SimpleBooleanProperty(true);
        maximizableProperty = new SimpleBooleanProperty(true);

        floatingProperty = new ReadOnlyBooleanWrapper(false);
        draggingProperty = new ReadOnlyBooleanWrapper(false);
        resizingProperty = new ReadOnlyBooleanWrapper(false);
        maximizingProperty = new ReadOnlyBooleanWrapper(false);

        container = new ReadOnlyObjectWrapper<>(null);

    }

    public DockNode(DockUIPanel node) {

        this();

        this.content = node;

        buildUI(node);

        callCreationCallBack();
    }

    private void callCreationCallBack() {
        if (content.getNodeContent() instanceof DockNodeCreationListener) {
            ((DockNodeCreationListener) content.getNodeContent()).onDockNodeCreated(this);
        }
    }

    public void ensureVisibility() {
        if (container.get() instanceof DockTabberContainer) {
            ((DockTabberContainer) container.get()).ensureVisibility(this);
        }
    }

    public void setCloseRequestHandler(DockNodeCloseRequestHandler handler) {
        Objects.requireNonNull(handler);
        closeRequestHanlder = handler;
    }

    public DockNodeCloseRequestHandler getCloseRequestHandler() {
        return closeRequestHanlder;
    }

    public BooleanProperty floatableProperty() {
        return floatableProperty;
    }

    public BooleanProperty closeableProperty() {
        return closeableProperty;
    }

    public BooleanProperty resizableProperty() {
        return resizableProperty;
    }

    public BooleanProperty maximizableProperty() {
        return maximizableProperty;
    }

    public ReadOnlyBooleanProperty floatingProperty() {
        return floatingProperty.getReadOnlyProperty();
    }

    public ReadOnlyBooleanProperty draggingProperty() {
        return draggingProperty.getReadOnlyProperty();
    }

    public ReadOnlyBooleanProperty maximizingProperty() {
        return maximizingProperty.getReadOnlyProperty();
    }

    public ReadOnlyObjectProperty<DockContainer> containerProperty() {
        return container.getReadOnlyProperty();
    }

    public void setIcon(Image icon) {
        content.setIcon(icon);
    }

    public void restore() {
        if (draggingProperty.get()) {
            closeFloatingStage();
        }
        disableDragging();
    }

    public boolean isDockVisible() {
        if (containerProperty().get() == null || floatingProperty.get()) {
            return true;
        } else {
            return container.get().isDockVisible(this);
        }
    }

    public void closeFloatingStage() {

        if (stageFloatable != null) {
            stageFloatable.closeStage();
            stageFloatable = null;
        }

        floatingProperty.set(false);
    }

    public StageFloatable getFloatableStage() {
        return stageFloatable;
    }

    private void buildUI(DockUIPanel panel) {
        getChildren().add(panel);
        panel.setDockNode(this);
    }

    public void moveFloatable(double x, double y) {
        if (!maximizingProperty.get()) {
            stageFloatable.move(x, y);
        }
    }

    public void makeGhostFloatable(Window owner, double x, double y) {

        if (!floatingProperty.get()) {

            stageFloatable = new StageFloatable(this, owner, x, y);
            stageFloatable.show();

            makeTransparent();
        }

    }

    public void enableDraggingOnPosition(double x, double y) {
        draggingProperty.set(true);

        makeGhostFloatable(station.get().getScene().getWindow(), x, y);

        if (!maximizingProperty().get()) {
            AnchorageSystem.prepareDraggingZoneFor(station.get(), this);
        }
    }

    public void disableDragging() {
        draggingProperty.set(false);
        makeOpaque();
    }

    private void makeTransparent() {
        content.setOpacity(0.4);
    }

    private void makeOpaque() {
        content.setOpacity(1);
    }

    public void makeNodeActiveOnFloatableStage(Window owner, double x, double y) {

        disableDragging();

        if (!floatingProperty.get()) {

            if (floatableProperty.get()) {
                if (stageFloatable == null) {
                    makeGhostFloatable(owner, x, y);
                }
                stageFloatable.makeNodeActiveOnFloatableStage();

                floatingProperty.set(true);

            } else {
                closeFloatingStage();
            }
        }

    }

    /**
     * Get the value of station
     *
     * @return the value of station
     */
    public ObjectProperty<DockStation> stationProperty() {
        return station;
    }

    @Override
    public void setParentContainer(DockContainer container) {
        this.container.set(container);
    }

    @Override
    public DockContainer getParentContainer() {
        return container.get();
    }

    public void dockAsFloating(Window owner, DockStation station, double x, double y, double width, double height) {

        if (stationProperty().get() != null) {
            ensureVisibility();
            return;
        }

        station.add(this);
        stageFloatable = new StageFloatable(this, owner, x, y);
        stageFloatable.show();
        stageFloatable.setWidth(width);
        stageFloatable.setHeight(height);
        floatingProperty.set(true);
        this.station.set((DockStation) station);
    }

    public void dock(DockStation station, DockPosition position) {

        if (stationProperty().get() != null) {
            ensureVisibility();
            return;
        }

        station.add(this);
        station.putDock(this, position, 0.5);
        this.station.set((DockStation) station);
    }

    public void dock(DockNode nodeTarget, DockPosition position) {

        if (stationProperty().get() != null) {
            ensureVisibility();
            return;
        }

        nodeTarget.stationProperty().get().add(this);
        nodeTarget.getParentContainer().putDock(this, nodeTarget, position, 0.5);
        station.set(nodeTarget.station.get());
    }

    public void dock(DockStation station, DockPosition position, double percentage) {

        if (stationProperty().get() != null) {
            ensureVisibility();
            return;
        }
        station.add(this);
        station.putDock(this, position, percentage);
        this.station.set((DockStation) station);
    }

    public void dock(DockNode nodeTarget, DockPosition position, double percentage) {

        if (stationProperty().get() != null) {
            ensureVisibility();
            return;
        }

        nodeTarget.stationProperty().get().add(this);
        nodeTarget.getParentContainer().putDock(this, nodeTarget, position, percentage);
        station.set(nodeTarget.station.get());
    }

    public void dock(DockSubStation subStation, DockPosition position) {

        if (stationProperty().get() != null) {
            ensureVisibility();
            return;
        }

        subStation.putDock(this, position, 0.5);
    }

    public void dock(DockSubStation subStation, DockPosition position, double percentage) {

        if (stationProperty().get() != null) {
            ensureVisibility();
            return;
        }

        subStation.putDock(this, position, percentage);
    }

    public void undock() {

        if (stationProperty().get() == null) {
            return;
        }

        boolean isFloating = floatingProperty.get();

        restore();

        if (getParentContainer() != null) {
            getParentContainer().undock(this);
            station.get().remove(this);
        } else if (isFloating) {
            station.get().remove(this);
            station.set(null);
        }
    }

    public DockUIPanel getContent() {
        return content;
    }

    @Override
    public String toString() {
        return content.titleProperty().get();
    }

    public Bounds getSceneBounds() {
        return localToScene(getBoundsInLocal());
    }

    public Bounds getScreenBounds() {
        return localToScreen(getBoundsInLocal());
    }

    public boolean checkForTarget(double x, double y) {

        Point2D screenToScenePoint = getScene().getRoot().screenToLocal(x, y);
        Bounds sceneBounds = getSceneBounds();
        return sceneBounds.contains(screenToScenePoint.getX(), screenToScenePoint.getY());

    }

    public boolean insideTabContainer() {
        return container.get() instanceof DockTabberContainer;
    }

    public void maximizeOrRestore() {

        if (maximizingProperty.get()) {
            restoreLayout();
        } else {
            maximizeLayout();
        }

    }

    public void restoreLayout() {
        if (maximizableProperty.get()) {
            if (floatingProperty.get()) {
                stageFloatable.setX(floatingStateCoordinateX);
                stageFloatable.setY(floatingStateCoordinateY);
                stageFloatable.setWidth(floatingStateWidth);
                stageFloatable.setHeight(floatingStateHeight);
                maximizingProperty.set(false);
            } else if (station.get().restore(this)) {
                maximizingProperty.set(false);
            }

        }
    }

    private void moveStateToFullScreen() {
        // Get current screen of the stage      
        ObservableList<Screen> screens = Screen.getScreensForRectangle(new Rectangle2D(stageFloatable.getX(), stageFloatable.getY(), stageFloatable.getWidth(), stageFloatable.getHeight()));

        // Change stage properties
        Rectangle2D bounds = screens.get(0).getBounds();
        stageFloatable.setX(bounds.getMinX() - FLOATING_NODE_DROPSHADOW_RADIUS);
        stageFloatable.setY(bounds.getMinY() - FLOATING_NODE_DROPSHADOW_RADIUS);
        stageFloatable.setWidth(bounds.getWidth() + FLOATING_NODE_DROPSHADOW_RADIUS * 2);
        stageFloatable.setHeight(bounds.getHeight() + FLOATING_NODE_DROPSHADOW_RADIUS * 2);
    }

    public void maximizeLayout() {
        if (maximizableProperty.get()) {
            if (floatingProperty.get()) {
                floatingStateCoordinateX = stageFloatable.getX();
                floatingStateCoordinateY = stageFloatable.getY();
                floatingStateWidth = stageFloatable.getWidth();
                floatingStateHeight = stageFloatable.getHeight();
                moveStateToFullScreen();

                maximizingProperty.set(true);
            } else if (station.get().maximize(this)) {
                maximizingProperty.set(true);
            }

        }

    }
 
    public boolean isMenuButtonEnable() {
        return content.isMenuButtonEnable();
    }
 
 
}
