import org.jlab.groot.data.H1F
import org.jlab.groot.ui.TCanvas
import org.jlab.jnp.hipo4.data.Bank
import org.jlab.jnp.hipo4.data.Event
import org.jlab.jnp.hipo4.io.HipoChain
import org.jlab.jnp.physics.Particle
import org.jlab.jnp.physics.PhysicsEvent
import org.jlab.jnp.reader.DataManager

H1F hMM2pe = new H1F("hMM2pe", 180, -0.3, 1.5);
hMM2pe.setTitle("Missing Mass^2 of detected proton and electron");

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

    hMM2pe.fill(missingPe.mass2());
}

TCanvas c1 = new TCanvas("c1", 500, 500);
c1.draw(hMM2pe);


