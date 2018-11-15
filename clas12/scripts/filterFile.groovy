package org.jlab

import org.jlab.jnp.hipo.data.HipoEvent
import org.jlab.jnp.hipo.data.HipoGroup
import org.jlab.jnp.hipo.io.HipoReader
import org.jlab.jnp.hipo.io.HipoWriter
//String dataFile = "/home/tylerviducic/research/rho/clas12/data/run_43526_full.hipo";
//String dataFile = "/home/physics/research/rho/clas12/data/run_43491_full.hipo";
String dataFile = "/work/clas12/viducic/testfile_full.hipo"

HipoReader reader = new HipoReader();
reader.open(dataFile);
HipoWriter writer = reader.createWriter();
writer.open("/work/clas12/viducic/testfile_full_filtered.hipo");

while (reader.hasNext()==true){
    HipoEvent event = reader.readNextEvent();
    if(event.hasGroup("EVENT::particle")==true){
        HipoGroup bank = event.getGroup("EVENT::particle");
        boolean containsPip = false;
        boolean containsPim = false;
        boolean containsP = false;
        boolean containsGam = false;
        int nrows = bank.getNode("pid").getDataSize();
        for(int i = 0; i < nrows;i++){
            int pid = bank.getNode("pid").getInt(i);
            if(pid == 211){
                containsPip = true;
            }
            if(pid == -211){
                containsPim = true;
            }
            if(pid == 2212){
                containsP = true;
            }
            if(pid == 22){
                containsGam = true;
            }
        }
        if(containsPip && containsPim && containsP && containsGam){
            writer.writeEvent(event);
        }
    }
}

writer.close();

