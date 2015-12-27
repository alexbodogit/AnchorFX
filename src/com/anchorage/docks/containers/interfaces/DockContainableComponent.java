/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.anchorage.docks.containers.interfaces;
 

/**
 *
 * @author Alessio
 */
public interface DockContainableComponent {
    
    public void setParentContainer(DockContainer container);
    public DockContainer getParentContainer();
    
}
