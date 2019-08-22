#!/usr/bin/env python

import os
import glob

file = glob.glob('run_438*')

#probably don't need this one either

command = '/home/gavalian/coatjava/bin/hipo-utils -info'

for f in file:
    cmd_exec = command + " " + f
    print(cmd_exec)
    os.system(cmd_exec)