#!/usr/bin/env sh
# ./ndcg.sh <rankFile relFile> taskType
java -Xmx1024m -cp classes edu.stanford.cs276.NdcgMain $1 $2
