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

package com.anchorage.docks.containers.subcontainers;

import com.anchorage.docks.containers.common.DockCommons;
import com.anchorage.docks.containers.interfaces.DockContainableComponent;
import com.anchorage.docks.containers.interfaces.DockContainer;
import com.anchorage.docks.node.DockNode;
import com.anchorage.docks.stations.DockStation;
import javafx.scene.Node;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.BorderPane;

/**
 *
 * @author Alessio
 */
public final class DockSplitterContainer extends SplitPane implements DockContainer {

    private DockContainer container;
    
   
    @Override
    public void putDock(DockNode node, DockNode.DockPosition position, double percentage)  {
        // NOTHING
    }
  
    
    @Override
    public void putDock(DockNode node, DockNode nodeTarget, DockNode.DockPosition position, double percentage) {

        // get index of current node target
        int indexOfTarget = getItems().indexOf(nodeTarget);

        if (DockCommons.isABorderPosition(position)) {
            // create a splitter with node and nodeTarget
            DockSplitterContainer splitter = DockCommons.createSplitter(nodeTarget, node, position,percentage);

            getChildren().add(splitter);
            splitter.setParentContainer(this);

            // set the splitter on index of target
            getItems().set(indexOfTarget, splitter);
        } else {
            DockTabberContainer tabber = DockCommons.createTabber(nodeTarget, node, position);
            getChildren().add(tabber);
            tabber.setParentContainer(this);
            getItems().set(indexOfTarget, tabber);
        }

        // remove target from splitter
        getItems().remove(nodeTarget);
    }
    
    @Override
    public boolean isDockVisible(DockNode node)
    {
        return true;
    }

    @Override
    public void undock(DockNode node) {
        Node remainingNode = (getItems().get(0) == node) ?  getItems().get(1) : getItems().get(0);
        getItems().remove(remainingNode);
        getItems().remove(node);
        
        // WORKAROUND for bug on split panel. After  getItems().remove(node) the parent of node is not set to null !!!!!
        BorderPane workAroundPane = new BorderPane(node);
        workAroundPane.getChildren().remove(node);
        // END WORKAROUND
 
        int indexInsideParent = getParentContainer().indexOf(this);

        getParentContainer().insertNode(remainingNode, indexInsideParent);
        getParentContainer().removeNode(this);
        
        ((DockContainableComponent) node).setParentContainer(null);
        
    }

    @Override
    public void insertNode(Node node, int index) {
        getItems().set(index, node);
        ((DockContainableComponent) node).setParentContainer(this);
    }

    @Override
    public void removeNode(Node node) {
        getItems().remove(node);
        ((DockContainableComponent) node).setParentContainer(null);
    }

    @Override
    public int indexOf(Node node) {
        return getItems().indexOf(node);
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
