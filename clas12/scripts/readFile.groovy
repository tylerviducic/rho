
package org.jlab

import org.jlab.jnp.hipo.data.HipoEvent;
import org.jlab.jnp.hipo.data.HipoGroup;
import org.jlab.jnp.hipo.data.HipoNode;
import org.jlab.jnp.hipo.io.HipoReader;

String dataFile = "/home/tylerviducic/research/rho/clas12/data/run_43491_full.hipo";
//dataFile = args[0]

HipoReader reader = new HipoReader();
reader.open(dataFile);
// Loop over all events
while(reader.hasNext()==true){
  HipoEvent event = reader.readNextEvent();
  if(event.hasGroup("mc::event")==true){
     HipoGroup bank = event.getGroup("mc::event");
     int nrows = bank.getNode("pid").getDataSize();
     System.out.println(" rows = " + nrows);
     for(int i = 0; i < nrows; i++)
          System.out.print(bank.getNode("pid").getShort(i) + "  ");
     System.out.println();
  }
}
