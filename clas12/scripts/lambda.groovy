import org.jlab.groot.data.H1F
import org.jlab.groot.ui.TCanvas
import org.jlab.jnp.hipo4.data.Bank
import org.jlab.jnp.hipo4.io.HipoChain
import org.jlab.jnp.hipo4.data.Event
import org.jlab.jnp.physics.EventFilter
import org.jlab.jnp.physics.Particle
import org.jlab.jnp.physics.PhysicsEvent
import org.jlab.jnp.reader.DataManager

H1F hMMProtonElectron = new H1F("hMMProtonElectron", 200, 0, 1);
H1F hIMProtonPion = new H1F("hIMProtonPion", 250, 1.0, 2.5);
H1F hMMElectronKaon = new H1F("hIMElectronKaon", 100, 1.0, 2.5);

TCanvas c1 = new TCanvas("c1", 500, 500);
TCanvas c2 = new TCanvas("c2", 500, 500);
TCanvas c3 = new TCanvas("c3", 500, 500);

String directory = "/w/hallb-scifs17exp/clas12/viducic/data/clas12/lambda/tagger";
//String directory = "/w/hallb-scifs17exp/clas12/viducic/data/clas12/lambda/forward";
//String directory = "/home/tylerviducic/research/rho/clas12/data/lambda/forward";
HipoChain reader = new HipoChain();
reader.addDir(directory);
reader.open();

Event event = new Event();
Bank particle = new Bank(reader.getSchemaFactory().getSchema("REC::Particle"));

EventFilter filter = new EventFilter("11:2212:-211:321:Xn");

while (reader.hasNext()){
    reader.nextEvent(event);
    event.read(particle);

    PhysicsEvent physicsEvent = DataManager.getPhysicsEvent(10.6, particle);

    Particle missingKaon = physicsEvent.getParticle("[b] + [t] - [2212] - [11] - [-211]");
    Particle protonPion = physicsEvent.getParticle("[2212] + [-211]");

    hIMProtonPion.fill(protonPion.mass());
    if(protonPion.mass() > 0.95 && protonPion.mass() < 1.2){
        hMMProtonElectron.fill(missingKaon.mass());
    }

//    hMMProtonElectron.fill(missingKaon.mass());
//    if(missingKaon.mass() > 0.4 && missingKaon.mass() < 0.6){
//        hIMProtonPion.fill(protonPion.mass());
//        if (filter.isValid(physicsEvent)){
//            Particle electronKaon = physicsEvent.getParticle("[b] + [t] - [11] - [321]");
//            hMMElectronKaon.fill(electronKaon.mass());
//        }
//    }

}

c1.draw(hMMProtonElectron);
c2.draw(hIMProtonPion);
c3.draw(hMMElectronKaon);
