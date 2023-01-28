# Juno
![](https://img.shields.io/badge/Version-0.1.0--SNAPSHOT-brightgreen)  
Juno is a Java rendering api.

## Backends

### OpenGL
`org.meteordev:juno-opengl:<version>`  
A standalone OpenGL backend for use when Juno is the only piece of code making OpenGL calls.  
To use the OpenGL backend you also need to include the api `org.meteordev:juno-api:<version>` in your program. Then call `JunoProvider.set(new GLJuno());` after an OpenGL context has been created.

### Minecraft
`org.meteordev:juno-mc:<version>`  
A Minecraft Fabric mod which implements the Juno API, so it can be used seamlessly in Minecraft.  
To use the Minecraft backend simply Jar-in-Jar the backend in your mod or make it a required dependency.