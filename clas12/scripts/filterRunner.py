import os
import glob
import pickle

pickle_in = open('filteredRuns.p', 'rb')
filtered_runs = pickle.load(pickle_in)
pickle_in.close()
directories = glob.glob("/volatile/clas12/rg-a/production/pass0/physTrain/dst/recon/*")

command = '/home/viducic/jaw-2.0/bin/run-groovy /w/hallb-scifs17exp/clas12/viducic/rho/clas12/scripts/rgaMasterFilter.groovy'

for directory in directories:
    if directory not in filtered_runs and os.path.isdir(directory):
        os.system("mv rga_master.hipo rga_skimmed_old.hipo")
        cmd_ex = command + " " + directory
        os.system(cmd_ex)
        combine_command = "/home/viducic/jaw-2.0/bin/hipoutils.sh -merge -o rga_skimmed_master.hipo rga_skimmed.hipo rga_skimmed_old.hipo"
        os.system(combine_command)
        os.system('rm rga_skimmed_old.hipo')
        pickle_out = open('filteredRuns.p', 'wb')
        pickle.dump(directory, pickle_out)
        pickle_out.close()

print("done!")

