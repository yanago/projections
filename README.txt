## General info
This project contains the code for sample payments forecasting
Sample files are in "resources" folder 
I had taken alternative approach - instead of database I had decided to provide programmatic solution
Rational behind that is that it can be Integrated with Spark and thus much more scalable rather than being bound to specific DB engine 
	
## Technologies
Project is created with:
* Java 8 
* Scala 11 
* Maven

	
## Setup
To run this project:

```
$ cd aspen
$ mvn clean compile package 
$ mvn package exec:java -Dexec.mainClass=projections.Estimator