package org.jlab

import org.jlab.jnp.hipo.data.HipoEvent
import org.jlab.jnp.hipo.io.HipoReader
//String dataFile = "/home/tylerviducic/research/rho/clas12/data/run_43491_full.hipo";
String dataFile = "/home/physics/research/rho/clas12/data/run_43526_full_filtered.hipo";

HipoReader reader = new HipoReader();
reader.open(dataFile);

while (reader.hasNext()){
    HipoEvent event = reader.readNextEvent();
    if(event.hasGroup("EVENT::particle")){
        HipoGroup bank = event.getGroup("EVENT::particle");
        //bank.show();
        int nrows = bank.getNode("px").getDataSize();
        println ("---------------------------------------");
/*        for(int i = 0; i < nrows; i++){
            System.out.print("particle" + (i+1) + " px: " + bank.getNode("px").getFloat(i));
            print " ";
        }*/
    }
    //event.show();
}
