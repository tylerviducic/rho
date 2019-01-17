#!/usr/bin/env python

import os
import glob

file = glob.glob('run_438*')

command = '/home/gavalian/coatjava/bin/bos2hipo -lz4'

for f in file:
    cmd_exec = command + " " + f +".hipo " + f
    print(cmd_exec)
    os.system(cmd_exec)