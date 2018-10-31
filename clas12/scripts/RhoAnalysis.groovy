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
double beamEnergy = 10.6;

//data is coming from a file declared at the command line. Not sure if this is the only way to do this

//Declare Histograms

H1F h100 = new H1F("mx_p", "mx_P (GeV)", 200, 0, 2);
h100.setFillColor(43);

//Set Canvas

TCanvas c1 = new TCanvas("c1", 500, 600);

//update canvas every second. interval in milliseconds
c1.getCanvas().initTimer(1000);
c1.draw(h100);

HipoReader reader = new HipoReader();
reader.open(dataFile);

//Filters file for events with electron, pi+, pi-, and proton + any others
//EventFilter filter = new EventFilter("11:211:-211:X+:X-:Xn");

while (reader.hasNext()){
    HipoEvent event = reader.readNextEvent();
    PhysicsEvent physEvent = DataManager.getPhysicsEvent(beamEnergy, event);
    //if(filter.isValid(physEvent)==true){
        //Particle imPipPim = physEvent.getParticle("[211]+[-211]");
    //    h100.fill(imPipPim.mass());
    //Particle mxP = physEvent.getParticle("[b] + [t] - [211] - [-211] - [22]");
    //h100.fill(mxP.mass());
    //}
    Particle s = physEvent.getParticle("[b] + [t]");
    h100.fill(s.mass());
}

println ("done");


