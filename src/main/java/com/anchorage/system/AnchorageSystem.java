/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.anchorage.system;

import com.anchorage.docks.node.DockNode;
import com.anchorage.docks.node.ui.DockUIPanel;
import com.anchorage.docks.stations.DockStation;
import com.anchorage.docks.stations.DockSubStation;
import com.sun.javafx.css.StyleManager;
import javafx.scene.Parent;

import java.util.ArrayList;
import java.util.List;
import javafx.scene.image.Image;

/**
 *
 * @author Alessio
 */
public class AnchorageSystem {

    private static final List<DockStation> stations;
    private static DockNode nodeInDragging;
    private static final Image emptyIconImage;
    private static final Image emptySubstationIconImage;
    

    static {
        stations = new ArrayList<>();
        emptyIconImage = new Image("empty.png");
        emptySubstationIconImage = new Image("substation.png");
    }

    public static DockStation createStation() {
        DockStation station = new DockStation();
        stations.add(station);
        return station;
    }

    public static DockSubStation createSubStation(DockStation parentStation, String title) {
        DockSubStation station = new DockSubStation(new DockUIPanel(title, new DockStation(),true,emptySubstationIconImage));
        return station;
    }

    public static DockNode createDock(String title, Parent content) {
        DockUIPanel panel = new DockUIPanel(title, content,false,emptyIconImage);
        DockNode container = new DockNode(panel);
        return container;
    }
    
    public static DockNode createDock(String title, Parent content, Image icon) {
        DockUIPanel panel = new DockUIPanel(title, content,false,icon);
        DockNode container = new DockNode(panel);
        return container;
    }

    public static void installDefaultStyle() {
        StyleManager.getInstance()
                .addUserAgentStylesheet("AnchorFX.css");
    }

}
