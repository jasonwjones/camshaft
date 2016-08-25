package com.saxifrages.hyperion.camshaft;

import org.junit.Before;
import org.junit.Test;

public class CamshaftTest {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testMain() throws Exception {
		String[] args = {
			"--server=10.0.0.212", 
			"--user=admin", 
			"--password=password", 
			"--app=ASOSamp", 
			"--database=Sample",
			//"--query=SELECT CROSSJOIN({[Jan], [Feb], [Mar]}, {[Curr Year], [Prev Year]}) ON COLUMNS, {[Measures].Levels(0).members} ON ROWS, {[Transaction Type].Levels(0).members} ON PAGES FROM [ASOSamp].[Sample]"
			//"--query=SELECT CROSSJOIN({[Jan], [Feb]}, {[Curr Year], [Prev Year]}) ON COLUMNS, {[Measures].Levels(0).members} ON ROWS FROM [ASOSamp].[Sample]",
			"--query=SELECT CROSSJOIN({[Jan]}, {[Curr Year], [Prev Year]}) ON COLUMNS, {[Original Price], [Price Paid], [Returns], [Units]} ON ROWS FROM [ASOSamp].[Sample]",
			"-DformatString=%-24s",
			//"--tuple-separator=\" / \""
			"--tuple-separator=/"
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
