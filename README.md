# Checkers DD2380

### Compile
```sh
javac *.java
```


### Run
- The players use standard input and output to communicate
- The Moves made are shown as unicode-art on std err if the parameter verbose is given

### Play against self in same terminal
```sh
mkfifo pipe
java Main init verbose < pipe | java Main > pipe
```


#### Play against self in two different terminals
**Terminal 1:**
```sh
mkfifo pipe1 pipe2
java Main init verbose < pipe1 > pipe2
```
**Terminal 2:**
```sh
java Main verbose > pipe1 < pipe2
```

#### To play two different agents agains each other, you can use the classpath argument
```sh
java -classpath <path> Main init verbose < pipe | java -classpath <path> Main > pipe
```

**Comments:**
- Player Class missing (?)
