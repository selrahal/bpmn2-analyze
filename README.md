# Automatic Recommendations for BPMN2 Optimizations 

The bpmn2-analyze is a Java application that will parse a bpmn2 diagram, provide suggestions for process diagram improvements, and execute those suggestions. It first builds a tree by parsing the process diagram. Then it uses many TreeVisitors to add metadata to the nodes (like what process variables are used). The final TreeVisitors that run will determine possible improvements. Finally, the first improvement identified is executed and the whole process is repeated until there are no more improvements to be made.

 
Currently the improvements that are suggested and executed are based off of what process variables a work item node uses. If two nodes are executing serially but do not depend on any common process variables they could be executed in parallel.


Recommendations in Business Central:

![](docs/bc-simple-out.png?raw=true)


## Examples

### Simple

Let's take the following process (simple.bpmn2) as an example. It has three work item nodes and uses two different process variables, primary_key and email_address. The first node, "Write to DB", only depends on the primary_key. The second node, "Send Email", only depends on the email_address. The final node, "update email table", depends on both the primary_key and the email_address.

![](docs/simple.png?raw=true)

After running the bpmn2-analyze with the following command:
 
```bash
    mvn exec:java -Dexec.mainClass="org.jbpm.analyze.main.JbpmAnalyze" -Dexec.args="simple.bpmn2 output.bpmn2"  
```

We now have an output.bpmn2 that looks like:

![](docs/simple-out.png?raw=true)

The process diagram has been changed so that the "Write to DB" and "Send Email" nodes are executing in parallel and they are both still executing before the "update email table".
 
### Simple 2

This second example is a bit more complicated. Either node "Write 1 to DB" or "Write 2 to DB" will be executed based off of some condition then an email is sent. The "Send Email" node does not depend on anything from the DB work items.

![](docs/simple2.png?raw=true)

After running the tool we can see that the "Send Email" node can be executed in parallel to the other two nodes:

![](docs/simple2-out.png?raw=true)
