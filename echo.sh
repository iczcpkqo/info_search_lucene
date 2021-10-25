#!/bin/bash
echo "========================================"
echo " *                      Name : Xiang Mao"
echo " *            Student Number :  21332237"
echo " *                                      "
echo " -------------------------------------- "
echo " * Analyzers : STANDARD                 "
echo " *   Scoring : BM25    ===============  "
echo " *                     || **BEST**  ||  "
echo " *   RESULTS :         ===============  "
echo "                                        "
echo " map                     all     0.3934 "
echo " iprec_at_recall_0.00    all     0.7995 "
trec_eval - m map ../corpus/QRelsCorrectedforTRECeval ../my.record_for_search_results.txt

trec_eval - m iprec_at_recall.0 ../corpus/QRelsCorrectedforTRECeval ../my.record_for_search_results.txt
