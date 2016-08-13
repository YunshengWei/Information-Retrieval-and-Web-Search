#!/bin/bash
# Version 2.0: includes idfs_file as a command line argument

if [[ ! $# -eq 5 && ! $# -eq 6 ]]; then
  echo "Usage: l2r.sh <train_signal_file> <train_rel_file> <test_signal_file> <idfs_file> <task> [out_file]"
  echo "out_file (optional): specify where to write output results to. If not specified, write to stdout"
  exit
fi

train_signal_file="$1"
train_rel_file="$2"
test_signal_file="$3"
idfs_file="$4"
task="$5"
out_file=""
if [ $# -ge 6 ]; then
  out_file="$6"
fi

ant

echo ""
echo "Executing: java -cp classes:lib/weka.jar cs276.pa4.Learning2Rank $train_signal_file $train_rel_file $test_signal_file $idfs_file $task $out_file"
java -cp classes:lib/weka.jar cs276.pa4.Learning2Rank $train_signal_file $train_rel_file $test_signal_file $idfs_file $task $out_file

