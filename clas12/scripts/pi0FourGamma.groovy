import org.jlab.groot.data.H1F
import org.jlab.groot.data.H2F
import org.jlab.groot.ui.TCanvas
import org.jlab.jnp.hipo4.data.Bank
import org.jlab.jnp.hipo4.data.Event
import org.jlab.jnp.hipo4.io.HipoChain
import org.jlab.jnp.physics.EventFilter
import org.jlab.jnp.physics.Particle
import org.jlab.jnp.physics.PhysicsEvent
import org.jlab.jnp.reader.DataManager


H2F hpionpion = new H2F("pionpion", 60, 0, 0.3, 60, 0, 0.3);
hpionpion.setTitleX("first pion");
hpionpion.setTitleY("second pion");

H1F hf0 = new H1F("f0", 100, 0.0, 2.0);
hf0.setTitle("IM(pi0pi0)");

H2F hmp = new H2F("mp", 100, -0.5, 0.5, 100, -0.5, 0.5);
hmp.setTitle("Missing px vs missing py of all particles");

H1F hmm2 = new H1F("mm2", 100, -0.5, 0.5);
hmm2.setTitle("Missing mass2 of all particles");

H1F hmxP = new H1F("mxP", 100, 0.5, 1.5);
hmxP.setTitle("Missing mass of electron and pi0pi0");

TCanvas c1 = new TCanvas("c1", 1000, 1000);
c1.divide(2, 2);
c1.getCanvas().initTimer(1000);

c1.cd(0).draw(hpionpion);
c1.cd(1).draw(hf0);
c1.cd(2).draw(hmm2);
c1.cd(3).draw(hmxP);
//c1.cd(3).draw(hmp);


String dataFile = "/w/hallb-scifs17exp/clas12/viducic/data/clas12/premakoff/pi0pi0_skim4.hipo";
HipoChain reader = new HipoChain();
reader.addFile(dataFile);
reader.open();

Event event = new Event();
Bank particle = new Bank(reader.getSchemaFactory().getSchema("REC::Particle"));
Bank calorimeter = new Bank(reader.getSchemaFactory().getSchema("REC::Calorimeter"));

EventFilter eventFilter = new EventFilter("11:2212:22:22:22:22:Xn");

while (reader.hasNext()) {
    reader.nextEvent(event);
    event.read(particle);
    event.read(calorimeter);

    PhysicsEvent physicsEvent = DataManager.getPhysicsEvent(10.6, particle);

    if (eventFilter.isValid(physicsEvent)) {

        ArrayList<Integer> photonIndex = new ArrayList<>();
        ArrayList<Particle> photons = new ArrayList<>();

        for(int i = 0; i < physicsEvent.count(); i++){
            if (photonIndex.size() > 4){
                break;
            }
            Particle currentParticle = physicsEvent.getParticle(i);
            if(currentParticle.pid() == 22 && currentParticle.e() > 0.5){
                photonIndex.add(i);
                photons.add(currentParticle);
            }
        }
        if(photons.size() < 4){
            continue;
        }
        ArrayList<Integer> sectors = getSectors(photonIndex, calorimeter);

        if(sectors.get(0) == sectors.get(1) && sectors.get(1) == sectors.get(2) && sectors.get(2) == sectors.get(3)){
            continue;
        }


        Particle missingePPi0Pi0 = physicsEvent.getParticle("[b] + [t] - [2212] - [11]");
        for(int i = 0; i < photons.size(); i++){
            missingePPi0Pi0.combine(Particle.copyFrom(photons.get(i)), -1);
        }
        Particle missingePi0Pi0 = physicsEvent.getParticle("[b] + [t] - [11]");

        ArrayList<Particle> pions = getPairs(sectors, photons);
        ArrayList<Particle> kinFitPions = getKinFitPions(sectors, photons);

        if(pions.size() != 2){
            continue;
        }

        hmm2.fill(missingePPi0Pi0.mass2());
        hpionpion.fill(pions.get(0).mass(), pions.get(1).mass());

        if(Math.abs(missingePPi0Pi0.mass()) < 0.05
                && pions.get(0).mass() > 0.12 && pions.get(0).mass() < 0.15 && pions.get(1).mass() > 0.12 && pions.get(1).mass() < 0.15){

            for(int i = 0; i < kinFitPions.size(); i++){
                missingePi0Pi0.combine(Particle.copyFrom(kinFitPions.get(i)), -1);
            }

             hmm2.fill(missingePPi0Pi0.mass2());

            Particle pi0pi0 = Particle.copyFrom(kinFitPions.get(0));
            pi0pi0.combine(Particle.copyFrom(kinFitPions.get(1)), 1);

            hf0.fill(pi0pi0.mass());
            hmxP.fill(missingePi0Pi0.mass());
        }
    }
}
System.out.println("done");

//   methods

public static int getSector(int pindex, Bank calorimeter){
    for(int i = 0; i < calorimeter.getRows(); i++){
        if(calorimeter.getInt("pindex", i) == pindex){
            return calorimeter.getInt("sector", i);
        }
    }
    return -1;
}

public static ArrayList<Integer> getSectors(ArrayList<Integer> pIndex, Bank calorimeter){
    ArrayList<Integer> sectors = new ArrayList<>();
    for(int i = 0; i < pIndex.size(); i++){
        int sector = getSector(i, calorimeter);
        sectors.add(sector);
    }
    return sectors;
}


public static ArrayList<Particle> getPairs(ArrayList<Integer> sectors, ArrayList<Particle> photons){
    ArrayList<Particle> pions = new ArrayList<>();

    if(sectors.get(0) == sectors.get(1) && sectors.get(2) == sectors.get(3)){
        Particle pion1 = Particle.copyFrom(photons.get(0));
        pion1.combine(Particle.copyFrom(photons.get(1)), 1);
        Particle pion2 = Particle.copyFrom(photons.get(2));
        pion2.combine(Particle.copyFrom(photons.get(3)), 1);

        pions.add(pion1);
        pions.add(pion2);
    } else if(sectors.get(0) == sectors.get(2) && sectors.get(1) == sectors.get(3)){
        Particle pion1 = Particle.copyFrom(photons.get(0));
        pion1.combine(Particle.copyFrom(photons.get(2)), 1);
        Particle pion2 = Particle.copyFrom(photons.get(1));
        pion2.combine(Particle.copyFrom(photons.get(3)), 1);

        pions.add(pion1);
        pions.add(pion2);
    } else if(sectors.get(0) == sectors.get(3) && sectors.get(2) == sectors.get(1)){
        Particle pion1 = Particle.copyFrom(photons.get(0));
        pion1.combine(Particle.copyFrom(photons.get(3)), 1);
        Particle pion2 = Particle.copyFrom(photons.get(2));
        pion2.combine(Particle.copyFrom(photons.get(1)), 1);

        pions.add(pion1);
        pions.add(pion2);
    }

    return pions;
}

public static ArrayList<Particle> getKinFitPions(ArrayList<Integer> sectors, ArrayList<Particle> photons){
    ArrayList<Particle> kinFitPions = new ArrayList<>();
    Particle pion1 = new Particle();
    Particle pion2 = new Particle();

    if(sectors.get(0) == sectors.get(1) && sectors.get(2) == sectors.get(3)){
        pion1.initParticleWithMass(0.135, photons.get(0).px() + photons.get(1).px(), photons.get(0).py() + photons.get(1).py(), photons.get(0).pz() + photons.get(1).pz(),
                (photons.get(0).vx() + photons.get(1).vx())/2, (photons.get(0).vy() + photons.get(1).vy())/2, (photons.get(0).vz() + photons.get(1).vz())/2);
        pion2.initParticleWithMass(0.135, photons.get(2).px() + photons.get(3).px(), photons.get(2).py() + photons.get(3).py(), photons.get(2).pz() + photons.get(3).pz(),
                (photons.get(2).vx() + photons.get(3).vx())/2, (photons.get(2).vy() + photons.get(3).vy())/2, (photons.get(2).vz() + photons.get(3).vz())/2);
    } else if(sectors.get(0) == sectors.get(2) && sectors.get(1) == sectors.get(3)){
        pion1.initParticleWithMass(0.135, photons.get(0).px() + photons.get(2).px(), photons.get(0).py() + photons.get(2).py(), photons.get(0).pz() + photons.get(2).pz(),
                (photons.get(0).vx() + photons.get(2).vx())/2, (photons.get(0).vy() + photons.get(2).vy())/2, (photons.get(0).vz() + photons.get(2).vz())/2);
        pion2.initParticleWithMass(0.135, photons.get(1).px() + photons.get(3).px(), photons.get(1).py() + photons.get(3).py(), photons.get(1).pz() + photons.get(3).pz(),
                (photons.get(1).vx() + photons.get(3).vx())/2, (photons.get(1).vy() + photons.get(3).vy())/2, (photons.get(1).vz() + photons.get(3).vz())/2);
    } else if(sectors.get(0) == sectors.get(3) && sectors.get(2) == sectors.get(1)) {
        pion1.initParticleWithMass(0.135, photons.get(0).px() + photons.get(3).px(), photons.get(0).py() + photons.get(3).py(), photons.get(0).pz() + photons.get(3).pz(),
                (photons.get(0).vx() + photons.get(3).vx()) / 2, (photons.get(0).vy() + photons.get(3).vy()) / 2, (photons.get(0).vz() + photons.get(3).vz()) / 2);
        pion2.initParticleWithMass(0.135, photons.get(2).px() + photons.get(1).px(), photons.get(2).py() + photons.get(1).py(), photons.get(2).pz() + photons.get(1).pz(),
                (photons.get(2).vx() + photons.get(1).vx()) / 2, (photons.get(2).vy() + photons.get(1).vy()) / 2, (photons.get(2).vz() + photons.get(1).vz()) / 2);
    }
    return kinFitPions;
}