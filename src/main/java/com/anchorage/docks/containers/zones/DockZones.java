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

import com.anchorage.docks.containers.common.AnchorageSettings;
import static com.anchorage.docks.containers.common.AnchorageSettings.FLOATING_NODE_DROPSHADOW_RADIUS;
import com.anchorage.docks.node.DockNode;
import com.anchorage.docks.node.ui.DockUIPanel;
import com.anchorage.docks.stations.DockStation;
import com.anchorage.docks.stations.DockSubStation;
import java.util.ArrayList;
import java.util.List;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.geometry.BoundingBox;
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
import javafx.util.Duration;

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
    private DockNode.DockPosition currentPosition;

    private ZoneSelector currentZoneSelector = null;

    private Rectangle rectanglePreview;
    private Timeline opacityAnimationPreview;

    static {
        dragTopImage = new Image("dragtop.png");
        dragBottomImage = new Image("dragbottom.png");
        dragLeftImage = new Image("dragleft.png");
        dragRightImage = new Image("dragright.png");
        dragCenterImage = new Image("dragcenter.png");
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
        createRectangleForPreview();

        setAlwaysOnTop(true);
        circleStage.setAlwaysOnTop(true);
    }

    private void createRectangleForPreview() {
        rectanglePreview = new Rectangle(0, 0, 50, 50);
        rectanglePreview.getStyleClass().add("dockzone-rectangle-preview");
        rectanglePreview.setOpacity(0);

        opacityAnimationPreview = new Timeline(new KeyFrame(Duration.seconds(0.5), new KeyValue(rectanglePreview.opacityProperty(), 0.5, Interpolator.LINEAR)));

        opacityAnimationPreview.setAutoReverse(true);
        opacityAnimationPreview.setCycleCount(-1);

        mainRoot.getChildren().add(rectanglePreview);

    }

    private void makeSelectors() {
        selectors = new ArrayList<>();

        if (ownerStation.getChildren().size() > 0) {
            // selectors of station
            selectors.add(new ZoneSelector(dragTopImage, DockNode.DockPosition.TOP, true, mainRoot, (mainRoot.getWidth() - dragTopImage.getWidth()) / 2, OFFSET_IMAGE));
            selectors.add(new ZoneSelector(dragBottomImage, DockNode.DockPosition.BOTTOM, true, mainRoot, (mainRoot.getWidth() - dragTopImage.getWidth()) / 2, mainRoot.getHeight() - dragBottomImage.getHeight() - OFFSET_IMAGE));
            selectors.add(new ZoneSelector(dragLeftImage, DockNode.DockPosition.LEFT, true, mainRoot, OFFSET_IMAGE, (mainRoot.getHeight() - dragLeftImage.getWidth()) / 2));
            selectors.add(new ZoneSelector(dragRightImage, DockNode.DockPosition.RIGHT, true, mainRoot, (mainRoot.getWidth() - dragRightImage.getWidth() - OFFSET_IMAGE), (mainRoot.getHeight() - dragRightImage.getWidth()) / 2));

            // selectors of node
            selectors.add(new ZoneSelector(dragTopImage, DockNode.DockPosition.TOP, false, circleStageRoot, (circleStageRoot.getWidth() - dragTopImage.getWidth()) / 2, OFFSET_IMAGE));
            selectors.add(new ZoneSelector(dragBottomImage, DockNode.DockPosition.BOTTOM, false, circleStageRoot, (circleStageRoot.getWidth() - dragBottomImage.getWidth()) / 2, circleStageRoot.getHeight() - dragBottomImage.getHeight() - OFFSET_IMAGE));
            selectors.add(new ZoneSelector(dragLeftImage, DockNode.DockPosition.LEFT, false, circleStageRoot, OFFSET_IMAGE, (circleStageRoot.getHeight() - dragLeftImage.getHeight()) / 2));
            selectors.add(new ZoneSelector(dragRightImage, DockNode.DockPosition.RIGHT, false, circleStageRoot, circleStageRoot.getWidth() - dragRightImage.getWidth() - OFFSET_IMAGE, (circleStageRoot.getHeight() - dragRightImage.getHeight()) / 2));
            selectors.add(new ZoneSelector(dragCenterImage, DockNode.DockPosition.CENTER, false, circleStageRoot, (circleStageRoot.getWidth() - dragCenterImage.getWidth()) / 2, (circleStageRoot.getHeight() - dragCenterImage.getHeight()) / 2));

        }
        else {
            selectors.add(new ZoneSelector(dragCenterImage, DockNode.DockPosition.CENTER, true, mainRoot, (mainRoot.getWidth() - dragCenterImage.getWidth()) / 2, (mainRoot.getHeight() - dragCenterImage.getHeight()) / 2));
        }

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

    public void hideCircleZones() {

        hidePreview();

        currentNodeTarget = null;

        if (currentZoneSelector != null) {
            currentZoneSelector.reset();
        }

        currentZoneSelector = null;

        currentPosition = null;
        circleStageRoot.setOpacity(0);
    }

    private void checkVisibilityConditions() {
        selectors.
                stream().
                forEach(z -> z.setZoneDisabled(false));

        if (currentNodeTarget == nodeToMove) {
            // disable border zones
            selectors.
                    stream().
                    filter(z -> !z.isStationZone() && z.getPosition() != DockNode.DockPosition.CENTER).
                    forEach(z -> z.setZoneDisabled(true));
        }
    }

    public boolean searchArea(double x, double y) {

        checkVisibilityConditions();

        ZoneSelector selector = selectors.stream()
                .filter(s -> s.overMe(x, y) && !s.isZoneDisabled())
                .findFirst()
                .orElse(null);

        highLight(selector);

        if (selector != null && selector != currentZoneSelector) {
            currentZoneSelector = selector;
            makePreview(currentZoneSelector, currentNodeTarget);
            currentPosition = currentZoneSelector.getPosition();
             
        }
        else {
            if (selector == null) {
                hidePreview();
                currentZoneSelector = null;
                currentPosition = null;
            }
        }
        return selector != null;
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

    public DockNode.DockPosition getCurrentPosition() {
        return currentPosition;
    }

    private void highLight(ZoneSelector selector) {

        selectors.stream().forEach(s -> s.reset());

        if (selector != null) {
            if (selector.isStationZone()) {
                circleStageRoot.setOpacity(0);
            }
            else {
                circleStageRoot.setOpacity(1);
            }
            selector.highLight();

        }
        else {
            if (rectanglePreview != null) {
                hidePreview();
            }
            currentNodeTarget = null;
        }
    }

    private DockStation getStation() {
        return ownerStation;
    }

    private void hidePreview() {
        if (AnchorageSettings.isDockingPositionPreview()) {
            stopAnimatePreview();
            rectanglePreview.setOpacity(0);
        }
    }

    private void showPreview(Bounds sceneBounds, ZoneSelector selector) {

        if (selector.getPosition() == DockNode.DockPosition.LEFT) {

            rectanglePreview.setX(sceneBounds.getMinX());
            rectanglePreview.setY(sceneBounds.getMinY());
            rectanglePreview.setWidth(sceneBounds.getWidth() / 2);
            rectanglePreview.setHeight(sceneBounds.getHeight());
        }

        if (selector.getPosition() == DockNode.DockPosition.TOP) {

            rectanglePreview.setX(sceneBounds.getMinX());
            rectanglePreview.setY(sceneBounds.getMinY());
            rectanglePreview.setWidth(sceneBounds.getWidth());
            rectanglePreview.setHeight(sceneBounds.getHeight() / 2);

        }

        if (selector.getPosition() == DockNode.DockPosition.RIGHT) {

            rectanglePreview.setX(sceneBounds.getMinX() + sceneBounds.getWidth() / 2);
            rectanglePreview.setY(sceneBounds.getMinY());
            rectanglePreview.setWidth(sceneBounds.getWidth() / 2);
            rectanglePreview.setHeight(sceneBounds.getHeight());
        }

        if (selector.getPosition() == DockNode.DockPosition.BOTTOM) {

            rectanglePreview.setX(sceneBounds.getMinX());
            rectanglePreview.setY(sceneBounds.getMinY() + sceneBounds.getHeight() / 2);
            rectanglePreview.setWidth(sceneBounds.getWidth());
            rectanglePreview.setHeight(sceneBounds.getHeight() / 2);

        }

        if (selector.getPosition() == DockNode.DockPosition.CENTER) {

            rectanglePreview.setX(sceneBounds.getMinX());
            rectanglePreview.setY(sceneBounds.getMinY());
            rectanglePreview.setWidth(sceneBounds.getWidth());
            rectanglePreview.setHeight(sceneBounds.getHeight());
        }
    }

    private void animatePreview() {
        stopAnimatePreview();
        rectanglePreview.setOpacity(1);
        rectanglePreview.toFront();
        opacityAnimationPreview.play();
    }

    private void stopAnimatePreview() {
        opacityAnimationPreview.stop();
    }

    private void makePreview(ZoneSelector selector, DockNode currentNodeTarget) {

        if (AnchorageSettings.isDockingPositionPreview()) {

            if (selector.isStationZone()) {
                Bounds sceneBounds = ownerStation.getBoundsInParent();
                showPreview(sceneBounds,selector);
            }
            else {
                Bounds nodeSceneBounds = currentNodeTarget.localToScene(currentNodeTarget.getBoundsInLocal());
                Bounds stationSceneBounds = ownerStation.localToScene(ownerStation.getBoundsInLocal());
                
                Bounds sceneBounds = new BoundingBox(nodeSceneBounds.getMinX()-stationSceneBounds.getMinX(),
                                                     nodeSceneBounds.getMinY()-stationSceneBounds.getMinY(),
                                                     nodeSceneBounds.getWidth(),nodeSceneBounds.getHeight());
                
                if (ownerStation.isSubStation())
                {
                    DockSubStation subStationNode = ownerStation.getDockNodeForSubStation();
                    
                    if (subStationNode.floatingProperty().get())
                    {
                        sceneBounds = new BoundingBox(sceneBounds.getMinX()-FLOATING_NODE_DROPSHADOW_RADIUS-subStationNode.getFloatableStage().getPaddingOffset().getLeft(),
                        sceneBounds.getMinY()-FLOATING_NODE_DROPSHADOW_RADIUS-subStationNode.getFloatableStage().getPaddingOffset().getTop()-DockUIPanel.BAR_HEIGHT,
                        sceneBounds.getWidth(),
                        sceneBounds.getHeight());
                    }
                }
                showPreview(sceneBounds,selector);
            }

            animatePreview();
        }

    }

}
