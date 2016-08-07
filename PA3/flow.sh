#!/usr/bin/env sh
# ./flow.sh <sigFile taskOption idfPath buildFlag relFile> taskType
java -Xmx1024m -cp classes edu.stanford.cs276.Rank $1 $2 $3 $4 > flowResult.txt
java -Xmx1024m -cp classes edu.stanford.cs276.NdcgMain flowResult.txt $5
