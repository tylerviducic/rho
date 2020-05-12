import org.jlab.groot.data.H1F
import org.jlab.groot.data.H2F
import org.jlab.groot.ui.TCanvas
import org.jlab.jnp.hipo4.data.Bank
import org.jlab.jnp.hipo4.data.Event
import org.jlab.jnp.hipo4.io.HipoChain
import org.jlab.jnp.physics.Particle
import org.jlab.jnp.physics.PhysicsEvent
import org.jlab.jnp.reader.DataManager
import org.jlab.jnp.physics.EventFilter

// ------------------------------------------  Histograms ------------------------------------------------
H1F hMissingMassEPi0= new H1F("MissingMassEPi0", 100, 0, 2);
hMissingMassEPi0.setTitle("Missing mass of e'#pi^0");

H1F hMissingMassEPPi0 = new H1F("MissingMassEPi0", 100, -0.5, 0.5);
hMissingMassEPPi0.setTitle("MM(e'p'#pi^0")

H1F hIMGamGam = new H1F("IMGamGam", 100, 0, 0.3);
hIMGamGam.setTitle("Invariant mass of #gamma#gamma");

H2F hGamGamPvsTheta = new H2F("GamGamPvsTheta", 200, 0, 6, 35, 0, 35);
hGamGamPvsTheta.setTitle("Momentum of #gamma#gamma vs opening angle between them");
hGamGamPvsTheta.setTitleX("P(#gamma#gamma)");
hGamGamPvsTheta.setTitleY("#theta(#gamma#gamma)");

H1F hQ2 = new H1F("q2", 100, 0, 3);
hQ2.setTitle("Q^2 of e'");

H1F hmissingMomentum = new H1F("missingMomentum", 100, 0, 3);
hmissingMomentum.setTitle("Missing Momentum in region of pion");

// ------------------------------------------  Histograms ------------------------------------------------

TCanvas c1 = new TCanvas("c1", 1000, 1000);
c1.divide(3, 2);
c1.getCanvas().initTimer(1000);
c1.cd(0).draw(hIMGamGam);
c1.cd(1).draw(hMissingMassEPi0);
c1.cd(2).draw(hMissingMassEPPi0);
c1.cd(3).draw(hGamGamPvsTheta);
c1.cd(4).draw(hQ2);
c1.cd(5).draw(hmissingMomentum);


String file = "/w/hallb-scifs17exp/clas12/viducic/data/clas12/pion/pi0Photoproduction_skim3.hipo";

HipoChain reader = new HipoChain();
reader.addFile(file);
reader.open();

Event event = new Event();
Bank particle = new Bank(reader.getSchemaFactory().getSchema("REC::Particle"));

EventFilter eventFilter = new EventFilter("11:2212:22:22:Xn");

while (reader.hasNext()){
    reader.nextEvent(event);
    event.read(particle);

    PhysicsEvent physicsEvent = DataManager.getPhysicsEvent(10.6, particle);

    if(!eventFilter.isValid(physicsEvent) && physicsEvent.getParticle(0).pid() == 11 && physicsEvent.getParticleByPid(2212, 0).theta() > Math.toRadians(35)){
        continue;
    }
    //Particle electron = getTaggerElectron(physicsEvent);
    Particle electron = physicsEvent.getParticleByPid(11, 0);

    ArrayList<Integer> photonIndexes = getBestPhotons(physicsEvent);
    Particle photon1 = physicsEvent.getParticle(photonIndexes.get(0));
    Particle photon2 = physicsEvent.getParticle(photonIndexes.get(1));

//    Particle pion = Particle.copyFrom(photon1);
//    pion.combine(Particle.copyFrom(photon2), 1);
    Particle pion = physicsEvent.getParticle("[22, 0] + [22, 1]");

    Particle missingEPPi0 = physicsEvent.getParticle("[b] + [t] - [2212] - [11, 0] - [22, 0] - [22, 1]");
    Particle missingEPi0 = physicsEvent.getParticle("[b] + [t] - [11, 0] - [22, 0] - [22, 1]");
//    missingEPi0.combine(Particle.copyFrom(pion), -1);
//    missingEPPi0.combine(Particle.copyFrom(pion), -1);

    double q2 = getQ2(physicsEvent.beamParticle(), electron);
    double photonTheta = Math.toDegrees(Math.acos(photon1.cosTheta(photon2)));

    if(pion.mass() < 0.15 && pion.mass() > 0.1 && missingEPPi0.p() < 1.5) {
        hQ2.fill(q2);
        hMissingMassEPPi0.fill(missingEPPi0.mass2());
        if(q2 < 0.2 && Math.abs(missingEPPi0.mass2()) < 0.1) {
            hMissingMassEPi0.fill(missingEPi0.mass());
            hIMGamGam.fill(pion.mass());
        }

        hGamGamPvsTheta.fill(pion.p(), photonTheta);
    }
}


System.out.println("done");


///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////    METHODS   //////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

public static ArrayList<Integer> getBestPhotons(PhysicsEvent physicsEvent){
    ArrayList<Integer> photons = new ArrayList<>();
    int numPhotons = physicsEvent.countByPid(22);

    for(int i = 0; i < numPhotons - 1; i++){
        Particle photon1 = Particle.copyFrom(physicsEvent.getParticleByPid(22, i));
        for (int j = i + 1; j < numPhotons; j++){
            Particle photon2 = Particle.copyFrom(physicsEvent.getParticleByPid(22, j));
            Particle pi0 = Particle.copyFrom(photon1);
            pi0.combine(photon2, 1);
            double photonTheta = Math.toDegrees(Math.acos(photon1.cosTheta(photon2)));

            if (pi0.mass() > 0.12 && pi0.mass() < 0.15){
                photons.add(physicsEvent.getParticleIndex(22, i));
                photons.add(physicsEvent.getParticleIndex(22, j));
                return photons;
            }
        }
    }
    photons.add(physicsEvent.getParticleIndex(22, 0));
    photons.add(physicsEvent.getParticleIndex(22, 1));
    return photons;
}

//public static ArrayList<Integer> getBestPhotons(PhysicsEvent physicsEvent){
//    ArrayList<Integer> photons = new ArrayList<>();
//    int numPhotons = physicsEvent.countByPid(22);
//    int photonIndex1 = 0;
//    int photonIndex2 = 1;
//
//    double closest = 69420;
//
//
//    for(int i = 0; i < numPhotons - 1; i++) {
//        Particle photon1 = Particle.copyFrom(physicsEvent.getParticleByPid(22, i));
//        for (int j = i + 1; j < numPhotons; j++) {
//
//            Particle photon2 = Particle.copyFrom(physicsEvent.getParticleByPid(22, j));
//                Particle pi0 = Particle.copyFrom(photon1);
//                pi0.combine(photon2, 1);
//                double distance = Math.abs(pi0.mass() - 0.135);
//                if (distance < closest && pi0.mass() > 0.08 && pi0.mass() < 0.17) {
//                    photonIndex1 = i;
//                    photonIndex2 = j;
//            }
//        }
//    }
//    photons.add(physicsEvent.getParticleIndex(22, photonIndex1));
//    photons.add(physicsEvent.getParticleIndex(22, photonIndex2));
//    return photons;
//}

public static Particle getTaggerElectron(PhysicsEvent physicsEvent){
    for(int i = 0; i < physicsEvent.countByPid(11); i++){
        Particle electron = physicsEvent.getParticleByPid(22, i);
        if (electron.theta() < Math.toRadians(4)){
            return electron;
        }
    }
    return physicsEvent.getParticleByPid(22, 0);
}

public static double getQ2(Particle particle1, Particle particle2){
    return 4 * particle1.e() * particle2.e() * Math.sin(particle2.theta() /2) * Math.sin(particle2.theta()/2);
}
