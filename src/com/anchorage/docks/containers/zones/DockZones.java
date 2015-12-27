/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.anchorage.docks.containers.zones;

import com.anchorage.docks.node.DockNode;
import com.anchorage.docks.stations.DockStation;
import java.util.ArrayList;
import java.util.List;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 *
 * @author avinerbi
 */
public final class DockZones extends Stage {

    private static final double CIRCLE_RADIUS = 100;
    public static final int OFFSET_IMAGE = 15;

    ///////// MAIN STAGE SCENE AND ROOT
    private Scene scene;
    private Pane mainRoot;

    ////////////////////////////////////
    /////////// CIRCLE EXTERNAL STAGE 
    private Stage circleStage;
    private Scene circleStageScene;
    private Pane circleStageRoot;
    private Circle circleZone;
    ////////////////////////////////////
    
    private static Image dragTopImage;
    private static Image dragBottomImage;
    private static Image dragLeftImage;
    private static Image dragRightImage;
    private static Image dragCenterImage;
  
    private List<ZoneSelector> selectors;

    ///////////////////////////////////////////////
    private DockStation ownerStation;
    private Rectangle currentAreaSelected;

    private DockNode currentNodeTarget;
    private DockNode nodeToMove;
    private DockNode.DOCK_POSITION currentPosition;
    
    private ZoneSelector currentZoneSelector;

    static {
        dragTopImage = new Image("resources/dragtop.png");
        dragBottomImage = new Image("resources/dragbottom.png");
        dragLeftImage = new Image("resources/dragleft.png");
        dragRightImage = new Image("resources/dragright.png");
        dragCenterImage = new Image("resources/dragcenter.png");
    }

    private DockZones() {

    }

    public DockZones(DockStation station, DockNode nodeToMove) {

        this.nodeToMove = nodeToMove;
        this.ownerStation = station;

        initOwner(ownerStation.getStationWindow());
        initStyle(StageStyle.TRANSPARENT);

        buildUI();
        
        buildCircleStage();
        makeSelectors();
        
        setAlwaysOnTop(true);
        circleStage.setAlwaysOnTop(true);
    }

    private void makeSelectors() {
        selectors = new ArrayList<>();
        
        // selectors of station
        selectors.add(new ZoneSelector(dragTopImage, DockNode.DOCK_POSITION.TOP, true, mainRoot, (mainRoot.getWidth()-dragTopImage.getWidth())/2,OFFSET_IMAGE));
        selectors.add(new ZoneSelector(dragBottomImage, DockNode.DOCK_POSITION.BOTTOM, true, mainRoot, (mainRoot.getWidth()-dragTopImage.getWidth())/2,mainRoot.getHeight()-dragBottomImage.getHeight()-OFFSET_IMAGE));
        selectors.add(new ZoneSelector(dragLeftImage, DockNode.DOCK_POSITION.LEFT, true, mainRoot, OFFSET_IMAGE,(mainRoot.getHeight()-dragLeftImage.getWidth())/2));
        selectors.add(new ZoneSelector(dragRightImage, DockNode.DOCK_POSITION.RIGHT, true, mainRoot, (mainRoot.getWidth()-dragRightImage.getWidth()-OFFSET_IMAGE),(mainRoot.getHeight()-dragRightImage.getWidth())/2));
        
        // selectors of node
        selectors.add(new ZoneSelector(dragTopImage, DockNode.DOCK_POSITION.TOP, false, circleStageRoot,(circleStageRoot.getWidth() - dragTopImage.getWidth()) / 2, OFFSET_IMAGE));
        selectors.add(new ZoneSelector(dragBottomImage, DockNode.DOCK_POSITION.BOTTOM, false, circleStageRoot,(circleStageRoot.getWidth() - dragBottomImage.getWidth()) / 2, circleStageRoot.getHeight() - dragBottomImage.getHeight() - OFFSET_IMAGE));
        selectors.add(new ZoneSelector(dragLeftImage, DockNode.DOCK_POSITION.LEFT, false, circleStageRoot,OFFSET_IMAGE, (circleStageRoot.getHeight() - dragLeftImage.getHeight()) / 2));
        selectors.add(new ZoneSelector(dragRightImage, DockNode.DOCK_POSITION.RIGHT, false, circleStageRoot,circleStageRoot.getWidth() - dragRightImage.getWidth() - OFFSET_IMAGE, (circleStageRoot.getHeight() - dragRightImage.getHeight()) / 2));
        selectors.add(new ZoneSelector(dragCenterImage, DockNode.DOCK_POSITION.CENTER, false, circleStageRoot,(circleStageRoot.getWidth() - dragCenterImage.getWidth()) / 2, (circleStageRoot.getHeight() - dragCenterImage.getHeight()) / 2));
    }

    private void buildCircleStage() {
        circleStage = new Stage();
        circleStage.initStyle(StageStyle.TRANSPARENT);
        circleStage.initOwner(this);

        circleZone = new Circle(CIRCLE_RADIUS);
        circleZone.setCenterX(CIRCLE_RADIUS);
        circleZone.setCenterY(CIRCLE_RADIUS);
        circleZone.getStyleClass().add("dockzone-circle-container-selectors");

        circleStageRoot = new Pane(circleZone);
        circleStageRoot.setStyle("-fx-background-color:rgba(0,0,0,0);");

        circleStageScene = new Scene(circleStageRoot, CIRCLE_RADIUS * 2, CIRCLE_RADIUS * 2, Color.TRANSPARENT);

        circleStage.setScene(circleStageScene);
 
        circleStageRoot.setOpacity(0);
    } 

    private void buildUI() {

        mainRoot = new Pane();
        mainRoot.setStyle("-fx-background-color:rgba(0,0,0,0);");

        scene = new Scene(mainRoot, ownerStation.getWidth(), ownerStation.getHeight(), Color.TRANSPARENT);

        setScene(scene);

        Point2D screenOrigin = ownerStation.localToScreen(ownerStation.getBoundsInLocal().getMinX(), ownerStation.getBoundsInLocal().getMinY());

        setX(screenOrigin.getX());
        setY(screenOrigin.getY());

    }
 

    public void showZones() {
        show();
        circleStage.show();
    }

    public void moveAt(DockNode node) {

        currentNodeTarget = node;

        Bounds screenBounds = node.getScreenBounds();

        if (circleStageRoot.opacityProperty().get() == 0) {
            circleStageRoot.setOpacity(1);

        }

        circleStage.setX(screenBounds.getMinX() + (screenBounds.getWidth() - circleStageRoot.getWidth()) / 2);
        circleStage.setY(screenBounds.getMinY() + (screenBounds.getHeight() - circleStageRoot.getHeight()) / 2);

    }

    public void hideZones() {

        currentNodeTarget = null;
        currentPosition = null;
        circleStageRoot.setOpacity(0);
    }

    public void searchArea(double x, double y) {
 
        currentZoneSelector = selectors.stream()
                .filter(s->s.overMe(x, y))
                .findFirst()
                .orElse(null);
        
        highLight(currentZoneSelector);
        
        if (currentZoneSelector != null)
            currentPosition = currentZoneSelector.getPosition();
    }

    public DockNode getCurrentNodeTarget() {
        return currentNodeTarget;
    }

    public ZoneSelector getCurrentZoneSelector() {
        return currentZoneSelector;
    }
    
    

    public DockNode getNodeSource() {
        return nodeToMove;
    }

    public DockNode.DOCK_POSITION getCurrentPosition() {
        return currentPosition;
    }

    private void highLight(ZoneSelector selector) {
         
        selectors.stream().forEach(s->s.setOpacity(0.3));
        if (selector != null)
        {
            if (selector.isStationZone())
                hideZones();
            selector.setOpacity(1);
        }
        else
            currentNodeTarget = null;
    }
    
    private DockStation getStation()
    {
        return ownerStation;
    }

}
