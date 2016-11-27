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

package com.anchorage.demo;

import com.anchorage.docks.containers.common.AnchorageSettings;
import com.anchorage.docks.node.DockNode;
import com.anchorage.docks.node.interfaces.DockNodeCreationListener;
import com.anchorage.docks.stations.DockStation;
import com.anchorage.system.AnchorageSystem;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 *
 * @author Alessio
 */
public class AnchorFX_events extends Application {

    @Override
    public void start(Stage primaryStage) {

        AnchorageSettings.setDockingPositionPreview(false);

        DockStation station = AnchorageSystem.createStation();

        Scene scene = new Scene(station, 1024, 768);

        DockNode node1 = AnchorageSystem.createDock("Events", new MyPanel());
        node1.dock(station, DockNode.DockPosition.LEFT); 

        
        AnchorageSystem.installDefaultStyle();

        primaryStage.setTitle("AnchorFX ");
        primaryStage.setScene(scene);
        primaryStage.show();

    } 
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

}

class MyPanel extends VBox implements DockNodeCreationListener {

    private DockNode myNode;
    private Label floatingLabel;
    private Label draggingLabel;
    private Label maximizedLabel;
  
    
    @Override
    public void onDockNodeCreated(DockNode node) {
        myNode = node;
        
        setSpacing(20);
        
        floatingLabel = new Label("Floating : false");
        myNode.floatingProperty().addListener((ov,oldv,newv)->floatingLabel.setText("Floating : "+newv));
        
        draggingLabel = new Label("Dragging : false");
        myNode.draggingProperty().addListener((ov,oldv,newv)->draggingLabel.setText("Dragging : "+newv));
        
        maximizedLabel = new Label("Maximizing : false");
        myNode.maximizingProperty().addListener((ov,oldv,newv)->maximizedLabel.setText("Maximizing : "+newv));
          
        setStyle("-fx-background-color:rgb(200,200,200); -fx-padding:15;");
        
        getChildren().addAll(floatingLabel,draggingLabel,maximizedLabel);
    }

}
