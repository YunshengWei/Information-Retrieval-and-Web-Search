#!/usr/bin/env sh
./rank.sh data/rank/pa3.signal.${2} ${1} idfs false
./ndcg.sh ranked.txt data/rank/pa3.rel.${2}
