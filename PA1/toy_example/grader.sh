#!/bin/bash
fmt="user = %U, system = %S, elapsed = %e, memory = %M, memory = %K"
ref=ref
queryin=queries
queryref=$ref/query_ref
file_count_ref=$ref/filecount.ref
corpus=data

out=output
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
if [ ! -d "$queryout" ]; then
  mkdir -p $queryout
fi

chmod +x index.sh
chmod +x query.sh

echo "" >&2
echo "###### Testing Indexing ######" >&2
START=$(date +%s)
./memusg -o $index_memout ./index.sh $corpus $index > $file_count
END=$(date +%s)
DIFF=$(( $END - $START ))
echo "$DIFF seconds" > $indextime
filecount_diff=`diff -U 0 $file_count $file_count_ref | grep -v ^@ | wc -l`
echo "######"
if [ $filecount_diff -gt 0 ]; then
  echo "file counts different from oracle" >&2
  diff -U 0 $file_count $file_count_ref | grep -v ^@ >&2
else
  echo "file counts test passed" >&2 
fi
echo "######"

echo "" >&2
echo "###### Testing Retrieval ######" >&2
START=$(date +%s)
query_error=0
echo  > $querytime
for i in {1..5}
do
./memusg -o $query_memout ./query.sh $index < $queryin/query.${i} > $queryout/${i}
END=$(date +%s)
DIFF=$(( $END - $START ))
echo "$DIFF seconds" >> $querytime

query_diff=`diff -U 0 $queryout/${i} $queryref/${i} | grep -v ^@ | wc -l`
if [ $query_diff -gt 0 ]; then
  query_error=`expr $query_error + 1`
fi
done
echo "######"
if [ $query_error -gt 0 ]; then
  echo "$query_error queries were wrong" >&2
else
  echo "all queries passed"
fi
echo "######"
