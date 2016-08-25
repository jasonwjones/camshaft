# Camshaft MDX Query Tool

Runs MDX queries against an Essbase server and outputs the results to a file. 
You might then use that file to load with a load rule.

This tool is meant to be an alternative to running MDX queries from MaxL and then 
having to strip a bunch of stuff off of them in order to try and get to a parseable format.


## Example Usage

This example requires Java to be on the PATH (i.e., you can type `java` at the terminal and it runs fine)
This example should be entered as one line but has been put into multiple lines for readability here

java -jar camshaft.jar --server=epm11123 --user=admin --password=password --app=ASOSamp --database=Sample
 --query="SELECT CROSSJOIN({[Jan], [Feb], [Mar]}, {[Curr Year], [Prev Year]}) ON COLUMNS, {[Measures].Levels(0).members} ON ROWS"
 --outfile=mdxpull.txt

Other options:

--no-header

This suppresses the header rows in case you don't need them.

You can always run the program with no arguments in order to get a list of all current flags and alternative names you can pass.


## Author

Written by Jason Jones (@jwj) of Saxifrage Systems LLC. 

http://www.jasonwjones.com
http://www.saxifrages.com


## Version History

### 1.0.0

* Initial release