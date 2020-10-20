The Basic Framework for Adam
============================
A basic framework for modeling distributed systems with a separation of the data flow from the control of the system.
This module is used for model checking in AdamMC and for the synthesis in AdamSYNT.

Contains:
---------
- data structures for
  * Petri nets with transits (cp. [ATVA'19](http://arxiv.org/abs/1907.11061)),
  * alternating Büchi tree automata
  * Büchi automata
  * circuits
- tools for Petri nets (including inhibitor arcs), e.g.,
  * rendering into the dot format
  * calculating bounding boxes
- process handler and pool management
- transformer of nondeterminisc automata into deterministic ones
- renderer for circuits into Aiger format
- renderer for Petri nets with transits and the automata into the dot format
- parser for Petri nets with transits and Software defined networks
- generators for examples for Petri nets with transits and Software Defined Networking

Integration:
------------
This modules can be used as separate library and
- is integrated in: [adam](https://github.com/adamtool/adam), [adammc](https://github.com/adamtool/adammc), [adamsynt](https://github.com/adamtool/adamsynt)
- contains the packages: tools, petrinetWithTransits
- depends on the repos: [libs](https://github.com/adamtool/libs).

Related Publications:
---------------------
- _Bernd Finkbeiner, Manuel Gieseking, Jesko Hecking-Harbusch, Ernst-Rüdiger Olderog:_
  [Model Checking Data Flows in Concurrent Network Updates](https://doi.org/10.1007/978-3-030-31784-3_30). ATVA 2019: 515-533 [(Full Version)](http://arxiv.org/abs/1907.11061).

------------------------------------

How To Build
------------
A __Makefile__ is located in the main folder.
First, pull a local copy of the dependencies with
```
make pull_dependencies
```
then build the whole framework with all the dependencies with
```
make
```
To build a single dependencies separately, use, e.g,
```
make tools
```
To delete the build files and clean-up
```
make clean
```
To also delete the files generated by the test and all temporary files use
```
make clean-all
```
Some of the algorithms depend on external libraries or tools. To locate them properly create a file in the main folder
```
touch ADAM.properties
```
and add the absolute paths of the necessary libraries or tools:
```
libraryFolder=<path2repo>/dependencies/libs
aigertools=
dot=dot
```

Tests
-----
Both modules contain tests. You can run the tests by entering the corresponding folder, e.g.,
```
cd tools
```
and run all tests for the module by just typing
```
ant test
```
For testing a specific class in the module _tools_ use for example
```
ant test-class -Dclass.name=uniolunisaar.adam.tests.transformers.TestNDet2Det
```
and for testing a specific method in the module _petriNetWithTransits_ use for example
```
ant test-method -Dclass.name=uniolunisaar.adam.tests.pnwt.parser.sdn.TestSDNTopology -Dmethod.name=topology
```
