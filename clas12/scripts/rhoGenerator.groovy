
//******************************************************
// Generator script for generating physics events
// for rho decay to pion+,pion- and gamma
//******************************************************
import org.jlab.groot.data.H1F
import org.jlab.jnp.hipo.io.HipoWriter
import org.jlab.jnp.pdg.DistributionFunc
import org.jlab.jnp.pdg.PDGDatabase
import org.jlab.jnp.pdg.PDGParticle
import org.jlab.jnp.pdg.PhysicsConstants
import org.jlab.jnp.physics.LorentzVector
import org.jlab.jnp.physics.PhysicsEvent;
import org.jlab.jnp.physics.Particle
import org.jlab.jnp.physics.Vector3
import org.jlab.jnp.physics.reaction.DecayKinematics
import org.jlab.jnp.physics.reaction.IDecay;
import org.jlab.jnp.physics.reaction.PhysicsGenerator;
import org.jlab.jnp.physics.reaction.ThreeBodyDecay
import org.jlab.jnp.physics.reaction.TwoBodyDecay
import org.jlab.jnp.reader.LundReader
import org.jlab.jnp.reader.LundWriter

import java.util.logging.Level
import java.util.logging.Logger;

//String dataFile = "/home/physics/research/rho/clas12/data/testGenerated";
String outFile = "/home/physics/research/rho/clas12/data/conversionTest.gamp"
GampWriter writer = new GampWriter(outFile);

//H1F h1 = new H1F("h1")



//--- Create a generator with photon beam
// and proton target (the default), and with decay
// products of proton and rho, and set beam
// energy range from 2.0 GeV to 5.0 GeV
MyGenerator generator = new MyGenerator("p","rho0");
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
for(int i = 0; i < 1000; i++){
    generator.generate(event);
    Particle rho = event.getParticle("[211]+[-211]+[22]");
    Particle mxp = event.getParticle("[b]+[t]-[2212]");
    System.out.println("------> EVENT");
    System.out.println("     beam energy = " + event.beamParticle().e());
    System.out.println("invariant mass of pi pi gamma -> rho (mass) = " + rho.mass());
    System.out.println("missing mass of the proton -> " + mxp.mass());
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

//////////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////////


public class MyGenerator{

    private MyTwoBodyDecay decayCM = null;
    private double gammaEnergyMin = 3.0D;
    private double gammaEnergyMax = 5.0D;
    private List<IDecay> decayList = new ArrayList();

    public MyGenerator(String p1, String p2) {
        PDGParticle part1 = PDGDatabase.getParticleByName(p1);
        PDGParticle part2 = PDGDatabase.getParticleByName(p2);
        if (part1 != null && part2 != null) {
            this.decayCM = new MyTwoBodyDecay(8600, part1.pid(), part2.pid());
        } else {
            System.out.println("**** ERROR **** : decay particles not found");
        }

    }

    public void setBeamEnergy(double min, double max) {
        this.gammaEnergyMin = min;
        this.gammaEnergyMax = max;
    }

    public void addDecay(IDecay idc) {
        this.decayList.add(idc);
    }

    public void generate(PhysicsEvent event) {
        event.clear();
        Particle p = new Particle();
        double r = Math.random();
        double energy = this.gammaEnergyMin + (this.gammaEnergyMax - this.gammaEnergyMin) * r;
        event.setTargetParticle(new Particle(2212, 0.0D, 0.0D, 0.0D));
        event.setBeamParticle(new Particle(22, 0.0D, 0.0D, energy));
        LorentzVector gamma = new LorentzVector(0.0D, 0.0D, energy, energy);
        LorentzVector proton = new LorentzVector(0.0D, 0.0D, 0.0D, PhysicsConstants.massProton());
        gamma.add(proton);
        p.initParticleWithMass(gamma.mass(), gamma.px(), gamma.py(), gamma.pz(), 0.0D, 0.0D, 0.0D);
        p.pid(8600);
        event.addParticle(p);
        this.decayCM.decayParticles(event);
        int decaySize = this.decayList.size();

        for(int i = 0; i < decaySize; ++i) {
            ((IDecay)this.decayList.get(i)).decayParticles(event);
        }

    }
}

//////////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////////


public class MyTwoBodyDecay{

    int decayParticleID1;
    int decayParticleID2;
    int parentParticleID;
    LorentzVector decayProd1;
    LorentzVector decayProd2;
    DistributionFunc func = new DistributionFunc();

    public MyTwoBodyDecay() {
        this.setDecayParticle(111);
        this.setDecayProducts(22, 22);
    }

    public MyTwoBodyDecay(int parentID, int childid1, int childid2) {
        this.decayProd1 = new LorentzVector();
        this.decayProd2 = new LorentzVector();
        this.setDecayParticle(parentID);
        this.setDecayProducts(childid1, childid2);
    }

    public MyTwoBodyDecay(String parent, String child1, String child2) {
        this.setDecayParticle(parent);
        this.setDecayProducts(child1, child2);
    }

    public void init() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void setDecayParticle(int id) {
        this.parentParticleID = id;
    }

    public void setDecayParticle(String name) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void setDecayProducts(int pid1, int pid2) {
        this.decayParticleID1 = pid1;
        this.decayParticleID2 = pid2;
    }

    public void setDecayProducts(String name1, String name2) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void setDecayProducts(int pid1, int pid2, int pid3) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void setDecayProducts(String name1, String name2, String name3) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    private double getCosThetaRandom() {
        return Math.random() * 2.0D - 1.0D;
    }

    private double getPhiRandom() {
        return Math.random() * 2.0D * 3.141592653589793D - 3.141592653589793D;
    }

    public void decayParticles(PhysicsEvent event) {
        double cosTheta = this.getCosThetaRandom();
        double phi = this.getPhiRandom();
        PDGParticle p1 = PDGDatabase.getParticleById(this.decayParticleID1);
        PDGParticle p2 = PDGDatabase.getParticleById(this.decayParticleID2);
        Particle mother = event.getParticleByPid(this.parentParticleID, 0);
        LorentzVector vector = new LorentzVector();
        vector.copy(mother.vector());
        //LorentzVector[] vec = DecayKinematics.getDecayParticles(vector, p1.mass(), p2.mass(), Math.acos(cosTheta), phi);
        func.initBreitWigner(p2.mass(), p2.width(), 800);
        double m = func.getRandom();
        System.out.println("Mass from breitwigner: " + m);
        LorentzVector[] vec = DecayKinematics.getDecayParticles(vector, p1.mass(), m, Math.acos(cosTheta), phi);
        Vector3 vectBoost = vector.boostVector();
        vec[0].boost(vectBoost);
        vec[1].boost(vectBoost);
        Particle r = new Particle();
        r.initParticleWithMass(vec[1].mass(), vec[1].px(), vec[1].py(), vec[1].pz(),mother.vertex().x(), mother.vertex().y(), mother.vertex().z());
        r.pid(p2.pid());
        this.decayProd1.copy(vec[0]);
        this.decayProd2.copy(vec[1]);
        int index = event.getParticleIndex(this.parentParticleID, 0);
        event.removeParticle(index);
        event.addParticle(new Particle(this.decayParticleID1, vec[0].px(), vec[0].py(), vec[0].pz(), mother.vertex().x(), mother.vertex().y(), mother.vertex().z()));
        //event.addParticle(new Particle(this.decayParticleID2, vec[1].px(), vec[1].py(), vec[1].pz(), mother.vertex().x(), mother.vertex().y(), mother.vertex().z()));
        event.addParticle(r);
        System.out.println("***********" + event.toLundString());
        System.out.println("mass of event rho: " + event.getParticleByPid(113,0).mass());
        System.out.println("--------------------------------------------------------------");
    }
}

//////////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////////


public class MyThreeBodyDecay implements IDecay {
    int decayParticleID;
    int[] productParticleID;

    public MyThreeBodyDecay() {
        this.decayParticleID = 221;
        this.productParticleID = new int[3];
        this.productParticleID[0] = 211;
        this.productParticleID[1] = -211;
        this.productParticleID[2] = 22;
    }

    public MyThreeBodyDecay(String parent, String child1, String child2, String child3) {
        this.productParticleID = new int[3];
        this.setDecayParticle(parent);
        this.setDecayProducts(child1, child2, child3);
    }

    public void init() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void setDecayParticle(int id) {
        if (PDGDatabase.getParticleById(id) == null) {
            System.out.println("ThreeBodyDecay:setDecayParticle ERROR ---> can not find  particle with pid=" + id);
        } else {
            this.decayParticleID = id;
        }
    }

    public void setDecayProducts(int pid1, int pid2) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void setDecayProducts(int pid1, int pid2, int pid3) {
        if (PDGDatabase.getParticleById(pid1) == null) {
            System.out.println("ThreeBodyDecay: ERROR ---> unknown particle id " + pid1);
        } else if (PDGDatabase.getParticleById(pid2) == null) {
            System.out.println("ThreeBodyDecay: ERROR ---> unknown particle id " + pid2);
        } else if (PDGDatabase.getParticleById(pid3) == null) {
            System.out.println("ThreeBodyDecay: ERROR ---> unknown particle id " + pid3);
        } else {
            this.productParticleID[0] = pid1;
            this.productParticleID[1] = pid2;
            this.productParticleID[2] = pid3;
        }
    }

    double getMassFunction(double mass, double m1, double m2, double m3) {
        double mmin = m1 + m2;
        double mmax = mass - m3;
        if (mmax < mmin) {
            System.out.println("---> error the particle m=" + mass + " can not decay to m = [" + m1 + " , " + m2 + " , " + m3 + " ]");
            return 0.0D;
        } else {
            double randomMass = Math.random();
            return mmin + Math.abs(mmax - mmin) * randomMass;
        }
    }

    public void decayParticles(PhysicsEvent event) {
        int index = event.getParticleIndex(this.decayParticleID, 0);
        if (index < 0) {
            System.out.println("----> particle with pid=" + this.decayParticleID + " does not exist in the event");
        } else {
            //PDGParticle parent = PDGDatabase.getParticleById(this.decayParticleID);
            Particle decayPart = event.getParticle(index);
            PDGParticle dp1 = PDGDatabase.getParticleById(this.productParticleID[0]);
            PDGParticle dp2 = PDGDatabase.getParticleById(this.productParticleID[1]);
            PDGParticle dp3 = PDGDatabase.getParticleById(this.productParticleID[2]);
            double massDecayCombo = this.getMassFunction(decayPart.mass(), dp1.mass(), dp2.mass(), dp3.mass());
            double decayCosThetaDL = Math.random() * 2.0D - 1.0D;
            double decayPhiDL = Math.random() * 2.0D * 3.141592653589793D - 3.141592653589793D;
            //Particle decayPart = event.getParticle(index);
            LorentzVector[] decayGDiL = DecayKinematics.getDecayParticlesLab(decayPart.vector(), massDecayCombo, dp3.mass(), Math.acos(decayCosThetaDL), decayPhiDL);
            double decayCosThetaStep2 = Math.random() * 2.0D - 1.0D;
            double decayPhiStep2 = Math.random() * 2.0D * 3.141592653589793D - 3.141592653589793D;
            LorentzVector[] decayGDStep2 = DecayKinematics.getDecayParticlesLab(decayGDiL[0], dp1.mass(), dp2.mass(), Math.cos(decayCosThetaStep2), decayPhiStep2);
            event.addParticle(new Particle(dp1.pid(), decayGDStep2[0].px(), decayGDStep2[0].py(), decayGDStep2[0].pz(), decayPart.vertex().x(), decayPart.vertex().y(), decayPart.vertex().z()));
            event.addParticle(new Particle(dp2.pid(), decayGDStep2[1].px(), decayGDStep2[1].py(), decayGDStep2[1].pz(), decayPart.vertex().x(), decayPart.vertex().y(), decayPart.vertex().z()));
            event.addParticle(new Particle(dp3.pid(), decayGDiL[1].px(), decayGDiL[1].py(), decayGDiL[1].pz(), decayPart.vertex().x(), decayPart.vertex().y(), decayPart.vertex().z()));
            index = event.getParticleIndex(this.decayParticleID, 0);
            event.removeParticle(index);
        }
    }

    public void setDecayParticle(String name) {
        PDGParticle dp = PDGDatabase.getParticleByName(name);
        if (dp == null) {
            System.out.println("----> error. pdg does not contain name=[" + name + "]");
        } else {
            this.decayParticleID = dp.pid();
        }
    }

    public void setDecayProducts(String name1, String name2) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void setDecayProducts(String name1, String name2, String name3) {
        if (PDGDatabase.getParticleByName(name1) == null) {
            System.out.println("ThreeBodyDecay: ERROR ---> unknown particle name " + name1);
        } else if (PDGDatabase.getParticleByName(name2) == null) {
            System.out.println("ThreeBodyDecay: ERROR ---> unknown particle name " + name2);
        } else if (PDGDatabase.getParticleByName(name3) == null) {
            System.out.println("ThreeBodyDecay: ERROR ---> unknown particle name " + name3);
        } else {
            this.productParticleID[0] = PDGDatabase.getParticleByName(name1).pid();
            this.productParticleID[1] = PDGDatabase.getParticleByName(name2).pid();
            this.productParticleID[2] = PDGDatabase.getParticleByName(name3).pid();
        }
    }
}










































