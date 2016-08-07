#!/usr/bin/env sh
# ./rank.sh <sigFile taskOption idfPath buildFlag> taskType
java -Xmx1024m -cp classes edu.stanford.cs276.Rank $1 $2 $3 $4
