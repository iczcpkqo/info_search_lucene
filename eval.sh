#!/bin/bash

clear

echo "========================================"
echo "*                                      *"
echo "*    Hello This Script Will Start,     *"
echo "*    Thank you very much!              *"
echo "*                                      *"
echo "========================================"
echo "*                                      *"
echo "*                  3                   *"
echo "*                                      *"
sleep 1
echo "*                  2                   *"
echo "*                                      *"
sleep 2
echo "*                  1                   *"
echo "*                                      *"
echo "========================================"
echo ""
echo ""
echo ""

sleep 1
clear

echo "========================================"
echo "*                                      *"
echo "* Mvn Package is Running...            *"
echo "*                                      *"
echo "========================================"
echo ""
sleep 2
mvn package
echo ""
echo ""
echo ""

sleep 2
clear

echo "========================================"
echo "*                                      *"
echo "* Create Index... & Searching...       *"
echo "*                 & Saving Results...  *"
echo "*                                      *"
echo "========================================"
echo ""
sleep 2

java -jar target/XiangMao-assignment_1.jar

echo ""
echo ""
echo ""

sleep 2
clear

echo "========================================"
echo "*                                      *"
echo "*         Detail of Trec_Eval          *"
echo "*                                      *"
echo "========================================"
sleep 2
echo "*                                      *"
./src/main/java/trec_eval-9.0.7/trec_eval ./src/main/java/corpus/QRelsCorrectedforTRECeval ./src/main/java/my.record_for_search_results.txt

echo "*                                      *"
echo "========================================"
echo ""
echo ""
echo ""
echo "running, plase wait..."
echo ""
echo ""
echo ""

sleep 5
clear

echo "========================================"
echo "*                                      *"
echo "*                     Name : Xiang Mao *"
echo "*           Student Number :  21332237 *"
echo "*                                      *"
echo "* ------------------------------------ *"
echo "* Analyzers : STANDARD                 *"
echo "*   Scoring : BM25                     *"
echo "*                                      *"
echo "*                      =============== *"
echo "*                      || **BEST**  || *"
echo "*   RESULTS :          =============== *"
echo "*                                      *"

./src/main/java/trec_eval-9.0.7/trec_eval -m map ./src/main/java/corpus/QRelsCorrectedforTRECeval ./src/main/java/my.record_for_search_results.txt

./src/main/java/trec_eval-9.0.7/trec_eval -m iprec_at_recall.0 ./src/main/java/corpus/QRelsCorrectedforTRECeval ./src/main/java/my.record_for_search_results.txt


echo "*                                      *"
echo "*                                      *"
echo "*          =========================== *"
echo "*          || Scroll up for DETAILS || *"
echo "*          =========================== *"
echo "*                                      *"
echo "========================================"
echo ""


