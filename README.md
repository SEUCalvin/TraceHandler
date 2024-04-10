# TraceHandler
An automated tool library for parsing Android platform trace files.

## How To Use
- Generate trace files manually fist
- Change PACKAGE_NAME_LIST in Main.java, Filter the package name information you are interested in, otherwise, no method call records will be filtered
- Build -> Build Artifacts, generate a jar file, please refer to the configuration process for generating the jar file on your own

## Function1: Merge the function sets from multiple trace files to generate a combined file.
```
java -jar trace-handler.jar 2 set-output.txt trace1.trace trace2.trace trace3.trace ...
```
Assuming you have successfully generated a jar file named trace-handler.jar, this command will generate a deduplicated function set based on the provided multiple trace files, with all functions filtered by PACKAGE_NAME_LIST.


## Function2: Identify function sets in the trace files that do not belong to the combined file.
```
java -jar trace-handler.jar 1 set-output.txt trace.trace diff.txt
```
This command will use the combined file to identify any newly added function sets in the trace files to be checked and output them to the specified path file.

