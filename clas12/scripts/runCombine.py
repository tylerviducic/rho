#!/usr/bin/env python3

import os
import glob

run_numbers = []

files = glob.glob("/w/hallb-scifs17exp/clas12/viducic/data/rga/v2/*.hipo")

for f in files:
    # '/w/hallb-scifs17exp/clas12/viducic/data/rga/v1/dst_clas_005181.evio.00210-00214.hipo'
    run_num = f[56:62]
    if run_num not in run_numbers:
        run_numbers.append(run_num)

for num in run_numbers:
    print(num)
    os.mkdir("/w/hallb-scifs17exp/clas12/viducic/data/rga/v2/{}".format(num))


# for f in files:
#     run_num = f[56:62]
#     os.system('mv {} /w/hallb-scifs17exp/clas12/viducic/data/rga/v1/{}'.format(f, run_num))
