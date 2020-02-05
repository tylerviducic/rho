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
hMMProtonElectron.setTitle("Missing mass of Proton-Electron-Pion with no cuts");
H1F hIMProtonPion = new H1F("hIMProtonPion", 250, 1.0, 1.25);
hIMProtonPion.setTitle("Invariant mass of Proton-Pion  with no cuts");
H1F hMMElectronKaon = new H1F("hIMElectronKaon", 100, 1.0, 2.5);
hMMElectronKaon.setTitle("Missing mass of electron-kaon");
H1F hMMProtonElectronCut = new H1F("hMMProtonElectronCut", 200, 0, 1);
hMMProtonElectronCut.setTitle("Missing mass of Proton-Electron-Pion with cuts");
H1F hIMProtonPionCut = new H1F("hIMProtonPionCut", 250, 1.0, 1.25)
hIMProtonPionCut.setTitle("Invariant mass of Proton-Pion  with cuts");

TCanvas c1 = new TCanvas("c1", 1000, 1000);

c1.divide(2,2);
c1.getCanvas().initTimer(1000);
c1.cd(0).draw(hIMProtonPion);
c1.cd(1).draw(hMMProtonElectronCut);
c1.cd(2).draw(hIMProtonPionCut);
c1.cd(3).draw(hMMProtonElectron);



/*TCanvas c2 = new TCanvas("c2", 500, 500);
TCanvas c3 = new TCanvas("c3", 500, 500);
TCanvas c4 = new TCanvas("c4", 500, 500);
TCanvas c5 = new TCanvas("c5", 500, 500);
*/
//String directory = "/w/hallb-scifs17exp/clas12/viducic/data/clas12/lambda/tagger";
String directory = "/w/hallb-scifs17exp/clas12/viducic/data/clas12/lambda/forward";
//String directory = "/home/tylerviducic/research/rho/clas12/data/lambda/forward";
HipoChain reader = new HipoChain();
reader.addDir(directory);
reader.open();

Event event = new Event();
Bank particle = new Bank(reader.getSchemaFactory().getSchema("REC::Particle"));

EventFilter filter = new EventFilter("11:2212:-211:Xn:X+:X-");

while (reader.hasNext()){
    reader.nextEvent(event);
    event.read(particle);

    PhysicsEvent physicsEvent = DataManager.getPhysicsEvent(10.6, particle);

    Particle missingKaon = physicsEvent.getParticle("[b] + [t] - [2212] - [11] - [-211]");
    Particle protonPion = physicsEvent.getParticle("[2212] + [-211]");

    hIMProtonPion.fill(protonPion.mass());
    if(protonPion.mass() > 1.1 && protonPion.mass() < 1.125){
        hMMProtonElectronCut.fill(missingKaon.mass());
    }

    hMMProtonElectron.fill(missingKaon.mass());
    if(missingKaon.mass() > 0.4 && missingKaon.mass() < 0.6){
        hIMProtonPionCut.fill(protonPion.mass());
        if (filter.isValid(physicsEvent)){
            Particle electronKaon = physicsEvent.getParticle("[b] + [t] - [11] - [321]");
            hMMElectronKaon.fill(electronKaon.mass());
        }
    }

}


