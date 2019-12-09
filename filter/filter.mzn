include "globals.mzn";
include "cumulative.mzn";
include "ar_2_4.dzn";
int: n;
int: del_add;
int: del_mul;
int: number_mul;
int: number_add;
set of int: last;
set of int: add;
set of int: mul;
array[1..n] of set of int : dependencies;
array[1..n] of var 0..(n*del_mul + n*del_add): startTimes;
array[1..n] of var int: opCosts;
var 0..((n+1)*(del_mul + del_add)): endTime;
array[1.. length(mul)] of var 1..number_mul: mulResourceUsed;
array[1.. length(add)] of var 1..number_add: addResourceUsed;
array[1.. n] of var 1..(n*del_mul + n*del_add): finishTimes;
% Constraints:

constraint 
  forall( i in 1..length(add)) (
    forall( j in 1..length(add) where i != j) (
      startTimes[add[i]] + opCosts[add[i]] <= startTimes[add[j]] \/
        startTimes[add[j]] + opCosts[add[j]] <= startTimes[add[i]] \/
          addResourceUsed[i] != addResourceUsed[j]
    )
  );
constraint 
  forall( i in 1..length(mul)) (
    forall( j in 1..length(mul) where i != j) (
      startTimes[mul[i]] + opCosts[mul[i]] <= startTimes[mul[j]] \/
        startTimes[mul[j]] + opCosts[mul[j]] <= startTimes[mul[i]] \/
          mulResourceUsed[i] != mulResourceUsed[j]
    )
  );
constraint
  forall(i in 1..length(add)) (
    opCosts[add[i]] = del_add
  );

constraint
  forall(i in 1..length(mul)) (
    opCosts[mul[i]] = del_mul
  );

constraint 
  forall(i in 1..length(dependencies)) (
    forall (j in 1..length(dependencies[i])) (
      startTimes[i] + opCosts[i] <= startTimes[dependencies[i][j]] 
    )
  );
constraint
  forall(i in 1..n) (
    finishTimes[i] = startTimes[i] + opCosts[i]
  );
array[1.. length(add)] of var 1..1: rAdd;
array[1.. length(mul)] of var 1..1: rMul;
array[1.. length(add)] of var del_add..del_add: opAdd;
array[1.. length(mul)] of var del_mul..del_mul: opMul;
array[1.. length(mul)] of var 0..(n*del_mul + n*del_add): startMul;
array[1.. length(add)] of var 0..(n*del_mul + n*del_add): startAdd;
constraint 
  forall ( i in 1.. length(add)) (
    startAdd[i] = startTimes[add[i]]
  );
constraint 
  forall ( i in 1.. length(mul)) (
    startMul[i] = startTimes[mul[i]]
  );  
constraint cumulative(startAdd, opAdd, rAdd, number_add);
constraint cumulative(startMul, opMul, rMul, number_mul);
constraint endTime = max(finishTimes);


solve :: int_search(startTimes, first_fail, indomain_min, complete) 
    minimize endTime;

output [ show(finishTimes), "\n", show(endTime) ]