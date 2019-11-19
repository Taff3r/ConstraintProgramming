include "globals.mzn";
include "truckData.dzn";

int: graphSize;
int: start;
int: nDest;
array[1..nDest] of var int: dest;
int: nEdges;
array[1..nEdges] of var int: from;
array[1..nEdges] of var int: to;
array[1..nEdges] of var int: cost;
array[1.. nDest] of int: NODES;

% Create all nodes
constraint forall (i in nDest) (
    NODES[i] = array2d(from[i], to[i], cost[i]) 
)