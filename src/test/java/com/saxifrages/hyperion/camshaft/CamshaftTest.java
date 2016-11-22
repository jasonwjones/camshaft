package com.saxifrages.hyperion.camshaft;

import java.io.File;

import org.junit.Before;
import org.junit.Test;

public class CamshaftTest {

	private String testQueryFile;
	
	@Before
	public void setUp() throws Exception {
		//testQueryFile = getClass().getResource("/asosamp-sample-test-02.mdx").getFile();
		testQueryFile = getClass().getResource("/sample-basic-test-1.mdx").getFile();
	}

	@Test
	public void testMain() throws Exception {
		String[] args = {
			"--server=epm11124", 
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
