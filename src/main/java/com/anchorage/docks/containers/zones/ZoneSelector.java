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
    
    private final DockNode.DockPosition position;
    
    private ImageView iconView;
    private Circle iconCircle;
    
    private final boolean stationZone;
    private final Pane parent;
    
    private boolean zoneDisabled = false;

    public ZoneSelector(Image image, DockNode.DockPosition position, boolean stationZone, Pane parent, double x, double y)
    { 
        this.position = position;
        this.stationZone = stationZone;
        this.parent = parent;
          
        buildUI(image,x,y);
    }
    
    public void setZoneDisabled(boolean value)
    {
        zoneDisabled = value;
        setOpacity((value) ? 0 : 1);
    }
    
    public boolean isZoneDisabled()
    {
        return zoneDisabled;
    }
    
    public void reset()
    {
        setOpacity((zoneDisabled) ? 0 : 0.3);
    }
    
    public void highLight()
    {
        setOpacity((zoneDisabled) ? 0 : 1);
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
    
    public DockNode.DockPosition getPosition()
    {
        return position;
    }
}
