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
package com.anchorage.docks.stations;

import com.anchorage.docks.containers.SingleDockContainer;
import com.anchorage.docks.containers.subcontainers.DockTabberContainer;
import com.anchorage.docks.node.DockNode;
import com.anchorage.docks.containers.zones.DockZones;
import com.anchorage.docks.containers.zones.ZoneSelector;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;
import javafx.stage.Window;

/**
 *
 * @author Alessio
 */
public final class DockStation extends SingleDockContainer {

    private final List<DockNode> nodes;
    private DockZones dockZones;
    private DockNode currentNodeTarget;

    private Node currentNodeBeforeMaximization;
    private DockNode currentNodeMaximized;
    private Parent currentNodeMaximizedParent;
    private boolean substation;

    private DockSubStation dockNodeForSubstation;

    private boolean commonStation;

    private boolean selected = false;

    /**
     * Get the value of selected
     *
     * @return the value of selected
     */
    public boolean isSelected() {
        return selected;
    }

    public DockStation() {
        nodes = new ArrayList<>();
        substation = false;
        buildUI();
    }

    public DockStation(boolean commonStation) {
        this();
        this.commonStation = commonStation;
    }

    public boolean isCommonStation() {
        return commonStation;
    }

    private void buildUI() {
        getStyleClass().add("station");
    }

    public Window getStationWindow() {
        return getStationScene().getWindow();
    }

    public Scene getStationScene() {
        return getScene();
    }

    public boolean isInnerPosition(double x, double y) {

        Window window = getStationWindow();
        Rectangle bounds = new Rectangle(window.getX(), window.getY(), window.getWidth(), window.getHeight());
        return bounds.contains(x, y);
    }

    public void prepareZones(DockNode nodeToMove) {

        if (currentNodeMaximized == null) {
            dockZones = new DockZones(this, nodeToMove);
            dockZones.showZones();
        }

    }

    public boolean isSubStation() {
        return substation;
    }

    public void add(DockNode node) {
        if (!nodes.contains(node)) {
            nodes.add(node);
        }
    }

    public void remove(DockNode node) {
        if (nodes.contains(node)) {
            nodes.remove(node);
            node.stationProperty().set(null);
        }
    }

    public void searchTargetNode(double x, double y) {

        selected = false;
        
        if (currentNodeMaximized != null) {
            return;
        }

        Optional<DockNode> nodeTarget = nodes.stream()
                .filter(node
                        -> !node.floatingProperty().get()
                        && node.checkForTarget(x, y)
                        && node.isDockVisible()).findFirst();

        nodeTarget.map(node -> (Runnable) () -> {

            currentNodeTarget = node;
            dockZones.moveAt(node);

        }).orElse((Runnable) () -> {

            if (currentNodeTarget != null) {
                dockZones.hideCircleZones();

            }

        }).run();

        selected = dockZones.searchArea(x, y);
    }

    public void addOverlay(Node node) {
        getChildren().add(node);
    }

    public void removeOverlay(Node node) {
        getChildren().remove(node);
    }

    public void hideZones() {
        dockZones.close();
    }

    private void manageDragOnSameNode() {

        DockTabberContainer tabContainer = (DockTabberContainer) dockZones.getCurrentNodeTarget().getParentContainer();
        tabContainer.manageDragOnSameNode(dockZones.getCurrentNodeTarget(), dockZones.getCurrentPosition());
    }

    public void finalizeDrag() {

        if (currentNodeMaximized != null) {
            return;
        }

        ZoneSelector selector = dockZones.getCurrentZoneSelector();

        if (selector == null) {
            dockZones.getNodeSource().makeNodeActiveOnFloatableStage(getScene().getWindow(), getScene().getX(), getScene().getY());
        }
        else {
            DockNode.DockPosition position = selector.getPosition();
            if (selector.isStationZone()) {
                dockZones.getNodeSource().undock();
                dockZones.getNodeSource().dock(this, position);
            }
            else {
                manageDockDestination();
            }
        }
 
    }

    public void closeZones()
    {
       dockZones.close();
    }
    
    private void manageDockDestination() {

        if (dockZones.getCurrentNodeTarget() == dockZones.getNodeSource()) {
            if (dockZones.getCurrentNodeTarget().getParentContainer() instanceof DockTabberContainer
                    && dockZones.getCurrentPosition() != DockNode.DockPosition.CENTER) {
                manageDragOnSameNode();
            }
            else {
                dockZones.getNodeSource().restore();
            }
        }
        else {
            dockZones.getNodeSource().undock();
            dockZones.getNodeSource().dock(dockZones.getCurrentNodeTarget(), dockZones.getCurrentPosition());
        }
    }

    public boolean maximize(DockNode node) {

        if (!node.maximizingProperty().get() && getChildren().get(0) != node) {
            currentNodeBeforeMaximization = getChildren().get(0);

            getChildren().remove(0);

            currentNodeMaximizedParent = node.getParent();

            getChildren().add(node);

            currentNodeMaximized = node;

            return true;
        }
        return false;
    }

    public boolean restore(DockNode node) {
        if (currentNodeMaximized != null && currentNodeMaximized == node) {
            Pane panelParent = (Pane) currentNodeMaximizedParent;
            panelParent.getChildren().add(currentNodeMaximized);

            getChildren().add(currentNodeBeforeMaximization);

            currentNodeMaximized = null;
            currentNodeBeforeMaximization = null;

            return true;
        }
        else {
            return false;
        }

    }

    public void markAsSubStation(DockSubStation dockNodeForSubstation) {
        substation = true;
        this.dockNodeForSubstation = dockNodeForSubstation;
    }

    public DockSubStation getDockNodeForSubStation() {
        return dockNodeForSubstation;
    }

}
