# Meme Stream [![Build Status](https://travis-ci.org/Edvinas01/meme-stream.svg?branch=master)](https://travis-ci.org/Edvinas01/meme-stream)
For all your meme streaming needs.

## What is this?
Meme stream grabs media from various external sources and stores them inside an internal db. When the time is right, the internal db is accessed and its contents are forwarded to sources of your choice.

## Building and executable
So you want to build this bad boy? First you're gonna need the latest `JDK 8` version. Then, after checking out the project, navigate to root directory and run:
```
./gradlew shadowJar
```

Or if you're on Windows:
```
gradlew.bat shadowJar
```

This will create an executable `.jar` file which you'll be able to run. You will find this file under `build/libs`.

## Running
After you've built the project, simply execute the `.jar` file:
```
java -jar meme-stream-<version>-all.jar
``` 

At first it will create a blank `config.yml` file which you'll have to fill with data if you want to use various modules. Do that and restart the application, and you should be ready to go.