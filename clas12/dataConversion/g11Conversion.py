#!/usr/bin/env python

import os
import subprocess
import glob

file = glob.glob('/cache/mss/clas/g11a/production/pass1/v1/data/run*')

command = 'ln -s  '

for f in file:
    cmd_exec = command + f 
    print(cmd_exec)
    os.system(cmd_exec)
