package org.jlab

import org.jlab.jnp.hipo.data.HipoEvent;
import org.jlab.jnp.hipo.data.HipoGroup;
import org.jlab.jnp.hipo.data.HipoNode;
import org.jlab.jnp.hipo.io.HipoReader;

//String dataFile = "/home/tylerviducic/research/rho/clas12/data/run_43491_full.hipo";
String dataFile = "/home/tylerviducic/research/rho/clas12/data/run_43526_full_filtered.hipo";

HipoReader reader = new HipoReader();
reader.open(dataFile);

while (reader.hasNext()){
    HipoEvent event = reader.readNextEvent();
    //if(event.hasGroup("EVENT::particle")){
    //    HipoGroup bank = event.getGroup("EVENT::particle");
     //   bank.show();
    //}
    event.show();
}
