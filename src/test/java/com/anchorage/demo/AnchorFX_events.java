/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
        node1.dock(station, DockNode.DOCK_POSITION.LEFT); 
        
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
