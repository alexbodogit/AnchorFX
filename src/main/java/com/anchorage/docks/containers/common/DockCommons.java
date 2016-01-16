/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.anchorage.docks.containers.common;

import com.anchorage.docks.containers.interfaces.DockContainableComponent;
import com.anchorage.docks.containers.subcontainers.DockSplitterContainer;
import com.anchorage.docks.containers.subcontainers.DockTabberContainer;
import com.anchorage.docks.node.DockNode;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.Tab;

/**
 *
 * @author Alessio
 */
public class DockCommons {

    public static boolean isABorderPosition(DockNode.DOCK_POSITION position) {
        return position != DockNode.DOCK_POSITION.CENTER;
    }

    public static DockSplitterContainer createSplitter(Node existNode, Node newNode, DockNode.DOCK_POSITION position, double percentage) {
        DockSplitterContainer splitter = createEmptySplitter();

        
        if (position == DockNode.DOCK_POSITION.BOTTOM || position == DockNode.DOCK_POSITION.TOP) {
            splitter.setOrientation(Orientation.VERTICAL);
        }

        DockContainableComponent existContainableComponent = (DockContainableComponent) existNode;
        DockContainableComponent newContainableComponent = (DockContainableComponent) newNode;

        existContainableComponent.setParentContainer(splitter);
        newContainableComponent.setParentContainer(splitter);

        if (position == DockNode.DOCK_POSITION.BOTTOM || position == DockNode.DOCK_POSITION.RIGHT) {
            splitter.getItems().addAll(existNode, newNode);
        } else {
            splitter.getItems().addAll(newNode, existNode);
        } 
        
        splitter.getStyleClass().add("docknode-split-pane");
        splitter.setDividerPositions(percentage);
        return splitter;
    }

    public static DockSplitterContainer createEmptySplitter() {
        return new DockSplitterContainer();
    }

    public static DockTabberContainer createTabber(Node existNode, Node newNode, DockNode.DOCK_POSITION position) {

        if (existNode instanceof DockNode && newNode instanceof DockNode) {
            DockNode existDockNode = (DockNode) existNode;
            DockNode newDockNode = (DockNode) newNode;
 
            DockTabberContainer tabber = new DockTabberContainer();
            Tab existTabPanel = new Tab(existDockNode.getContent().titleProperty().get());
            Tab newTabPanel = new Tab(newDockNode.getContent().titleProperty().get());
            
            existTabPanel.setOnCloseRequest(event->{
             
                   existDockNode.undock();
                   event.consume();
            
            });
            
            newTabPanel.setOnCloseRequest(event->{
             
                   newDockNode.undock();
                   event.consume();
            
            });
            
            existTabPanel.closableProperty().bind(existDockNode.closeableProperty());
            newTabPanel.closableProperty().bind(newDockNode.closeableProperty());

            existTabPanel.setContent(existDockNode);
            newTabPanel.setContent(newDockNode);

            DockContainableComponent existContainableComponent = (DockContainableComponent) existNode;
            DockContainableComponent newContainableComponent = (DockContainableComponent) newNode;

            existContainableComponent.setParentContainer(tabber);
            newContainableComponent.setParentContainer(tabber);

            tabber.getTabs().addAll(existTabPanel, newTabPanel);
            
            tabber.getStyleClass().add("docknode-tab-pane");
            
            newDockNode.ensureVisibility();
            
            return tabber;
        }  
        return null;
    }
}
