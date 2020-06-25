import org.jlab.groot.data.H1F
import org.jlab.groot.ui.TCanvas
import org.jlab.jnp.hipo4.data.Bank
import org.jlab.jnp.hipo4.data.Event
import org.jlab.jnp.hipo4.io.HipoChain
import org.jlab.jnp.physics.Particle
import org.jlab.jnp.physics.PhysicsEvent
import org.jlab.jnp.reader.DataManager


H1F hNoKinNoCor = new H1F("NoKinNoCor", 100, 0, 2);
hNoKinNoCor.setTitle("MM(e#pi^0) - no kin no correction - BLACK");
hNoKinNoCor.setFillColor(1);

H1F hNoKinCor = new H1F("NoKinCor", 100, 0, 2);
hNoKinCor.setTitle("MM(e#pi^0) - no kin w/ correction - RED");
hNoKinCor.setFillColor(2);

H1F hKinNoCor = new H1F("NoKinNoCor", 100, 0, 2);
hKinNoCor.setTitle("MM(e#pi^0) - w/ kin no correction - GREEN");
hKinNoCor.setFillColor(3);

H1F hKinCor = new H1F("KinCor", 100, 0, 2);
hKinCor.setTitle("MM(e#pi^0) - w/ kin w/ correction - BLUE");
hKinCor.setFillColor(4);

String file = "/w/hallb-scifs17exp/clas12/viducic/data/clas12/pion/pi0Photoproduction_skim4_filtered.hipo";

HipoChain reader = new HipoChain();
reader.addFile(file);
reader.open();

Event event = new Event();
Bank particle = new Bank(reader.getSchemaFactory().getSchema("REC::Particle"));
Bank eCal = new Bank((reader.getSchemaFactory().getSchema("REC::Calorimeter")));

int counter = 0;

while (reader.hasNext() && counter <= 1000000){
    reader.nextEvent(event);
    event.read(particle);
    event.read(eCal);

    counter++;

    PhysicsEvent physicsEvent = DataManager.getPhysicsEvent(10.6, particle);

    Particle electron = physicsEvent.getParticleByPid(11, 0);
    Particle correctedElectron = correctedElectron(electron);

    ArrayList<Integer> photons = getBestPhotons(physicsEvent);

    Particle photon1 = physicsEvent.getParticle(photons.get(0));
    Particle photon2 = physicsEvent.getParticle(photons.get(1));

    int sector1 = getSector(photons.get(0), eCal);
    int sector2 = getSector(photons.get(1), eCal);


    if (sector1 != sector2) {
        continue;
    }

    Particle pi0 = Particle.copyFrom(photon1);
    pi0.combine(Particle.copyFrom(photon2), 1);

    Particle kinPi0 = new Particle();
    kinPi0.initParticleWithMass(0.135, photon1.px() + photon2.px(), photon1.py() + photon2.py(), photon1.pz() + photon2.pz(),
            (photon1.vx() + photon2.vx())/2,(photon1.vy() + photon2.vy())/2, (photon1.vz() + photon2.vz())/2);

    Particle missingEPi0NoCorrection = physicsEvent.getParticle("[b] + [t] - [11,0]");
    missingEPi0NoCorrection.combine(Particle.copyFrom(pi0), -1);

    Particle missingEPi0Corrected = physicsEvent.getParticle("[b] + [t]");
    missingEPi0Corrected.combine(Particle.copyFrom(correctedElectron), -1);
    missingEPi0Corrected.combine(Particle.copyFrom(pi0), -1);

    Particle missingEKinPi0NoCorrection = physicsEvent.getParticle("[b] + [t] - [11,0]");
    missingEKinPi0NoCorrection.combine(Particle.copyFrom(kinPi0), -1);

    Particle missingEKinPi0Corrected = physicsEvent.getParticle("[b] + [t]");
    missingEKinPi0Corrected.combine(Particle.copyFrom(correctedElectron), -1);
    missingEPi0Corrected.combine(Particle.copyFrom(kinPi0), -1);

    if (missingEPi0.p() < 1.0 && pi0.p() > 2 && pi0.p() < 5.5 && photonTheta < 10 && photonTheta > 3) {

        hNoKinNoCor.fill(missingEPi0NoCorrection.mass());
        hNoKinNoCor.setTitle("MM(e#pi^0) - no kin no correction");
        hNoKinCor.fill(missingEPi0Corrected.mass());
        hKinNoCor.fill(missingEKinPi0NoCorrection.mass());
        hKinCor.fill(missingEKinPi0Corrected.mass());
    }
}

TCanvas c1 = new TCanvas("c1", 1000, 1000);
c1.divide(2, 2);
c1.cd(0).draw(hNoKinNoCor);
c1.cd(1).draw(hNoKinCor);
c1.cd(2).draw(hKinNoCor);
c1.cd(3).draw(hKinCor);

TCanvas c2 = new TCanvas("c2", 1000, 1000);
c2.draw(hNoKinNoCor);
c2.draw(hNoKinCor, "same");
c2.draw(hKinNoCor, "same");
c2.draw(hKinCor, "same");


////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

public static int getSector(int pindex, Bank calorimeter) {
    for (int i = 0; i < calorimeter.getRows(); i++) {
        if (calorimeter.getInt("pindex", i) == pindex) {
            return calorimeter.getInt("sector", i);
        }
    }
    return -1;
}

public static ArrayList<Integer> getBestPhotons(PhysicsEvent physicsEvent) {
    ArrayList<Integer> photons = new ArrayList<>();
    int numPhotons = physicsEvent.countByPid(22);

    for (int i = 0; i < numPhotons - 1; i++) {
        Particle photon1 = Particle.copyFrom(physicsEvent.getParticleByPid(22, i));
        for (int j = i + 1; j < numPhotons; j++) {
            Particle photon2 = Particle.copyFrom(physicsEvent.getParticleByPid(22, j));
            Particle pi0 = Particle.copyFrom(photon1);
            pi0.combine(photon2, 1);

            if (pi0.mass() > 0.12 && pi0.mass() < 0.15) {
//                System.out.println("equation: " + imGamGam + "  --  object: " + pi0.mass());
//                System.out.println("gagik's theta: " + photon1.cosTheta(photon2) + "  --  my theta: " + myCosTheta(photon1, photon2));

                photons.add(physicsEvent.getParticleIndex(22, i));
                photons.add(physicsEvent.getParticleIndex(22, j));
                return photons;
            }
        }
    }
//    Particle pi0 = Particle.copyFrom(physicsEvent.getParticleByPid(22, 0));
//    pi0.combine(Particle.copyFrom(physicsEvent.getParticleByPid(22,1)), 1);
    photons.add(physicsEvent.getParticleIndex(22, 0));
    photons.add(physicsEvent.getParticleIndex(22, 1));
    return photons;
}

public static Particle correctedElectron(Particle electron){
    Particle correctedElectron = Particle.copyFrom(electron);
    double momentum = electron.p();
    correctedElectron.setP(6.8123 - 2.6613 * momentum + 0.41056 * momentum * momentum - 0.021082 * momentum * momentum * momentum);

    return correctedElectron;
}