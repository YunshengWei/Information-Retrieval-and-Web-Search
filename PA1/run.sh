#!/bin/bash
fmt="user = %U, system = %S, elapsed = %e, memory = %M, memory = %K"
ref=ref
queryin=$3
indexshfile=$4
queryshfile=$5
if ! [ -d $queryin ]; then echo "query directory $queryin does not exist" >&2; exit; fi

corpus=$1

out=$2
if [ ! -d "$out" ]; then
  mkdir -p $out
fi
index=$out/index
queryout=$out/query_out
querytime=$out/querytime.txt
indextime=$out/indextime.txt
index_memout=$out/index_memory_out
query_memout=$out/query_memory_out
file_count=$out/filecount.txt
index_size=$out/indexsize.txt
if [ ! -d "$queryout" ]; then
  mkdir -p $queryout
fi

#/usr/bin/time -f "$fmt" -a -o $indextime ./memusg -o $index_memout ./$indexshfile $corpus $index > $file_count

START=$(date +%s)
./memusg -o $index_memout ./$indexshfile $corpus $index > $file_count
END=$(date +%s)
DIFF=$(( $END - $START ))
echo "$DIFF seconds" > $indextime

indexsize=`(du -sh $index | awk '{print $1}')`
echo "$indexsize" > $index_size
 
query_error=0

echo ""> $querytime
for file in $queryin/*
do
START=$(date +%s)
./memusg -o $query_memout ./$queryshfile $index < $file > $queryout/$(basename $file)
END=$(date +%s)
DIFF=$(( $END - $START ))
echo "$DIFF seconds" >> $querytime
done
