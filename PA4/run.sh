#!/bin/bash
# Version 2.0: includes idfs_file as a command line argument

if [[ ! $# -eq 6 && ! $# -eq 6 ]]; then
  echo "Usage: run.sh <train_signal_file> <train_rel_file> <test_signal_file> <test_rel_file> <idfs_file> <task>"
  exit
fi

train_signal_file="$1"
train_rel_file="$2"
test_signal_file="$3"
test_rel_file="$4"
idfs_file="$5"
task="$6"

./l2r.sh $train_signal_file $train_rel_file $test_signal_file $idfs_file $task tmp.out.txt

# compute NDCG
echo ""
echo "# Executing: java -cp classes cs276.pa4.NdcgMain tmp.out.txt $test_rel_file"
java -cp classes cs276.pa4.NdcgMain tmp.out.txt $test_rel_file

rm -rf tmp.out.txt
