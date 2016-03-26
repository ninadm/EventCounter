# EventCounter
This is an implementation of an event counter using red-black tree. Each event has two
fields: ID and count, where count is the number of active events with the given ID.
The event counter stores only those ID’s whose count is > 0. Once a count drops
below 1, that ID is removed. Initially, your program must build red-black tree from
a sorted list of n events (i.e., n pairs (ID, count) in ascending order of ID) in O(n)
time. The counter supports the following operations in the specified time
complexity.

Create O(n)
Reduce(ID, m) O(log n)
Increase(ID, m) O(log n)
Count(ID) O(log n)
Inrange(ID1, ID2) O(log n + s) where s is the number of IDs in the range.
Next(ID) O(log n)
Previous(ID) O(log n)

Input Format and execution
 Run as
 $java bbst file-name
 
 file-name has the input format as
 
 n
 
 ID1 count1
 
 ID2 count2
 
 ...
 
 IDn countn
 
 
 
 
