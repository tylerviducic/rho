import os
import subprocess
import glob
import pickle

pickle_in = open('filteredRuns.p', 'rb')
filtered_runs = pickle.load(pickle_in)
pickle_in.close()
directories = glob.glob("path/to/directory/*")

command = 'rg rgaMasterFiler.groovy'

for directory in directories:
    if directory not in filtered_runs:
        cmd_ex = command + directory
        pickle_out = open('filteredRuns.p', 'wb')
        pickle.dump(directory, pickle_out)
        pickle_out.close()

