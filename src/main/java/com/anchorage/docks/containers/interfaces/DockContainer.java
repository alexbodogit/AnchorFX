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

package com.anchorage.docks.containers.interfaces;

import com.anchorage.docks.node.DockNode;

import javafx.scene.Node;

/**
 *
 * @author Alessio
 */
public interface DockContainer extends DockContainableComponent {
    public void putDock(DockNode node, DockNode.DockPosition position, double percentage);
    public void putDock(DockNode node, DockNode nodeTarget, DockNode.DockPosition position, double percentage);
    
    public void undock(DockNode node);
    
    public int indexOf(Node node);

    public void insertNode(Node node, int index);
    public void removeNode(Node node);

    public boolean isDockVisible(DockNode node);
     
}
