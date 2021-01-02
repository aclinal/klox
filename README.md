# klox
Kotlin implementation of `jlox`.

## Building
```shell
gradle clean shadowJar
```

## Running
REPL: 
```shell
java -jar ./build/libs/klox.jar
```

Wth specified Lox source file input:
```shell
java -jar ./build/libs/klox.jar [file]
```
