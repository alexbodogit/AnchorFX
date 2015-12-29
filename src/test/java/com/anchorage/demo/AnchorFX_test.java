/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.anchorage.demo;

import com.anchorage.docks.node.DockNode;
import com.anchorage.docks.stations.DockStation;
import com.anchorage.system.AnchorageSystem;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.TableView;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.web.HTMLEditor;
import javafx.stage.Stage;

import java.util.Random;

/**
 *
 * @author Alessio
 */
public class AnchorFX_test extends Application {

    @Override
    public void start(Stage primaryStage) {

        DockStation station = AnchorageSystem.createStation();

        Scene scene = new Scene(station, 800, 500);
      
        DockNode node1 = AnchorageSystem.createDock("Tree", generateRandomTree());
        node1.dock(station, DockNode.DOCK_POSITION.CENTER);
  
        DockNode node2 = AnchorageSystem.createDock("Editor", new HTMLEditor());
        node2.dock(station, DockNode.DOCK_POSITION.RIGHT);
        
        DockNode node3 = AnchorageSystem.createDock("Below the editor", generateRandomTree());
        node3.dock(node2, DockNode.DOCK_POSITION.BOTTOM,0.8);

        AnchorageSystem.installDefaultStyle();

        primaryStage.setTitle("AnchorFX");
        primaryStage.setScene(scene);
        primaryStage.show();
        
        DockNode node4 = AnchorageSystem.createDock("Floating", new TableView());
        node4.dockAsFloating(primaryStage, station, 0, 0, 400, 200);

        
        AnchorageSystem.installDefaultStyle();
    }

    private TreeView<String> generateRandomTree() {
        // create a demonstration tree view to use as the contents for a dock node
        TreeItem<String> root = new TreeItem<String>("Root");
        TreeView<String> treeView = new TreeView<String>(root);
        treeView.setShowRoot(false);

        // populate the prototype tree with some random nodes
        Random rand = new Random();
        for (int i = 4 + rand.nextInt(8); i > 0; i--) {
            TreeItem<String> treeItem = new TreeItem<String>("Item " + i);
            root.getChildren().add(treeItem);
            for (int j = 2 + rand.nextInt(4); j > 0; j--) {
                TreeItem<String> childItem = new TreeItem<String>("Child " + j);
                treeItem.getChildren().add(childItem);
            }
        }

        return treeView;
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

}
