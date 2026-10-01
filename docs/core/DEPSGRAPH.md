# Design
Enums and key objects are used throughout the system to avoid type issues. This avoid the necessity for creating subclasses
for every ComponentNode or OperationNode. The dependency graph can operate mostly without knowing the types.

# Structure

Dependency Graph nodes are split up into DataNode, ComponentNode and OperationNode.

## DataNode
This node represents one data structure that should be evaluated.

## ComponentNode
This node is used to structure data into several units. It helps with clarity, debugging and relations between
complex data. One ComponentNode represents a subset of the data that the DataNode manages. ComponentNodes have an
entry and exit operation and between them custom operations.

## OperationNode
OperationNode represents on logical unit of operation. It is the smallest unit of the dependency graph.

# How relations are handled
Relations can be between any node, but practically they are between OperationNodes. The public API enforces this.
The relation is a directed edge that represents a dependency. If there is an edge from node A to node B, it means that A must be executed before B.

# Updating the graph toplogy
The graph topology needs to be updated when relations change.<br>
Example: the parent of a model is changed.

## Relation updating
Relations can hold data that should be watched for changes to automatically update the graph toplogy.

## Generic updating
For more generic and complex use cases, event listeners can be declared for nodes.
//TODO

# Building
Building starts at the layer of the data components. Implement the class `ComponentBuilder` to create a component.
<br><br>
Building happens in two phases:
- first phase: all the nodes in the dependency graph are built.
- second phase: the relations are built. This ensure that all nodes exist before relations are created. It also allows to create relations between nodes that are not in the same component.
<br><br>
A `ComponentBuilder` holds the builders for OperationNodes and builders for relations between ComponentNodes.

## Build OperationNodes
OperationNodes are built for a component. To build them, register the builder function to the respective `ComponentBuilder`.
One builder function can add multiple OperationNodes to the ComponentNode and create relations between them.
<br><br>
In that function additional builders can be registered to build relations for the created OperationNodes to other nodes in the graph.
These will be executed in the second phase of building.
This should also be used when creating relations inside the same DataNode, as the target components might not be available at the time of building.

## Build relations between Nodes
At every stage of building, separate `RelationBuilder` functions can be registered to build relations in the second phase.
- To build relations between `ComponentNode`, register the builder function to the respective `ComponentBuilder` instance.
- To build relations between `OperationNode`, register the builder function to the helper object inside the `OperationNodesBuilder` function.

This layered approach allows to fetch more contextual information and pass them to the relation builder function.
<br><br>
Example: while building the constraint operation nodes, we have access to the constraint data, which we can pass
to the relation builder function, to create relations specifically for constraint subtypes.
