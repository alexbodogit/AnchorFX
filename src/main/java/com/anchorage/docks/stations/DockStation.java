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
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
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

    public DockStation() {
        nodes = new ArrayList<>();
        buildUI();
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

    public void add(DockNode node) {
        if (!nodes.contains(node)) {
            nodes.add(node);
        }
    }

    public void remove(DockNode node) {
        if (nodes.contains(node)) {
            nodes.remove(node);
        }
    }

    public void searchTargetNode(double x, double y) {

        if (currentNodeMaximized != null) 
            return;
        
        Optional<DockNode> nodeTarget = nodes.stream()
                .filter(node
                        -> !node.floatingProperty().get()
                        && node.checkForTarget(x, y)
                        && node.isDockVisible()).findFirst();

        nodeTarget.map(node -> (Runnable) () -> {

            currentNodeTarget = node;
            dockZones.moveAt(node);
            dockZones.searchArea(x, y);

        }).orElse((Runnable) () -> {

            if (currentNodeTarget != null) {
                dockZones.hideZones();

            }

        }).run();

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

        if (currentNodeMaximized != null) return; 
        
        ZoneSelector selector = dockZones.getCurrentZoneSelector();

        if (selector == null) {
            dockZones.getNodeSource().makeNodeActiveOnFloatableStage(getScene().getWindow(), getScene().getX(), getScene().getY());
        }
        else {
            DockNode.DOCK_POSITION position = selector.getPosition();
            if (selector.isStationZone()) {
                dockZones.getNodeSource().undock();
                dockZones.getNodeSource().dock(this, position);
            }
            else {
                manageDockDestination();
            }
        }

        dockZones.close();
    }

    private void manageDockDestination() {
        
        if (dockZones.getCurrentNodeTarget() == dockZones.getNodeSource()) {
            if (dockZones.getCurrentNodeTarget().getParentContainer() instanceof DockTabberContainer
                    && dockZones.getCurrentPosition() != DockNode.DOCK_POSITION.CENTER) {
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
        else
            return false;

    }

    
}
