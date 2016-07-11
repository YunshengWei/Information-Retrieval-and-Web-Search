SCRIPTPATH=$( cd $(dirname $0) ; pwd -P )
java -Xmx200M -cp $SCRIPTPATH/../classes cs276.assignments.Query Basic $1
