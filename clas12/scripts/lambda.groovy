import org.jlab.groot.data.H1F
import org.jlab.groot.ui.TCanvas
import org.jlab.jnp.hipo4.data.Bank
import org.jlab.jnp.hipo4.io.HipoChain
import org.jlab.jnp.hipo4.data.Event
import org.jlab.jnp.physics.EventFilter
import org.jlab.jnp.physics.Particle
import org.jlab.jnp.physics.PhysicsEvent
import org.jlab.jnp.reader.DataManager

H1F hMMProtonElectron = new H1F("hMMProtonElectron", 100, 0, 1);
H1F hIMProtonPion = new H1F("hIMProtonPion", 100, 0.5, 1.5);
H1F hIMElectronKaon = new H1F("hIMElectronKaon", 100, 0, 1.5);

TCanvas c1 = new TCanvas("c1", 500, 500);
TCanvas c2 = new TCanvas("c2", 500, 500);
TCanvas c3 = new TCanvas("c3", 500, 500);

String file = "";
HipoChain reader = new HipoChain();
reader.addFile(file);

Event event = new Event();
Bank particle = new Bank(reader.getSchemaFactory().getSchema("REC::Particle"));

EventFilter filter = new EventFilter("11:2212:-211:321");

while (reader.hasNext()){
    reader.nextEvent(event);
    event.read(particle);

    PhysicsEvent physicsEvent = DataManager.getPhysicsEvent(10.6, particle);

    Particle missingKaon = physicsEvent.getParticle("[b] + [t] - [2212] - [11] - [-211]");
    Particle protonPion = physicsEvent.getParticle("[2212] + [-211]");

    hMMProtonElectron.fill(missingKaon.mass());


    if (filter.isValid(physicsEvent)){
        Particle electronKaon = physicsEvent.getParticle("[11] + [321]");
    }

}

c1.draw(hMMProtonElectron);
