#!/usr/bin/env python

import os
import glob

file = glob.glob('run_43809*')

command = '/home/gavalian/coatjava/bin/hipo-utils -merge -o /work/clas12/viducic/43809_full.hipo '

for f in file:
    command+= f + ' '
cmd_exec = command + " " + f
print(cmd_exec)
os.system(cmd_exec)