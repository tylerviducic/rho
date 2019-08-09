#!/usr/bin/env python3

import os
import glob

run_numbers = []

files = glob.glob("/w/hallb-scifs17exp/clas12/viducic/data/rga/v1/*.hipo")
print(files)

for f in files:
    # dst_clas_005165.evio.00580-00584.hipo
    run_num = f[9:15]
    if run_num not in run_numbers:
        run_numbers.append(run_num)

for num in run_numbers:
    print(num)
    os.mkdir("/w/hallb-scifs17exp/clas12/viducic/data/rga/v1/{}".format(num))
