include "globals.mzn";
include "ar_1_1.dzn";
int: n;
int: del_add;
int: del_mul;
int: number_mul;
int: number_add;
set of int: last;
set of int: add;
set of int: mul;
array[1..n] of set of int : dependencies;

output [ show(n) ]