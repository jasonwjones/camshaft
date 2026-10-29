package com.jasonwjones.hyperion.camshaft;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Compares two results of the same query in long shape, where each row is the
 * members that identify a value, then the value. A row on one side is matched
 * with the row on the other that has the same members; a member that appears
 * twice, as a shared member can, is matched in order of appearance.
 */
public final class Diff {

	/** A value that's on both sides and differs */
	public static final String CHANGED = "changed";

	/** A value only on the left */
	public static final String LEFT_ONLY = "left only";

	/** A value only on the right */
	public static final String RIGHT_ONLY = "right only";

	/** A value that's the same on both sides */
	public static final String SAME = "same";

	/**
	 * What a comparison found.
	 *
	 * @param table a row for each difference, or for every value if asked
	 * @param compared how many values were compared, counting both sides'
	 *            unmatched ones
	 * @param changed how many differ
	 * @param leftOnly how many are only on the left
	 * @param rightOnly how many are only on the right
	 */
	public record Result(Table table, int compared, int changed, int leftOnly, int rightOnly) {

		/**
		 * @return whether anything differs
		 */
		public boolean differs() {
			return changed + leftOnly + rightOnly > 0;
		}

		/**
		 * @return a line saying what was found
		 */
		public String summary() {
			if (!differs()) {
				return compared + " values compared: no differences";
			}
			return compared + " values compared: " + changed + " changed, " + leftOnly + " only on the left, "
					+ rightOnly + " only on the right";
		}

	}

	private Diff() {}

	/**
	 * @param left the left result, in long shape
	 * @param right the right result, in long shape
	 * @param tolerance how far apart two numbers can be and still count as the
	 *            same
	 * @param all whether to include the values that are the same
	 * @return what's different
	 * @throws CamshaftException if the results don't have the same columns
	 */
	public static Result compare(Table left, Table right, BigDecimal tolerance, boolean all) throws CamshaftException {
		List<String> leftNames = names(left);
		List<String> rightNames = names(right);
		if (!leftNames.equals(rightNames)) {
			throw new CamshaftException("The two results have different columns, " + leftNames + " and " + rightNames
					+ ", so they can't be compared");
		}
		if (leftNames.isEmpty()) {
			throw new CamshaftException("The results have no columns to compare");
		}
		int keys = leftNames.size() - 1;

		Map<List<Object>, Object> rightValues = new LinkedHashMap<>();
		Map<List<Object>, Integer> seen = new HashMap<>();
		for (Object[] row : right.getRows()) {
			rightValues.put(key(row, keys, seen), row[keys]);
		}

		List<Object[]> rows = new ArrayList<>();
		int changed = 0;
		int leftOnly = 0;
		seen.clear();
		for (Object[] row : left.getRows()) {
			List<Object> key = key(row, keys, seen);
			Object leftValue = row[keys];
			if (!rightValues.containsKey(key)) {
				rows.add(row(LEFT_ONLY, row, keys, leftValue, null));
				leftOnly++;
				continue;
			}
			Object rightValue = rightValues.remove(key);
			if (same(leftValue, rightValue, tolerance)) {
				if (all) {
					rows.add(row(SAME, row, keys, leftValue, rightValue));
				}
			} else {
				rows.add(row(CHANGED, row, keys, leftValue, rightValue));
				changed++;
			}
		}
		for (Map.Entry<List<Object>, Object> entry : rightValues.entrySet()) {
			Object[] members = entry.getKey().subList(0, keys).toArray();
			rows.add(row(RIGHT_ONLY, members, keys, null, entry.getValue()));
		}

		List<Table.Column> columns = new ArrayList<>();
		columns.add(new Table.Column("STATUS", false));
		columns.addAll(left.getColumns().subList(0, keys));
		boolean numeric = left.getColumns().get(keys).numeric() || right.getColumns().get(keys).numeric();
		columns.add(new Table.Column("LEFT", numeric));
		columns.add(new Table.Column("RIGHT", numeric));
		columns.add(new Table.Column("DIFFERENCE", true));
		int compared = left.getRows().size() + rightValues.size();
		return new Result(new Table(columns, rows), compared, changed, leftOnly, rightValues.size());
	}

	private static List<String> names(Table table) {
		List<String> names = new ArrayList<>();
		for (Table.Column column : table.getColumns()) {
			names.add(column.name());
		}
		return names;
	}

	/**
	 * A row's members, then how many times they've been seen before, so that a
	 * repeated set of members is matched in order.
	 */
	private static List<Object> key(Object[] row, int keys, Map<List<Object>, Integer> seen) {
		List<Object> members = Arrays.asList(Arrays.copyOf(row, keys));
		int occurrence = seen.merge(members, 1, Integer::sum);
		List<Object> key = new ArrayList<>(members);
		key.add(occurrence);
		return key;
	}

	private static Object[] row(String status, Object[] members, int keys, Object left, Object right) {
		Object[] row = new Object[keys + 4];
		row[0] = status;
		System.arraycopy(members, 0, row, 1, keys);
		row[keys + 1] = left;
		row[keys + 2] = right;
		BigDecimal leftNumber = number(left);
		BigDecimal rightNumber = number(right);
		row[keys + 3] = leftNumber != null && rightNumber != null ? rightNumber.subtract(leftNumber) : null;
		return row;
	}

	static boolean same(Object left, Object right, BigDecimal tolerance) {
		BigDecimal leftNumber = number(left);
		BigDecimal rightNumber = number(right);
		if (leftNumber != null && rightNumber != null) {
			return leftNumber.subtract(rightNumber).abs().compareTo(tolerance) <= 0;
		}
		return Objects.equals(left, right);
	}

	/**
	 * A value as a number, written plainly, so that 1812.0 less 1812.0 is 0
	 * rather than 0.0; null if it isn't one.
	 */
	private static BigDecimal number(Object value) {
		if (!(value instanceof Number)) {
			return null;
		}
		BigDecimal number = value instanceof BigDecimal ? (BigDecimal) value : new BigDecimal(value.toString());
		number = number.stripTrailingZeros();
		return number.scale() < 0 ? number.setScale(0) : number;
	}

}
