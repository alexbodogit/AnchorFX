/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.anchorage.docks.node.interfaces;

/**
 *
 * @author Alessio
 */

@FunctionalInterface
public interface DockNodeCloseHandler {
    
    public boolean canClose();
}
