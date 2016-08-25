Quoted MDX query for pasting into Java command:
==========================================================

"SELECT CROSSJOIN({[Jan], [Feb], [Mar]}, {[Curr Year], [Prev Year]}) ON COLUMNS, {[Measures].Levels(0).members} ON ROWS FROM [ASOSamp].[Sample]"

"SELECT CROSSJOIN({[Jan], [Feb], [Mar]}, {[Curr Year], [Prev Year]}) ON COLUMNS, {[Measures].Levels(0).members} ON ROWS, {[Transaction Type].Levels(0).members} ON PAGES FROM [ASOSamp].[Sample]"



Same command ended with semi-colon for pasting in to MaxL:
==========================================================

SELECT CROSSJOIN({[Jan], [Feb], [Mar]}, {[Curr Year], [Prev Year]}) ON COLUMNS, {[Measures].Levels(0).members} ON ROWS FROM [ASOSamp].[Sample];

SELECT CROSSJOIN({[Jan], [Feb], [Mar]}, {[Curr Year], [Prev Year]}) ON COLUMNS, {[Measures].Levels(0).members} ON ROWS, {[Transaction Type].Levels(0).members} ON PAGES FROM [ASOSamp].[Sample];

