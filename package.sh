#!/bin/bash

# helper script to package up Camshaft into a distributable
#
# Tasks: create empty folders, copy in JDBC drivers, copy documentation file, zip up

mvn clean compile assembly:single -DskipTests
# mvn clean package -DskipTests
mkdir target/camshaft

cp target/camshaft-*.jar target/camshaft/camshaft.jar
cp src/main/dist/* target/camshaft/

cd target/camshaft

asciidoctor-pdf README.adoc
rm README.md
rm README.adoc

zip -r ../camshaft-1.0.2.zip *
cd ..

scp camshaft-1.0.2.zip jonessys@jones-systems.com:~/public_html/sites/developer.jasonwjones.com/camshaft/downloads
scp camshaft/README.pdf jonessys@jones-systems.com:"~/public_html/sites/developer.jasonwjones.com/camshaft/Camshaft_Documentation_Latest.pdf"
