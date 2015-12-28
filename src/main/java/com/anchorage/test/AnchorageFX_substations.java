/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.anchorage.test;

import com.anchorage.docks.node.DockNode;
import com.anchorage.docks.stations.DockStation;
import com.anchorage.docks.stations.DockSubStation;
import com.anchorage.system.AnchorageSystem;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.TableView;
import javafx.stage.Stage;

/**
 *
 * @author Alessio
 */
public class AnchorageFX_substations extends Application {

    @Override
    public void start(Stage primaryStage) {

        DockStation station = AnchorageSystem.createStation();

        Scene scene = new Scene(station, 500, 500);



            DockNode node1 = AnchorageSystem.createDock("1", new TableView());
            node1.dock(station, DockNode.DOCK_POSITION.CENTER);

            DockSubStation station1 = AnchorageSystem.createSubStation(station,"SubStation");
            station1.dock(station, DockNode.DOCK_POSITION.LEFT);
            
            DockNode subNode = AnchorageSystem.createDock("subNode 1", new TableView());
            subNode.dock(station1, DockNode.DOCK_POSITION.LEFT);
            
            DockNode subNode2 = AnchorageSystem.createDock("subNode 2", new TableView());
            subNode2.dock(station1, DockNode.DOCK_POSITION.LEFT);
            
//            DockNode node2 = AnchorageSystem.createDock("2", new TableView());
//            node2.dock(station, DockNode.DOCK_POSITION.CENTER);
//
//            DockNode node3 = AnchorageSystem.createDock("3", new TableView());
//            node3.dock(node2, DockNode.DOCK_POSITION.BOTTOM);

        AnchorageSystem.installDefaultStyle();

            primaryStage.setTitle("Anchorage FX SubStation");
            primaryStage.setScene(scene);
            primaryStage.show();

            //node4.makeNodeActiveOnFloatableStage();

      

    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

}
