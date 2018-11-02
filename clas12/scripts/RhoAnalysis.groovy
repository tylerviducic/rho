//-------------------------------------------------------------------------------
//------------------------Created by Tyler Viducic-------------------------------
//----------------------------August 29 2018-------------------------------------
//-------------------------------God Speed---------------------------------------
//-------------------------------------------------------------------------------


import org.jlab.jnp.hipo.io.*;
import org.jlab.jnp.hipo.data.*;
import org.jlab.jnp.reader.*;
import org.jlab.jnp.physics.*;

import org.jlab.groot.data.*;
import org.jlab.groot.ui.*

String dataFile = "/home/tylerviducic/research/rho/clas12/data/run_43526_full_filtered.hipo";
//String inputFile = args[0];
double beamEnergy = 1.5;

HipoReader reader = new HipoReader();
reader.open(dataFile);

EventFilter filter = new EventFilter("2212:211:-211:22");

while(reader.hasNext()){
    HipoEvent event = reader.readNextEvent();
    PhysicsEvent physEvent = DataManager.getPhysicsEvent(beamEnergy, event);
    System.out.println(physEvent.toLundString());

}

println ("done");


