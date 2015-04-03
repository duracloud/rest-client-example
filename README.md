# Purpose

The purpose of this project is to provide a simple example for interacting with the DuraCloud REST APIs using the DuraCloud Java client.

# Requirements

The following tools will be required:
* Java SDK - either Java 7 or Java 8 will work
* Maven - this code has been tested with version 3.2.5
* Git - to pull down the code (or just download the zip)
* A DuraCloud account
  * If you don't have an account yet, you can request an immediate access to a trial account here: http://duracloud.org/trial-account-request

# Usage
1. Retrieve the code (using either git or the zip download option)
2. On the command line, transition to the project directory and run: 
'mvn clean install'
3. On the command line, transition to the project target directory and run: 
'java -jar simpleapiexample-1.0-driver.jar'
4. A list will be printed to the terminal with the parameters you need to include in order to execute the code. Add the parameters to the command line call above, and watch the magic happen.
5. Update the code with your own tests and experiments. This is meant as a starting point for you to build your own tools. Enjoy!