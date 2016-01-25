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
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.anchorage.demo;

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
public class AnchorFX_substations extends Application {

    @Override
    public void start(Stage primaryStage) {

        DockStation station = AnchorageSystem.createStation();

        Scene scene = new Scene(station,  1024, 768);

        DockNode node1 = AnchorageSystem.createDock("Node", new TableView());
        node1.dock(station, DockNode.DockPosition.CENTER);

        DockSubStation station1 = AnchorageSystem.createSubStation(station, "SubStation");
        station1.dock(station, DockNode.DockPosition.LEFT,0.7);

        DockNode subNode = AnchorageSystem.createDock("subNode 1", new TableView());
        subNode.dock(station1, DockNode.DockPosition.LEFT);
        subNode.floatableProperty().set(false);

        DockNode subNode2 = AnchorageSystem.createDock("subNode 2", new TableView());
        subNode2.dock(station1, DockNode.DockPosition.LEFT);

        AnchorageSystem.installDefaultStyle();

        primaryStage.setTitle("AnchorFX SubStation");
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
