# Hero Programming World

This application is designed to teach novice programmers how to use Java. It is a mini programming environment in which the aim is to steer a character around various obstacles and collect a total of 10 coins. To do this, real Java source code can be entered and compiled, which then ensures that the character moves or collects a coin.

The simulation itself runs in its own threaded app, the clock speed of which can be regulated so that you can really follow every single step of the execution. It is also possible to create your own maps or have random maps of any size created. Examples, which are stored in a database and can also be added to, are available in the corresponding tab.

This application is particularly suitable for testing search and wayfinding algorithms.

![screenshot](https://github.com/niklas-holtz/hero-programming-world/blob/main/hero-sim-screennshot.png?raw=true)

## Methods that can control the character

- void walk(): Makes the hero walk forward in his line of sight.
- void swim(): Makes the hero swim forward in his line of sight. This command only works in front of water tiles.
- void turnLeft(): Turns the hero counterclockwise.
- void turnLeft(): Turns the hero clockwise.
- void takeCoin(): Makes the hero pick up the coin on his current tile.
- boolean frontIsClear(): Checks whether the next tile in the line of sight is clear.
- boolean isInventoryFull(): Checks whether the hero's inventory is full.
- boolean isSwimming(): Checks whether the hero is currently in the water.
- boolean getCoin(): Checks whether there is a coin on the current tile.

## Extending the source code

To extend the source code, various methods can be added to the Hero class. The code entered in the application is always executed as a subclass of the Hero class, so that it therefore inherits all the existing methods.

## Tutoring

There is also a function to send the created map and the entered source code to a tutor, who can then confirm or reject it. In order to establish such a connection, a port and an IP must be specified in ```simulator.properties``` so that the student and tutor can connect.
