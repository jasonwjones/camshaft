package com.saxifrages.hyperion.camshaft;

public class CamshaftTest {

	private String testQueryFile;
	
	public void setUp() throws Exception {
		//testQueryFile = getClass().getResource("/asosamp-sample-test-03.mdx").getFile();
		//testQueryFile = getClass().getResource("/demo-basic-test-01.mdx").getFile();
		testQueryFile = getClass().getResource("/sample-basic-member-props.mdx").getFile();
		//testQueryFile = getClass().getResource("/sample-basic-test-1.mdx").getFile();
	}

	public static void main(String[] args) throws Exception {
		CamshaftTest test = new CamshaftTest();
		test.setUp();
		test.testMain();
	}

	public void testMain() throws Exception {
		String[] args = {
			"--server=epmvirt11124", 
			"--user=admin", 
			"--password=password", 
			"--app=Sample", 
			"--database=Basic",
			//"--query=SELECT CROSSJOIN({[Jan], [Feb], [Mar]}, {[Curr Year], [Prev Year]}) ON COLUMNS, {[Measures].Levels(0).members} ON ROWS, {[Transaction Type].Levels(0).members} ON PAGES FROM [ASOSamp].[Sample]"
			//"--query=SELECT CROSSJOIN({[Jan], [Feb]}, {[Curr Year], [Prev Year]}) ON COLUMNS, {[Measures].Levels(0).members} ON ROWS FROM [ASOSamp].[Sample]",
			
			"--query=" + testQueryFile,
			
			//"--query=SELECT CROSSJOIN({[Jan]}, {[Curr Year], [Prev Year]}) ON COLUMNS, {[Original Price], [Price Paid], [Returns], [Units]} ON ROWS FROM [ASOSamp].[Sample]",
			"-DformatString=%-24s",
			//"-Ddelimiter=___",
			"--tuple-separator=\" / \"",
			"--print-property-names"
			//"--tuple-separator=/",
			//"--maximum-fraction-digits=2"
			
			//"-Ddelimiter=,",
			//"--line-per-header",
			//"--no-header",
			//"--output-engine=html"
			//"--missing-text=-",
			//"--no-indent-header"
			//"--delimiter=,"
//			, "--outfile=/Users/jasonwjones/mdxpull.txt"
		};
		
		Camshaft.main(args);
	}

}
