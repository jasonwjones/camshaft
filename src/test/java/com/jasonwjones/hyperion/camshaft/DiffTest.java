package com.jasonwjones.hyperion.camshaft;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.Test;

public class DiffTest {

	private static final List<Table.Column> COLUMNS = List.of(new Table.Column("Market", false), new Table.Column("Year", false),
			new Table.Column("Value", true));

	private static Table table(Object[]... rows) {
		return new Table(COLUMNS, List.of(rows));
	}

	@Test
	public void findsNothingWhenTheValuesAgree() throws CamshaftException {
		Diff.Result result = Diff.compare(table(new Object[] { "East", "Jan", 1812.0 }), table(new Object[] { "East", "Jan", 1812.0 }),
				BigDecimal.ZERO, false);
		assertFalse(result.differs());
		assertEquals(0, result.table().getRows().size());
		assertEquals("1 values compared: no differences", result.summary());
	}

	@Test
	public void reportsChangedValuesAndOnesOnlyOnOneSide() throws CamshaftException {
		Diff.Result result = Diff.compare(
				table(new Object[] { "East", "Jan", 1812.0 }, new Object[] { "East", "Feb", 1754.0 }, new Object[] { "West", "Jan", 1174.0 }),
				table(new Object[] { "East", "Jan", 1900.0 }, new Object[] { "East", "Feb", 1754.0 }, new Object[] { "South", "Jan", 300.0 }),
				BigDecimal.ZERO, false);

		assertTrue(result.differs());
		assertEquals(1, result.changed());
		assertEquals(1, result.leftOnly());
		assertEquals(1, result.rightOnly());
		assertEquals(4, result.compared());
		assertEquals(List.of("STATUS", "Market", "Year", "LEFT", "RIGHT", "DIFFERENCE"),
				result.table().getColumns().stream().map(Table.Column::name).toList());
		assertArrayEquals(new Object[] { Diff.CHANGED, "East", "Jan", 1812.0, 1900.0, new BigDecimal("88") }, result.table().getRows().get(0));
		assertArrayEquals(new Object[] { Diff.LEFT_ONLY, "West", "Jan", 1174.0, null, null }, result.table().getRows().get(1));
		assertArrayEquals(new Object[] { Diff.RIGHT_ONLY, "South", "Jan", null, 300.0, null }, result.table().getRows().get(2));
		assertEquals("4 values compared: 1 changed, 1 only on the left, 1 only on the right", result.summary());
	}

	@Test
	public void toleratesSmallDifferencesWhenAsked() throws CamshaftException {
		Table left = table(new Object[] { "East", "Jan", 100.004 });
		Table right = table(new Object[] { "East", "Jan", 100.0 });
		assertTrue(Diff.compare(left, right, BigDecimal.ZERO, false).differs());
		assertFalse(Diff.compare(left, right, new BigDecimal("0.01"), false).differs());
	}

	@Test
	public void treatsMissingOnOneSideAsADifference() throws CamshaftException {
		Diff.Result result = Diff.compare(table(new Object[] { "East", "Jan", null }), table(new Object[] { "East", "Jan", 5.0 }),
				BigDecimal.ZERO, false);
		assertEquals(1, result.changed());
		assertEquals(null, result.table().getRows().get(0)[5]);
		assertFalse(Diff.compare(table(new Object[] { "East", "Jan", null }), table(new Object[] { "East", "Jan", null }),
				BigDecimal.ZERO, false).differs());
	}

	@Test
	public void matchesRepeatedMembersInOrder() throws CamshaftException {
		// A shared member appears twice in a level 0 set
		Table left = table(new Object[] { "Diet", "Jan", 1.0 }, new Object[] { "Diet", "Jan", 2.0 });
		Table right = table(new Object[] { "Diet", "Jan", 1.0 }, new Object[] { "Diet", "Jan", 3.0 });
		Diff.Result result = Diff.compare(left, right, BigDecimal.ZERO, false);
		assertEquals(1, result.changed());
		assertEquals(2.0, result.table().getRows().get(0)[3]);
	}

	@Test
	public void canShowTheValuesThatAgreeToo() throws CamshaftException {
		Diff.Result result = Diff.compare(table(new Object[] { "East", "Jan", 1812.0 }), table(new Object[] { "East", "Jan", 1812.0 }),
				BigDecimal.ZERO, true);
		assertEquals(Diff.SAME, result.table().getRows().get(0)[0]);
		assertEquals(BigDecimal.ZERO, result.table().getRows().get(0)[5]);
	}

	@Test
	public void refusesResultsWithDifferentColumns() {
		Table other = new Table(List.of(new Table.Column("Product", false), new Table.Column("Value", true)), List.of());
		assertThrows(CamshaftException.class, () -> Diff.compare(table(), other, BigDecimal.ZERO, false));
	}

}
