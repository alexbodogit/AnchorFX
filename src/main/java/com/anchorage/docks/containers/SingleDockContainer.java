/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.anchorage.docks.containers;

import com.anchorage.docks.containers.common.DockCommons;
import com.anchorage.docks.containers.interfaces.DockContainableComponent;
import com.anchorage.docks.containers.interfaces.DockContainer;
import com.anchorage.docks.containers.subcontainers.DockSplitterContainer;
import com.anchorage.docks.containers.subcontainers.DockTabberContainer;
import com.anchorage.docks.node.DockNode;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;

public class SingleDockContainer extends StackPane implements DockContainer {
    
    private DockContainer container;
    
    @Override
    public void putDock(DockNode node, DockNode.DOCK_POSITION position, double percentage) {
        
        if (getChildren().isEmpty()) {
            getChildren().add(node);
            node.setParentContainer(this);
        } else {
            manageSubContainers(node, position,percentage);
        }
    }
    
    @Override
    public void putDock(DockNode node, DockNode nodeTarget, DockNode.DOCK_POSITION position, double percentage) {
        
        if (getChildren().get(0) == nodeTarget)
        {
            manageSubContainers(node, position,percentage);
        }
    }
    
    @Override
    public boolean isDockVisible(DockNode node)
    {
        return true;
    }
    
    @Override
    public int indexOf(Node node) {
        return (getChildren().get(0) == node) ? 0 : -1;
    }
    
    @Override
    public void removeNode(Node node) {
        getChildren().remove(node);
        ((DockContainableComponent) node).setParentContainer(null);
    }
    
    @Override
    public void insertNode(Node node, int index) {
        getChildren().set(index, node);
        
        ((DockContainableComponent) node).setParentContainer(this);
    }
    
    @Override
    public void undock(DockNode node) {
        if (getChildren().get(0) == node) {
            getChildren().remove(node);
            node.setParentContainer(null);
        }
    }
    
    private void manageSubContainers(DockNode node, DockNode.DOCK_POSITION position,double percentage) {
        Node existNode = getChildren().get(0);
        getChildren().remove(existNode);
        
        if (DockCommons.isABorderPosition(position)) {
            DockSplitterContainer splitter = DockCommons.createSplitter(existNode, node, position,percentage);
            getChildren().add(splitter);
            splitter.setParentContainer(this);
        } else {
            DockTabberContainer tabber = DockCommons.createTabber(existNode, node, position);
            getChildren().add(tabber);
            tabber.setParentContainer(this);
        }
    }
    
    @Override
    public void setParentContainer(DockContainer container) {
        this.container = container;
    }
    
    @Override
    public DockContainer getParentContainer() {
        return container;
    }
    
}
