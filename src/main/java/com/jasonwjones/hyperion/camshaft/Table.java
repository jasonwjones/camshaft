package com.jasonwjones.hyperion.camshaft;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A query's result, read into memory: named, typed columns and rows of values.
 * Thriller has already turned the MDX grid into rows by the time it gets here,
 * so every format writes the same thing.
 */
public final class Table {

	/**
	 * A column of the result.
	 *
	 * @param name its name
	 * @param numeric whether it holds numbers, as opposed to text
	 */
	public record Column(String name, boolean numeric) {}

	private final List<Column> columns;

	private final List<Object[]> rows;

	public Table(List<Column> columns, List<Object[]> rows) {
		this.columns = Collections.unmodifiableList(new ArrayList<>(columns));
		this.rows = Collections.unmodifiableList(new ArrayList<>(rows));
	}

	/**
	 * Reads a result set to the end.
	 *
	 * @param resultSet the result set
	 * @return its columns and rows
	 * @throws SQLException if the result set can't be read
	 */
	public static Table from(ResultSet resultSet) throws SQLException {
		ResultSetMetaData metaData = resultSet.getMetaData();
		List<Column> columns = new ArrayList<>();
		for (int column = 1; column <= metaData.getColumnCount(); column++) {
			columns.add(new Column(metaData.getColumnLabel(column), isNumeric(metaData.getColumnType(column))));
		}
		List<Object[]> rows = new ArrayList<>();
		while (resultSet.next()) {
			Object[] row = new Object[columns.size()];
			for (int column = 0; column < row.length; column++) {
				row[column] = resultSet.getObject(column + 1);
			}
			rows.add(row);
		}
		return new Table(columns, rows);
	}

	static boolean isNumeric(int type) {
		switch (type) {
		case Types.DECIMAL:
		case Types.NUMERIC:
		case Types.DOUBLE:
		case Types.FLOAT:
		case Types.REAL:
		case Types.INTEGER:
		case Types.SMALLINT:
		case Types.TINYINT:
		case Types.BIGINT:
			return true;
		default:
			return false;
		}
	}

	public List<Column> getColumns() {
		return columns;
	}

	public List<Object[]> getRows() {
		return rows;
	}

}
