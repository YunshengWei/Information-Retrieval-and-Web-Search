import tempfile
import os
import shutil
import subprocess
import re

#################################################################
# This script helps verify that you are printing output correctly.
# As noted in the script's output, this only verifies the correct
# output format, not the correct output itself.
# usage: 
#    python formatVerification.py
#
# If you receive errors like "class not found" when running the
# script, it's likely that you need to compile your code first.
#################################################################

passed = True
pattern = "\d/"
indexDir = tempfile.mkdtemp()
SUCCESS = "\033[1;32mSuccess:\033[1;m "
ERROR = "\033[1;31mError:\033[1;m "

print "Verifying output format is correct using toy_example..." 
print "Note: this does *NOT* guarantee that the output is correct, merely that the output format is correct."
print "Ensure you have compiled your code before running this script."

os.system("sh task1/index.sh toy_example/data " + indexDir)
for i in range(1, 6):
	cmd = "sh task1/query.sh " + indexDir + " < toy_example/queries/query.%d" %  i
	result = subprocess.check_output(cmd, shell=True)

	if i == 1:
		if result != "no results found\n":
			print ERROR + "incorrect form for 'no results found'."
			passed = False

	else:
		if re.match(pattern, result) == None:
			print ERROR + "incorrect form for docName."
			passed = False

if passed:
	print SUCCESS + "Output format correct."

shutil.rmtree(indexDir)
