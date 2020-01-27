import org.jlab.groot.data.H1F
import org.jlab.groot.data.H2F
import org.jlab.groot.ui.TCanvas
import org.jlab.jnp.hipo4.data.Bank
import org.jlab.jnp.hipo4.data.Event
import org.jlab.jnp.hipo4.io.HipoChain
import org.jlab.jnp.physics.Particle
import org.jlab.jnp.physics.PhysicsEvent
import org.jlab.jnp.reader.DataManager

H1F hMM2pe = new H1F("hMM2pe", 230, -0.3, 2.0);
hMM2pe.setTitle("Missing Mass^2 of detected proton and electron");
H1F hMM2all = new H1F("hMM2all", 100, -0.5, 0.5);
hMM2all.setTitle("Missing mass 2 of p, e, 4 photons");
H1F hPion1 = new H1F("hPion1", 100, 0, 1.0);
hPion1.setTitle("im gam1 and gam2");
H1F hPion2 = new H1F("hPion2", 100, 0, 1.0);
hPion2.setTitle("im gam3 and gam4");
H2F hPion1Pion2 = new H2F("hPion1Pion2", 100, 0, 0.3, 100, 0, 0.3);

String file = "/w/hallb-scifs17exp/clas12/viducic/data/clas12/premakoff/photons.hipo";
HipoChain reader = new HipoChain();
reader.addFile(file);
reader.open();

Event event = new Event();
Bank particle = new Bank(reader.getSchemaFactory().getSchema("REC::Particle"));

while (reader.hasNext()){
    reader.nextEvent(event);
    event.read(particle);

    PhysicsEvent physicsEvent = DataManager.getPhysicsEvent(10.6, particle);

    Particle missingPe = physicsEvent.getParticle("[b] + [t] - [2212] - [11]");
    Particle missingPeGamGamGamGam = physicsEvent.getParticle("[b] + [t] - [2212] - [11] - [22,0] - [22, 1] - [22,2] - [22, 3]")
    Particle pion1 = physicsEvent.getParticle("[22, 0] + [22,1]");
    Particle pion2 = physicsEvent.getParticle("[22, 2] + [22,3]");

    hMM2pe.fill(missingPe.mass2());
    hMM2all.fill(missingPeGamGamGamGam.mass2());

    hPion1.fill(pion1.mass());
    hPion2.fill(pion2.mass());
    hPion1Pion2.fill(pion1.mass(), pion2.mass());

}

TCanvas c1 = new TCanvas("c1", 500, 500);
c1.draw(hMM2pe);
TCanvas c2 = new TCanvas("c2", 500, 500);
c2.draw(hMM2all);

TCanvas c3 = new TCanvas("c3", 500, 500);
TCanvas c4 = new TCanvas("c4", 500, 500);
TCanvas c5 = new TCanvas("c5", 500, 500);

c3.draw(hPion1);
c4.draw(hPion2);
c5.draw(hPion1Pion2);