import org.jlab.groot.data.H1F
import org.jlab.groot.ui.TCanvas
import org.jlab.jnp.hipo4.data.Bank
import org.jlab.jnp.hipo4.data.Event
import org.jlab.jnp.hipo4.io.HipoChain
import org.jlab.jnp.physics.EventFilter
import org.jlab.jnp.physics.Particle
import org.jlab.jnp.physics.PhysicsEvent
import org.jlab.jnp.reader.DataManager

H1F hMMElectronKaon = new H1F("hIMElectronKaon", 250, 1.0, 4.0);
hMMElectronKaon.setTitle("Missing mass of electron-kaon");
H1F hPion = new H1F("hPion", 250, -0.5, 4.0);
hPion.setTitle("Missing mass of e, p, k+");

TCanvas c1 = new TCanvas("c1", 1000, 1000);

c1.divide(2,1);
c1.getCanvas().initTimer(1000);
c1.cd(0).draw(hPion);
c1.cd(1).draw(hMMElectronKaon);

//String file = "/w/hallb-scifs17exp/clas12/viducic/data/clas12/lambda/tagger/kaon_detected_5038.hipo"
String directory = "/volatile/clas12/users/clas12/rich/dst/recon/005038/*";
HipoChain reader = new HipoChain();
reader.addFile(file);
reader.open();

Event event = new Event();
Bank particle = new Bank(reader.getSchemaFactory().getSchema("REC::Particle"));

EventFilter filter = new EventFilter("11:2212:321:Xn");

while (reader.hasNext()){
    reader.nextEvent(event);
    event.read(particle);

    PhysicsEvent physicsEvent = DataManager.getPhysicsEvent(10.6, particle);
    if (filter.isValid(physicsEvent) && physicsEvent.getParticleByPid(11, 0).theta() < Math.toRadians(5)) {
        Particle missingEKaon = physicsEvent.getParticle("[b] + [t] - [11] - [321]");
        Particle pion = physicsEvent.getParticle("[b] + [t] - [11] - [321] - [2212]");

        hPion.fill(pion.mass2());
        hMMElectronKaon.fill(missingEKaon.mass());
    }
}

System.out.println("done");