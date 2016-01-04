/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.anchorage.docks.node.interfaces;

import com.anchorage.docks.node.DockNode;

/**
 *
 * @author Alessio
 */
public interface DockNodeCreationListener {
    public void onDockNodeCreated(DockNode node);
}
