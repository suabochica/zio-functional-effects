# Zionomicon

Repository to store the solution of [Zionomicon](https://www.zionomicon.com/) exercises

## Setup

To compile the project, open a terminal and enter to `sbt` cli of the project via:

``` 
sbt
```

The output of this command should be:

``` 
sbt:zionomicon>
```

To compile the project execute the next command:

``` 
sbt:zionomicon> compile
```

Here a `/target` project should be created as sibling of `/project`. The target folder is the output of the compilation and it is cached to streamline the re-compile process.

The run task compiles and then runs the program:

``` 
sbt:zionomicon> run
```


## Specs

- Scala version: 2.13.4
- ZIO version: 1.0.4-2
