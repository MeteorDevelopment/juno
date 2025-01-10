# Juno
Juno is a Java rendering api.  
The Juno project is split into multiple separate packages.

## Maven
All the packages listed below are available on the Meteor maven.  
Link: https://maven.meteordev.org/releases

## Packages

### Juno API
`org.meteordev:juno-api:1.0.0`  
The core Juno API.
Look at the [example](https://github.com/MeteorDevelopment/juno/tree/master/example) for standalone usage of the API.

### Juno OpenGL
`org.meteordev:juno-opengl:1.0.1`  
Implements `juno-api:1.0.0`  
A standalone OpenGL implementation of the core Juno API.

### Juno Utils
`org.meteordev:juno-utils:1.0.1`  
Depends on `juno-api:1.0.0`  
Helper classes built on top of the core Juno API.

### Juno MC
`org.meteordev:juno-mc:1.0.1`  
Includes `juno-api:1.0.1` and `juno-utils:1.0.1`  
A Minecraft Fabric mod that implements the core Juno API on top of Minecraft. Also includes a few Minecraft helpers.  
To use Juno in a Minecraft mod simply Jar-in-Jar this package.
There is an [example](https://github.com/MeteorDevelopment/juno/tree/master/juno-mc/example) specifically for this package.