# Camshaft

A command-line tool that runs Essbase MDX queries and writes the results where you want them: a
CSV to load, JSON for a script, a Markdown table for a wiki, an Excel workbook for a person, or a
table to read in the terminal.

```
$ camshaft run "SELECT {[Jan], [Feb]} ON COLUMNS, CROSSJOIN({[East], [West]}, [Colas].Children) ON ROWS
    FROM Sample.Basic WHERE ([Sales], [Actual])"
+--------+---------+------+------+
| Market | Product | Jan  | Feb  |
+--------+---------+------+------+
| East   | 100-10  | 1812 | 1754 |
| East   | 100-20  |  200 |  206 |
| East   | 100-30  |   93 |  101 |
| West   | 100-10  | 1174 | 1146 |
| West   | 100-20  |  700 |  726 |
| West   | 100-30  |  465 |  426 |
+--------+---------+------+------+
```

It also runs folders of queries at once, lists what's on a server, and compares a query's results
between two servers or cubes.

Camshaft started in 2014 as a way to stop running MDX from MaxL and then stripping the headers off
the output before you could load it. Version 2 is a rewrite. It talks to Essbase over the REST API,
so it needs no Oracle software, and it runs its queries through
[Thriller](https://github.com/jasonwjones/thriller-mdx-jdbc), the MDX JDBC driver, and
[essbase-rest-client](https://github.com/appliedolap/essbase-rest-client). It has been tested
against Essbase 26.1.

## Getting it

Download `camshaft-<version>.jar` from the
[releases page](https://github.com/jasonwjones/camshaft/releases). It's a single jar with everything
Camshaft needs, and it runs on Java 17 or newer:

```
java -jar camshaft-2.0.0.jar --help
```

An alias saves typing:

```
alias camshaft='java -jar ~/bin/camshaft-2.0.0.jar'
```

## Connecting

Camshaft needs the server's address, as you'd open it in a browser, and a login. Each can come from
an option, a profile, or the environment, in that order:

| Option | Environment | Meaning |
| --- | --- | --- |
| `-s`, `--url` | `CAMSHAFT_URL` | The server's address, such as `http://localhost:9000/essbase` |
| `-u`, `--user` | `CAMSHAFT_USER` | The user to log in as |
| | `CAMSHAFT_PASSWORD` | The password |
| `--auth` | `CAMSHAFT_AUTH` | `password` (the default), `token` or `session`; see below |
| `-p`, `--profile` | | A profile to take all of these from |

There's deliberately no option for the password, which would end up in your shell history and in
the process list. Without one from the environment or a profile, Camshaft asks for it.

Profiles live in `~/.camshaft.properties` (or wherever `CAMSHAFT_PROFILES` says), one setting per
line. A profile named `default` is used when nothing else says where to connect:

```properties
default.url = http://localhost:9000/essbase
default.user = admin

prod.url = https://essbase.example.com/essbase
prod.user = reporting
prod.password = ...
```

For a server behind an identity provider, where users have no Essbase password, `--auth token`
sends the password as a bearer token, and `--auth session` presents it as the id of a session
established elsewhere (its `JSESSIONID`). Neither needs a user.

## Running queries

`camshaft run` takes the query itself, a file holding it, or `-` for standard input, which is also
where it reads when given nothing:

```
camshaft run "SELECT {[Jan]} ON COLUMNS, [Market].Children ON ROWS FROM Sample.Basic"
camshaft run sales.mdx -o sales.csv
cat sales.mdx | camshaft run -f json
```

The query reads the cube its `FROM` clause names. For a query without one, give `--cube Sample.Basic`.

### Formats

`-f` chooses the format: `csv`, `tsv`, `json`, `jsonl`, `markdown`, `table`, `html` or `xlsx`.
Without it, the output file's extension decides (`-o sales.csv` writes CSV), and otherwise it's a
table in a terminal and CSV when the output is piped somewhere.

Numbers are written in full, never in scientific notation, and `1812.0` is written `1812`. Other
options for the text formats:

| Option | Meaning |
| --- | --- |
| `--no-header` | Leave out the column names |
| `--null TEXT` | What to write for a null, such as `#Missing`; nothing by default |
| `--decimals N` | Round numbers to N decimal places |

`xlsx` is Essbase's own Excel output, laid out as Essbase lays out the grid, so the shaping options
below don't apply to it; `--members` does.

### Shaping

Camshaft runs queries through Thriller, so a result comes out exactly as it would in a SQL tool,
and Thriller's settings are options here:

| Option | Meaning |
| --- | --- |
| `--members NAMING` | How to name members: `name` (the default), `alias` or `unique` |
| `--alias-table TABLE` | The alias table for `--members alias`, such as `"Long Names"` |
| `--shape long` | One row for each value, with a column for each dimension: a fact table |
| `--pov` | Add the point of view's members as columns |
| `--names upper` | Make the column names identifiers, such as `JAN_COLA`; or `lower` |
| `--missing AS` | What `#Missing` becomes: `null` (the default), `zero`, or `skip` |
| `--timeout SECONDS` | How long to wait for the query |

A query's own Thriller hints still win, so a query file can carry its column names and types with
it. See the [Thriller README](https://github.com/jasonwjones/thriller-mdx-jdbc#readme) and its
[cookbook](https://github.com/jasonwjones/thriller-mdx-jdbc/blob/master/examples/README.md).

A fact table for loading somewhere else, for example:

```
$ camshaft run sales.mdx --shape long --pov --members alias --names lower -f jsonl
{"market":"East","product":"Cola","measures":"Sales","scenario":"Actual","year":"Jan","value":1812}
{"market":"East","product":"Cola","measures":"Sales","scenario":"Actual","year":"Feb","value":1754}
...
```

## Running a folder of queries

`camshaft batch` runs every `.mdx` file in the folders it's given, and any query files named, and
writes each result to a file of the same name in an output folder:

```
camshaft batch queries/ -d extracts/ -f csv
```

The format, shaping and connection options all apply, and one login serves every query. It stops at
the first query that fails unless given `--keep-going`, in which case it reports the failures at the
end.

## Looking around

```
$ camshaft dims Sample.Basic
+-------------+---------+---------+--------+
| DIMENSION   | STORAGE | MEMBERS | STORED |
+-------------+---------+---------+--------+
| Year        | dense   |      19 |     12 |
| Measures    | dense   |      17 |     11 |
| Product     | sparse  |      22 |     19 |
...
```

| Command | Lists |
| --- | --- |
| `camshaft apps` | The applications |
| `camshaft cubes [APP]` | The cubes, in one application or all of them |
| `camshaft dims APP.CUBE` | A cube's dimensions, dense or sparse, and their member counts |
| `camshaft members APP.CUBE DIMENSION` | A dimension's members, with each one's alias, level and generation |

`members` takes `--level N`, `--generation N`, `--children MEMBER` or `--descendants MEMBER` to
narrow it down, and `--alias-table TABLE` to take the aliases from another table. Each writes in any
of the formats but `xlsx`.

## Comparing

`camshaft diff` runs a query against two servers or two cubes and shows the values that differ:

```
camshaft diff margins.mdx --profile test --right-profile prod --tolerance 0.01
```

The right side is the left with the `--right-url`, `--right-user`, `--right-profile`,
`--right-auth` and `--right-cube` options applied. Its password comes from
`CAMSHAFT_RIGHT_PASSWORD` or a profile, or is the left's if it logs in as the same user. To compare
two cubes, leave the `FROM` clause out of the query:

```
camshaft diff margins.mdx --cube Sample.Basic --right-cube Sample.Copy
```

Both sides are run as fact tables, with the point of view, so every value is matched by all of its
members. The result has a row for each value that changed, with the members, the two values and the
difference, and one for each value on only one side. `--all` shows the values that agree too, and
`--tolerance` lets numbers that are close enough count as the same. A summary goes to standard
error:

```
48 values compared: 2 changed, 0 only on the left, 1 only on the right
```

## Exit status

| Status | Meaning |
| --- | --- |
| 0 | It worked, or `diff` found no differences |
| 1 | `diff` found differences |
| 2 | Something went wrong: a bad option, a failed login, a failed query |

So a script can run `camshaft diff` as a check, and `-v` shows what's happening and the whole story
when something fails.

## Camshaft or Thriller?

Both run MDX and turn the result into a table, and they share the code that does it: Camshaft runs
its queries through Thriller.

Use Thriller when you want MDX inside a tool that already speaks JDBC: an IDE, DBeaver, a reporting
or integration tool, your own Java. Use Camshaft for the command line: one command from a query to a
file, with no driver to set up, plus batches, listings and comparisons that a SQL tool has no notion
of.

## Coming from Camshaft 1.x

Version 1 used Oracle's Essbase Java API. Most of its options carry over:

| 1.x | 2.0 |
| --- | --- |
| `--server` | `--url`, the REST address, such as `http://localhost:9000/essbase` |
| `--app`, `--database` | The query's `FROM` clause, or `--cube App.Cube` |
| `--query` | The query, or the file holding it, without an option name |
| `--outfile` | `-o` |
| `--output-engine html` | `-f html` |
| `--missing-text`, `--maximum-fraction-digits` | Still accepted, as `--null` and `--decimals` |
| `--password`, `--encoded`, `encode` | `CAMSHAFT_PASSWORD`, a profile, or the prompt |
| `--tuple-separator`, `--line-per-header` | Thriller names the columns, with `--names` to make identifiers |

## Building and testing

Java 17 or newer, and Maven.

```
mvn package
```

leaves the jar in `target/`, after the tests, which need no server. To run them against a live one
with Sample.Basic loaded as well:

```
CAMSHAFT_URL=http://localhost:9000/essbase CAMSHAFT_USER=admin CAMSHAFT_PASSWORD=... mvn test
```

They only read.

## Releasing

Set the release version in `pom.xml`, commit, and push a matching tag:

```
git tag v2.0.0
git push origin v2.0.0
```

The release workflow checks the tag against the pom, builds and tests, and publishes a GitHub
release with the jar attached. Then move the pom on to the next `-SNAPSHOT` version.

## License

[Apache License 2.0](LICENSE).
