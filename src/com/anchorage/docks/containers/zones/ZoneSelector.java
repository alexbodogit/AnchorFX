/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.anchorage.docks.containers.zones;

import com.anchorage.docks.node.DockNode;
import javafx.geometry.Bounds;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;

/**
 *
 * @author Alessio
 */
public final class ZoneSelector extends Pane {
    
    private final DockNode.DOCK_POSITION position;
    
    private ImageView iconView;
    private Circle iconCircle;
    
    private final boolean stationZone;
    private final Pane parent;
    
    

    public ZoneSelector(Image image, DockNode.DOCK_POSITION position, boolean stationZone,Pane parent, double x, double y)
    { 
        this.position = position;
        this.stationZone = stationZone;
        this.parent = parent;
          
        buildUI(image,x,y);
    }
    
    private void buildUI(Image image, double x, double y)
    {    
        setPrefWidth(image.getWidth());
        setPrefHeight(image.getHeight());
        
        iconView = new ImageView(image);  
        setStyle("-fx-background-color:rgba(0,0,0,0);");
        
        iconCircle = new Circle(image.getWidth()/2+10);
        iconCircle.setCenterX(getPrefWidth() / 2);
        iconCircle.setCenterY(getPrefHeight() / 2);
        iconCircle.getStyleClass().add("dockzone-circle-selector");
        
        iconView.relocate((getPrefWidth()-image.getWidth()) / 2, (getPrefWidth()-image.getHeight()) / 2);
        
        getChildren().addAll(iconCircle,iconView);
        
        parent.getChildren().add(this);
        relocate(x, y); 
    }
    
    
    /**
     * Get the value of stationZone
     *
     * @return the value of stationZone
     */
    public boolean isStationZone() {
        return stationZone;
    }
    
    public boolean overMe(double x, double y)
    {
        Bounds screenBounds = localToScreen(getBoundsInLocal());
        return (screenBounds.contains(x, y));
    }
    
    public DockNode.DOCK_POSITION getPosition()
    {
        return position;
    }
}
