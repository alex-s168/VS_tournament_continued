# vst_chunks
A simple API for getting a list of all the thrusters on the current ship.

It is recommended to use CC: VS with this API.

## Namespaces
* `vst_components`

## Functions
### get_thrusters
`get_thrusters()`

Returns a list of all the thrusters on the current ship.
Every element in that list is a table with the following fields:
* `pos` a table with the position of the thruster (x, y, z) relative to the ship's center of mass
* `force` the force of the thruster (x, y, z) relative to the position of the thruster (thrusterSpeed * tier (* thrusterTinyForceMultiplier if tiny)) (* 0 if under water)
