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

package com.anchorage.docks.containers;

import com.anchorage.docks.containers.common.DockCommons;
import com.anchorage.docks.containers.interfaces.DockContainableComponent;
import com.anchorage.docks.containers.interfaces.DockContainer;
import com.anchorage.docks.containers.subcontainers.DockSplitterContainer;
import com.anchorage.docks.containers.subcontainers.DockTabberContainer;
import com.anchorage.docks.node.DockNode;
import com.anchorage.docks.stations.DockStation;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;

public class SingleDockContainer extends StackPane implements DockContainer {
    
    private DockContainer container;
     
    
    @Override
    public void putDock(DockNode node, DockNode.DockPosition position, double percentage) {
 
        if (getChildren().isEmpty()) {
            getChildren().add(node);
            node.setParentContainer(this);
        } else {
            manageSubContainers(node, position, percentage);
        }
    }
     
    
    @Override
    public void putDock(DockNode node, DockNode nodeTarget, DockNode.DockPosition position, double percentage) {
 
        if (getChildren().get(0) == nodeTarget) {
            manageSubContainers(node, position, percentage);
        }
    }
    
    @Override
    public boolean isDockVisible(DockNode node) {
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
 
    private void manageSubContainers(DockNode node, DockNode.DockPosition position, double percentage) {
        Node existNode = getChildren().get(0);
        
        if (DockCommons.isABorderPosition(position)) {
            getChildren().remove(existNode);
            DockSplitterContainer splitter = DockCommons.createSplitter(existNode, node, position, percentage);
            getChildren().add(splitter);
            splitter.setParentContainer(this);
        } else if (existNode instanceof DockTabberContainer) {
            DockTabberContainer tabber = (DockTabberContainer) existNode;
            tabber.putDock(node, DockNode.DockPosition.CENTER, percentage);
        } else if (existNode instanceof DockSplitterContainer) {
            position = DockNode.DockPosition.BOTTOM;
            DockSplitterContainer splitter = (DockSplitterContainer) existNode;
            node.dock((DockStation)this, position);
        } else {
            getChildren().remove(existNode);
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
