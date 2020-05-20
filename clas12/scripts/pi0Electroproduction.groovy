import org.jlab.groot.data.GraphErrors
import org.jlab.groot.fitter.DataFitter
import org.jlab.groot.math.F1D
import org.jlab.groot.ui.TCanvas
import org.jlab.jnp.hipo4.data.Bank
import org.jlab.jnp.hipo4.data.Event
import org.jlab.jnp.hipo4.io.HipoChain
import org.jlab.jnp.physics.EventFilter
import org.jlab.jnp.physics.Particle
import org.jlab.jnp.physics.PhysicsEvent
import org.jlab.jnp.reader.DataManager
import org.jlab.groot.data.H1F
import org.jlab.groot.data.H2F

// ------------------------------------------  Histograms ------------------------------------------------
H1F hMissingMassEPi0 = new H1F("MissingMassEPi0Pi0", 100, 0, 2);
hMissingMassEPi0.setTitle("Missing mass of e'#gamma#gamma");

H1F hIMGamGam = new H1F("IMGamGam", 200, 0, 1.0);
hIMGamGam.setTitle("Invariant mass of #gamma#gamma");

H2F hIMGamGamVsMM = new H2F("IMGamGamVsMM", 200, 0, 1.0, 100, 0, 2);
hIMGamGamVsMM.setTitle("IM(#gamma#gamma) vs MM(e#gamma#gamma)");
hIMGamGamVsMM.setTitleX("IM(#gamma#gamma)");
hIMGamGamVsMM.setTitleY("MM(e#gamma#gamma");

H1F hMMEP = new H1F("MMEP", 200, 0, 1.0);
hMMEP.setTitle("MM(e'p')");

H2F hGamGamPvsTheta = new H2F("GamGamPvsTheta", 200, 0, 6, 35, 0, 35);
hGamGamPvsTheta.setTitle("Momentum of #gamma#gamma vs opening angle between them");
hGamGamPvsTheta.setTitleX("P(#gamma#gamma)");
hGamGamPvsTheta.setTitleY("#theta(#gamma#gamma)");

H2F hIMGamGamVsMMEP = new H2F("IMGamGamVsMMEP", 200, 0, 1.0, 200, 0, 1.0);
hIMGamGamVsMMEP.setTitleX("IM(#gamma#gamma)");
hIMGamGamVsMMEP.setTitleY("MM(e'p'");

H1F hQ2 = new H1F("Q2", 100, 0, 3);
hQ2.setTitle("Q2");

H2F hIMpi0VsSin2Phi = new H2F("IMpi0VsSin2Phi", 100, 0.5, 0.2, 100, 0, 1);
hIMpi0VsSin2Phi.setTitleX("IM(#pi^0)");
hIMpi0VsSin2Phi.setTitleY("Sin^2[#phi(#gamma#pi^0)]");

// ------------------------------------------              ------------------------------------------------


TCanvas c1 = new TCanvas("c1", 1000, 1000);
c1.divide(4, 2);
c1.getCanvas().initTimer(30000);
c1.cd(0).draw(hIMGamGam);
c1.cd(1).draw(hMissingMassEPi0);
c1.cd(2).draw(hIMGamGamVsMM);
c1.cd(3).draw(hGamGamPvsTheta);
c1.cd(4).draw(hMMEP);
c1.cd(5).draw(hIMGamGamVsMMEP);
c1.cd(6).draw(hQ2);
c1.cd(7).draw(hIMpi0VsSin2Phi);

String file = "/w/hallb-scifs17exp/clas12/viducic/data/clas12/pion/pi0Photoproduction_skim4.hipo";

HipoChain reader = new HipoChain();
reader.addFile(file);
reader.open();

Event event = new Event();
Bank particle = new Bank(reader.getSchemaFactory().getSchema("REC::Particle"));

EventFilter eventFilter = new EventFilter("11:22:22:2212:Xn:X+:X-");

while (reader.hasNext()) {
    reader.nextEvent(event);
    event.read(particle);

    PhysicsEvent physicsEvent = DataManager.getPhysicsEvent(10.614, particle);

    if (eventFilter.isValid(physicsEvent)) {

        ArrayList<Integer> photons = getBestPhotons(physicsEvent);

        Particle virtualPhoton = physicsEvent.getParticle("[b] - [11]");

        Particle photon1 = physicsEvent.getParticle(photons.get(0));
        Particle photon2 = physicsEvent.getParticle(photons.get(1));

        double photonTheta = Math.toDegrees(Math.acos(photon1.cosTheta(photon2)));
        double q2 = getQ2(physicsEvent.beamParticle(), physicsEvent.getParticleByPid(11, 0));

        Particle pi0 = Particle.copyFrom(photon1);
        pi0.combine(Particle.copyFrom(photon2), 1);

        double pionPhi = Math.toDegrees(getPhiAngle(virtualPhoton, pi0));

        Particle missingEP = physicsEvent.getParticle("[b] + [t] - [2212] - [11]");
        Particle missingEPi0 = physicsEvent.getParticle("[b] + [t] - [11]");

        Particle kinFitPion = new Particle();
        kinFitPion.initParticleWithMass(0.135, photon1.px() + photon2.px(), photon1.py() + photon2.py(), photon1.pz() + photon2.pz(),
                (photon1.vx() + photon2.vx())/2, (photon1.vy() + photon2.vy())/2, (photon1.vz() + photon2.vz())/2);

        missingEPi0.combine(Particle.copyFrom(kinFitPion), -1);

        if (missingEPi0.p() < 1.0) {
            hIMGamGamVsMM.fill(pi0.mass(), missingEPi0.mass());
            hMissingMassEPi0.fill(missingEPi0.mass());
            if (missingEPi0.mass() > 0.8 && missingEPi0.mass() < 1.1) {
                hGamGamPvsTheta.fill(pi0.p(), photonTheta);
                hIMGamGam.fill(pi0.mass());
                hIMGamGamVsMMEP.fill(pi0.mass(), missingEP.mass());
                if ((pi0.p() > 2 && pi0.p() < 5.5 && photonTheta < 10 && photonTheta > 3) ||
                        (pi0.p() > 2 && pi0.p() < 5.5 && photonTheta < 10 && photonTheta > 3)) {
                    hMMEP.fill(missingEP.mass());
                    hQ2.fill(q2);
                    hIMpi0VsSin2Phi.fill(pi0.mass(), Math.sin(pionPhi) * Math.sin(pionPhi));
                }
            }
        }
    }
}


System.out.println("done");



///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////    METHODS   //////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


//public static ArrayList<Integer> getBestPhotons(PhysicsEvent physicsEvent){
//    ArrayList<Integer> photons = new ArrayList<>();
//    int numPhotons = physicsEvent.countByPid(22);
//
//    for(int i = 0; i < numPhotons - 1; i++){
//        Particle photon1 = Particle.copyFrom(physicsEvent.getParticleByPid(22, i));
//        for (int j = i + 1; j < numPhotons; j++){
//            Particle photon2 = Particle.copyFrom(physicsEvent.getParticleByPid(22, j));
//            Particle pi0 = Particle.copyFrom(photon1);
//            pi0.combine(photon2, 1);
//
//            if ((pi0.mass() > 0.12 && pi0.mass() < 0.15) || (pi0.mass() > 0.5 && pi0.mass() < 0.6)){
//                photons.add(physicsEvent.getParticleIndex(22, i));
//                photons.add(physicsEvent.getParticleIndex(22, j));
//                return photons;
//            }
//        }
//    }
//    photons.add(physicsEvent.getParticleIndex(22, 0));
//    photons.add(physicsEvent.getParticleIndex(22, 1));
//    return photons;
//}

public static ArrayList<Integer> getBestPhotons(PhysicsEvent physicsEvent){
    ArrayList<Integer> photons = new ArrayList<>();
    ArrayList<Integer> candidates = new ArrayList<>();
    int numPhotons = physicsEvent.countByPid(22);
    double closest = 1.0;
    Random r = new Random();

    for(int i = 0; i < numPhotons - 1; i++){
        Particle photon1 = Particle.copyFrom(physicsEvent.getParticleByPid(22, i));
        for (int j = i + 1; j < numPhotons; j++){
            Particle photon2 = Particle.copyFrom(physicsEvent.getParticleByPid(22, j));
            Particle pi0 = Particle.copyFrom(photon1);
            pi0.combine(photon2, 1);

            if ((pi0.mass() > 0.12 && pi0.mass() < 0.15) || (pi0.mass() > 0.5 && pi0.mass() < 0.6)){
                candidates.add(physicsEvent.getParticleIndex(22, i));
                candidates.add(physicsEvent.getParticleIndex(22, j));
            }
        }
    }

    if(candidates.size() > 1){
        for(int i = 0; i < candidates.size(); i = i+2){
            Particle candidate1 = Particle.copyFrom(physicsEvent.getParticle(candidates.get(i)));
            candidate1.combine(Particle.copyFrom(physicsEvent.getParticle(candidates.get(i + 1))), 1);
            if(candidate1.mass() > 0.12 && candidate1.mass() < 0.15){
                double distance = Math.abs(1 - 0.135/candidate1.mass());
                if(distance < closest){
                    closest = distance;
                    photons.add(0, candidates.get(i));
                    photons.add(1, candidates.get(i + 1));
                }
            }
            else if(candidate1.mass() > 0.5 && candidate1.mass() < 0.6){
                double distance = Math.abs(1 - 0.547/candidate1.mass());
                if(distance < closest){
                    closest = distance;
                    photons.add(0, candidates.get(i));
                    photons.add(1, candidates.get(i + 1));
                }
            }
        }
    }
    else{
        int randomIndex1 = -1;
        int randomIndex2 = -1;
        while (randomIndex1 == randomIndex2){
            randomIndex1 = r.nextInt(numPhotons);
            randomIndex2 = r.nextInt(numPhotons);
        }
        photons.add(0, physicsEvent.getParticleIndex(22, randomIndex1));
        photons.add(1, physicsEvent.getParticleIndex(22, randomIndex2));
    }
    return photons;
}

public static int getSector(int pindex, Bank calorimeter){
    for(int i = 0; i < calorimeter.getRows(); i++){
        if(calorimeter.getInt("pindex", i) == pindex){
            return calorimeter.getInt("sector", i);
        }
    }
    return -1;
}

public static double getQ2(Particle particle1, Particle particle2){
    return 4 * particle1.e() * particle2.e() * Math.sin(particle2.theta() /2) * Math.sin(particle2.theta()/2);
}

public static getPhiAngle(Particle particle1, Particle particle2){
    return Math.atan((particle1.py() - particle2.py()) / (particle1.px() - particle2.px()));
}
