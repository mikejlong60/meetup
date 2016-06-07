# Property-Based Testing with Scalacheck

## Abstract 

Property-based testing is a powerful testing paradigm that is available in functional programming languages like Scala and Haskell.  Property-based testing offers a number of advantages over more traditional testing techniques, the main one being that test values are randomly generated and asserted repeatedly. More traditional unit testing tools like jUnit and its extensions(dbUnit, Cactus, JsonUnit ...) require that one explicitly create test data and explicitly assert the outcomes. In this presentation I will show how to do property-based testing using the Scalacheck library. I will show examples of property-based unit tests and property-based integration tests applied to the Postgres RDBMs.

To run this project you need a JDK >= 1.8 and a postgres database in which you have run the create table script in the src/main/resources directory.  You should alter the postgres URL, User, and Password as per your environment.

Run sbt with: "./sbt -Dconfig.file=conf/application.conf"
SBT will download everything necessary for the project.
Once inside SBT type: "test"

## About the Presenter

Mike Long is a software engineer who resides in Winchester, VA. Over the past few years he has been making the transition from Object-Oriented to the Functional Programming paradigm.  He has been programing professionally in Scala for the past 3 years. Before that he worked at IBM where he wrote software at the CIO Innovation Lab. 
