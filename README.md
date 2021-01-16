# klox
Kotlin implementation of `jlox`.

## Building
```shell
gradlew clean shadowJar
```

## Testing
```shell
gradlew test
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
