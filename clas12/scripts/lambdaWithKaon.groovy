import org.jlab.groot.data.H1F
import org.jlab.groot.ui.TCanvas
import org.jlab.jnp.hipo4.data.Bank
import org.jlab.jnp.hipo4.data.Event
import org.jlab.jnp.hipo4.io.HipoChain
import org.jlab.jnp.physics.Particle
import org.jlab.jnp.physics.PhysicsEvent
import org.jlab.jnp.reader.DataManager

H1F hMMElectronKaon = new H1F("hIMElectronKaon", 100, 1.0, 2.5);
hMMElectronKaon.setTitle("Missing mass of electron-kaon");
H1F hPion = new H1F("hPion", 100, 0, 0.3);
hPion.setTitle("Missing mass of e, p, k+");

TCanvas c1 = new TCanvas("c1", 1000, 1000);

c1.divide(2,1);
c1.getCanvas().initTimer(1000);
c1.cd(0).draw(hPion);
c1.cd(1).draw(hMMElectronKaon);

String file = "/w/hallb-scifs17exp/clas12/viducic/data/clas12/lambda/tagger/kaon_detected_5038.hipo"
HipoChain reader = new HipoChain();
reader.addFile(file);
reader.open();

Event event = new Event();
Bank particle = new Bank(reader.getSchemaFactory().getSchema("REC::Particle"));

while (reader.hasNext()){
    reader.nextEvent(event);
    event.read(particle);

    PhysicsEvent physicsEvent = DataManager.getPhysicsEvent(10.6, particle);

    Particle missingEKaon = physicsEvent.getParticle("[b] + [t] - [11] - [321]");
    Particle pion = physicsEvent.getParticle("[b] + [t] - [11] - [321] - [2212]");

    hPion.fill(pion.mass());
    hMMElectronKaon.fill(missingEKaon.mass());
}

System.out.println("done");