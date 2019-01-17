#!/usr/bin/env python

import os
import glob

file = glob.glob('run_438*')

command = 'rm /work/clas12/viducic/'

for f in file:
        newf = f.replace('.filtered', '')
        cmd_exec = command + newf
        print(cmd_exec)
        os.system(cmd_exec)