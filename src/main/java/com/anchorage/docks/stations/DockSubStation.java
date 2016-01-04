/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.anchorage.docks.stations;


import com.anchorage.docks.node.DockNode;
import com.anchorage.docks.node.ui.DockUIPanel;
import javafx.scene.Scene;
import javafx.stage.Window;

/**
 *
 * @author avinerbi
 */
public final class DockSubStation extends DockNode {
    
     private final DockStation substation;
    /**
     * Get the value of station
     *
     * @return the value of station
     */
    
    public DockStation getSubStation() {
        return substation;
    }

    public Window getStationWindow()
    {
        return stationProperty().get().getStationScene().getWindow();
    }
    
    public Scene getStationScene()
    {
        return stationProperty().get().getStationScene();
    }
    
    public void putDock(DockNode dockNode, DockNode.DOCK_POSITION position, double percentage)  {
        substation.add(dockNode);
        substation.putDock(dockNode, position,percentage);
        dockNode.stationProperty().set(substation);
    }
     
      
    public DockSubStation(DockUIPanel uiPanel) {
        super(uiPanel);
        substation = (DockStation)getContent().getNodeContent();
    }
    
}
