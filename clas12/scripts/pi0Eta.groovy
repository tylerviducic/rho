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

import java.nio.file.Path


// ------------------------------------------  Histograms ------------------------------------------------
H1F hMissingMassEPPi0Eta = new H1F("MissingMassEPi0Pi0", 100, -0.5, 0.5);
hMissingMassEPi0Pi0.setTitle("Missing mass of e'#gamma#gamma");

H1F hIMGamGam = new H1F("IMGamGam", 100, 0, 0.3);
hIMGamGam.setTitle("Invariant mass of #gamma#gamma");

H2F hIMPi0Eta = new H2F("IMPi0Eta", 100, 0, 0.7, 100, 0, 0.7);
hIMGamGam.setTitle("Invariant mass of photon pairs");
hIMGamGam.setTitleX("pion candidate");
hIMGamGam.setTitleY("eta candidate");


// ------------------------------------------              ------------------------------------------------


TCanvas c1 = new TCanvas("c1", 1000, 1000);
c1.divide(1, 2);
c1.getCanvas().initTimer(1000);
c1.cd(0).draw(hMissingMassEPPi0Eta);
c1.cd(1).draw(hIMPi0Eta);


String file = "/w/hallb-scifs17exp/clas12/viducic/data/clas12/pion/pi0Photoproduction_skim4.hipo";

HipoChain reader = new HipoChain();
reader.addFile(file);
reader.open();

Event event = new Event();
Bank particle = new Bank(reader.getSchemaFactory().getSchema("REC::Particle"));

EventFilter eventFilter = new EventFilter("11:2212:22:22:22:22:Xn:X+:X-");

while (reader.hasNext()) {
    reader.nextEvent(event);
    event.read(particle);

    PhysicsEvent physicsEvent = DataManager.getPhysicsEvent(10.614, particle);

    if(eventFilter.isValid(physicsEvent)) {
        ArrayList<Integer> photonsPion = getBestPhotons(physicsEvent);
        ArrayList<Integer> photonsEta = getNextPhotons(physicsEvent, photonsPion.get(0), photonsPion.get(1));

        Particle pion = Particle.copyFrom(physicsEvent.getParticleByPid(22, photonsPion.get(0)));
        pion.combine(Particle.copyFrom(physicsEvent.getParticleByPid(22, photonsPion.get(1))), 1);

        Particle eta = Particle.copyFrom(physicsEvent.getParticleByPid(22, photonsEta.get(0)));
        eta.combine(Particle.copyFrom(physicsEvent.getParticleByPid(22, photonsEta.get(1))), 1);

        Particle missingEPPi0Eta = physicsEvent.getParticle("[b] + [t] - [11] - [2212]");
        missingEPPi0Eta.combine(Particle.copyFrom(eta), -1);
        missingEPPi0Eta.combine(Particle.copyFrom(pion), -1);

        hMissingMassEPPi0Eta.fill(missingEPPi0Eta.mass());

        if (Math.abs(missingEPPi0Eta.mass()) < 0.1) {
            hIMPi0Eta.fill(pion.mass(), eta.mass());
        }
    }
}

System.out.println("done");


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

                photons.add(i);
                photons.add(j);
                return photons;
            }
        }
    }
//    Particle pi0 = Particle.copyFrom(physicsEvent.getParticleByPid(22, 0));
//    pi0.combine(Particle.copyFrom(physicsEvent.getParticleByPid(22,1)), 1);
    photons.add(0);
    photons.add(1);
    return photons;
}

public static ArrayList<Integer> getNextPhotons(PhysicsEvent physicsEvent, int skip1, int skip2) {
    ArrayList<Integer> photons = new ArrayList<>();
    int numPhotons = physicsEvent.countByPid(22);
    double highestP1 = 0;
    double highestP2 = 0;

    int photonIndex1 = -1;
    int photonIndex2 = -1;

    for (int i = 0; i < numPhotons - 1; i++) {
        for (int j = i + 1; j < numPhotons; j++) {
            if (i == skip1 || i == skip2 || j == skip1 || j == skip2) {
                continue;
            } else {
                Particle photon1 = physicsEvent.getParticleByPid(22, i);
                Particle photon2 = physicsEvent.getParticleByPid(22, j);

                if (photon1.p() > highestP2) {
                    if (photon1.p() > highestP1) {
                        highestP2 = highestP1;
                        highestP1 = photon1.p();
                        photonIndex2 = photonIndex1;
                        photonIndex1 = i;
                    } else {
                        highestP2 = photon2.p();
                        photonIndex2 = i;
                    }
                }
                if (photon2.p() > highestP2) {
                    if (photon2.p() > highestP1) {
                        highestP2 = highestP1;
                        highestP1 = photon2.p();
                        photonIndex2 = photonIndex1;
                        photonIndex1 = j;
                    } else {
                        highestP2 = photon2.p();
                        photonIndex2 = i;
                    }
                }
            }
        }
    }
    photons.add(photonIndex1);
    photons.add(photonIndex2);
    return photons;
}