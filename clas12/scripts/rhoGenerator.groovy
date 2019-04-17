
//******************************************************
// Generator script for generating physics events
// for rho decay to pion+,pion- and gamma
//******************************************************
import org.jlab.jnp.hipo.io.HipoWriter
import org.jlab.jnp.physics.PhysicsEvent;
import org.jlab.jnp.physics.Particle;
import org.jlab.jnp.physics.reaction.PhysicsGenerator;
import org.jlab.jnp.physics.reaction.ThreeBodyDecay
import org.jlab.jnp.reader.LundReader
import org.jlab.jnp.reader.LundWriter

import java.util.logging.Level
import java.util.logging.Logger;

//String dataFile = "/home/physics/research/rho/clas12/data/testGenerated";
String outFile = "/home/physics/research/rho/clas12/data/conversionTest.gamp"
GampWriter writer = new GampWriter(outFile);


//--- Create a generator with photon beam
// and proton target (the default), and with decay
// products of proton and rho, and set beam
// energy range from 2.0 GeV to 5.0 GeV
PhysicsGenerator generator = new PhysicsGenerator("p","rho0");
generator.setBeamEnergy(1.5, 3.5);
//---
// Create a new event that will be filled by generator
PhysicsEvent   event = new PhysicsEvent();
//---
// define a three body decay of rho to pi+,pi-,gamma
// and add it to generator. Generator will automatically
// decay the eta prime particle in the event.
ThreeBodyDecay decay = new ThreeBodyDecay("rho0","pi+","pi-","gamma");
generator.addDecay(decay);
//---
// Loop 100 times and generate a random event. print out
// the beam energy and the event particles
for(int i = 0; i < 100; i++){
    generator.generate(event);
    Particle rho = event.getParticle("[211]+[-211]+[22]");
    System.out.println("------> EVENT");
    System.out.println("     beam energy = " + event.beamParticle().e());
    System.out.println("rho (mass) = " + rho.mass());
    System.out.println(event.toLundString());
    //System.out.println(event.toLundString() + "    5  0.    1     22  0  0    0.0000    0.0000    " + String.valueOf(event.beamParticle().e()) + "    " + String.valueOf(event.beamParticle().e()))
    //writer.writeEvent(event.toLundString() + "\n" + "    5  0.    1     22  0  0    0.0000    0.0000    " + String.valueOf(event.beamParticle().e()) + "    " + String.valueOf(event.beamParticle().e()));
    writer.writeEvent(event);
}

writer.close();


System.out.println("done");


//////////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////////

public class LundToGamp{

    private String lundFile;
    private String outFile;
    private double beamEnergy;


    LundToGamp(String lundFile, String outFile) {
        this.lundFile = lundFile
        this.outFile = outFile
    }

    public void convertLundFile(){
        BufferedWriter writer = new BufferedWriter(new FileWriter(outFile));
        LundReader reader = new LundReader(lundFile);

        while (reader.next() == true){
            PhysicsEvent event = reader.getEvent();
            double beam = event.beamParticle().e();
            Particle p = event.getParticleByPid(2212,0);
            Particle piP = event.getParticleByPid(211,0);
            Particle piM = event.getParticleByPid(-211,0);
            Particle gam = event.getParticleByPid(22,0);

            String pbeam = "1 0 0.0 0.0 " + String.valueOf(beam) + " " + String.valueOf(beam);
            String proton = "14 " + String.valueOf(p.charge()) + " " + String.valueOf(p.px()) + " " + String.valueOf(p.py())+
                    " " + String.valueOf(p.pz()) + " " + String.valueOf(p.e());
            String piPlus = "8 " + String.valueOf(piP.charge()) + " " + String.valueOf(piP.px()) + " " + String.valueOf(piP.py())+
                    " " + String.valueOf(piP.pz()) + " " + String.valueOf(piP.e());
            String piMinus = "9 " + String.valueOf(piM.charge()) + " " + String.valueOf(piM.px()) + " " + String.valueOf(piM.py())+
                    " " + String.valueOf(piM.pz()) + " " + String.valueOf(piM.e());
            String photon = "1 " + String.valueOf(gam.charge()) + " " + String.valueOf(gam.px()) + " " + String.valueOf(gam.py())+
                    " " + String.valueOf(gam.pz()) + " " + String.valueOf(gam.e());
            System.out.println(pbeam);
        }
    }
}


//////////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////////


public class GampWriter {
    private String outFile;
    private BufferedWriter writer = new BufferedWriter(new FileWriter(outFile));

    GampWriter(String outFile) {
        this.outFile = outFile
    }

    public void writeEvent(PhysicsEvent event) {
        double beam = event.beamParticle().e();
        Particle p = event.getParticleByPid(2212, 0);
        Particle piP = event.getParticleByPid(211, 0);
        Particle piM = event.getParticleByPid(-211, 0);
        Particle gam = event.getParticleByPid(22, 0);

        String pbeam = "1 0 0.0 0.0 " + String.valueOf(beam) + " " + String.valueOf(beam);
        String proton = "14 " + String.valueOf(p.charge()) + " " + String.valueOf(p.px()) + " " + String.valueOf(p.py()) +
                " " + String.valueOf(p.pz()) + " " + String.valueOf(p.e());
        String piPlus = "8 " + String.valueOf(piP.charge()) + " " + String.valueOf(piP.px()) + " " + String.valueOf(piP.py()) +
                " " + String.valueOf(piP.pz()) + " " + String.valueOf(piP.e());
        String piMinus = "9 " + String.valueOf(piM.charge()) + " " + String.valueOf(piM.px()) + " " + String.valueOf(piM.py()) +
                " " + String.valueOf(piM.pz()) + " " + String.valueOf(piM.e());
        String photon = "1 " + String.valueOf(gam.charge()) + " " + String.valueOf(gam.px()) + " " + String.valueOf(gam.py()) +
                " " + String.valueOf(gam.pz()) + " " + String.valueOf(gam.e());

        String eventString = pbeam + "\n" + piPlus + "\n" + piMinus + "\n" + proton + "\n" + photon + "\n" + "5\n";
        writer.write(eventString);
    }

    public void close() {
        if (this.writer != null) {
            try {
                this.writer.close();
            } catch (IOException var2) {
                Logger.getLogger(LundWriter.class.getName()).log(Level.SEVERE, (String) null, var2);
            }
        }
    }
}








