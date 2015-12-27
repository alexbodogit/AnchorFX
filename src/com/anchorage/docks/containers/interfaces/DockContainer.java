/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.anchorage.docks.containers.interfaces;

import com.anchorage.docks.node.DockNode;
import javafx.scene.Node;

/**
 *
 * @author Alessio
 */
public interface DockContainer extends DockContainableComponent {
    public void putDock(DockNode node, DockNode.DOCK_POSITION position);
    public void putDock(DockNode node, DockNode nodeTarget, DockNode.DOCK_POSITION position); 
    
    public void undock(DockNode node);
    
    public int indexOf(Node node);

    public void insertNode(Node node, int index);
    public void removeNode(Node node);

    public boolean isDockVisible(DockNode node);
}
