# change the data path (the first argument) if necessary
./run.sh ../data output dev_queries $1/index.sh $1/query.sh
for ((i = 1 ; i < 9 ; i++))
do
query_diff= diff -U 0 output/query_out/query.$i dev_output/$i.out | grep -v ^@ | wc -l
echo $query_diff 
done
