
package org.jlab

import org.jlab.jnp.hipo.data.HipoEvent
import org.jlab.jnp.hipo.data.HipoGroup
import org.jlab.jnp.hipo.io.HipoReader
//String dataFile = "/home/tylerviducic/research/rho/clas12/data/run_43491_full.hipo";
//String dataFile = "/home/physics/research/rho/clas12/data/run_43526_full_filtered.hipo";
String dataFile = '/work/clas12/viducic/g11TestFile_filtered.hipo'

//dataFile = args[0]

HipoReader reader = new HipoReader();
reader.open(dataFile);
count = 0;
// Loop over all events
while(reader.hasNext()==true){
  HipoEvent event = reader.readNextEvent();
    if (event.hasGroup("EVENT::particle")==true){
        HipoGroup bank = event.getGroup("EVENT::particle");
        int nrows = bank.getNode("pid").getDataSize();
        //System.out.println("rows (particles) = " + nrows);
        for(int i = 0; i < nrows; i++){
            if(bank.getNode("pid").getInt(i) == 47 || bank.getNode("pid").getInt(i) == 49){
                System.out.println("Has PID 47");
                count+=1;
                System.out.println(count);
            }
            //System.out.print(bank.getNode("pid").getInt(i) + "  ");
            //println " "
        }

    }
  }


