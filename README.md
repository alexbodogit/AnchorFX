# AnchorFX

### Docking framework for JavaFX platform

AnchorFX is a gratis and open source library for JavaFX to create graphical interfaces with docking features 

#### Features

 * Stations and sub stations support
 * Dockable and floatable panels
 * Splitter and Tabs containers support
 * CSS styling


#### Usage

##### Create a DockStation

```java
DockStation station = AnchorageSystem.createStation();
```

Once created the station, we can create the panels and hook them to the station

##### Create a DockNode

```java
Pane myPanel...
DockNode dockNode = AnchorageSystem.createDock("My Title", myPanel);
dockNode.dock(station, DockNode.DOCK_POSITION.CENTER);
```
A DockNode is the window built around your panel. This window has a title and an icon and it can be defined as:

* Closeable
* Resizable
* Maximizable
* Floatable

If we want to create a knot that can not be closed, we will write

```java
dockNode.closeableProperty().set(false);
```

##### Add a DockNode to a Dockstation

To be visible, a node must be associated with a station. To do this procedure, we use the function dock (...) of DockNode

```java
dockNode.dock(station, DockNode.DOCK_POSITION.CENTER);
```

In this case, the node will be added to the station in a central position. When a station is empty, the location will always be central, otherwise, will be taken the position provided and will be changed the layout

You may also add a node by specifying the percentage of placement of the divider. This percentage is only effective if the position is provided different from the central

```java
dockNode.dock(station, DockNode.DOCK_POSITION.CENTER, 0.8);
```

#### Adding a node over another specific node

AnchorFX provides the possibility to add a node in a generic position respect to another node already present in the station.
This feature lets you design a custom layout when your application starts 

```java
dockNode.dock(otherNode, DockNode.DOCK_POSITION.CENTER);
```


##### Create a DockSubStation

A Dock SubStation is a station that also has the functionality of DockNode
The nodes that are associated with a DockSubStation can only be moved within the DockSubStation associated.


```java
 DockSubStation subStation = AnchorageSystem.createSubStation(station, "SubStation");
 dockSubNode.dock(subStation, DockNode.DOCK_POSITION.CENTER);
 
 subStation.dock(station, DockNode.DOCK_POSITION.LEFT,0.7);
```
 
