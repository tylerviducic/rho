import org.jlab.groot.data.H1F
import org.jlab.groot.ui.TCanvas
import org.jlab.jnp.hipo4.data.Bank
import org.jlab.jnp.hipo4.data.Event
import org.jlab.jnp.hipo4.io.HipoReader
import org.jlab.jnp.physics.EventFilter
import org.jlab.jnp.physics.Particle
import org.jlab.jnp.physics.PhysicsEvent
import org.jlab.jnp.reader.DataManager


String dataFile = "/work/clas12/devita/ctofCalib/rec_004013.hipo";

H1F hPe_vz = new H1F("hPe_vz", 100, -10, 10);
hPe_vz.setTitle("Difference between proton and electron z-vertex");
hPe_vz.setFillColor(45);


HipoReader reader = new HipoReader();
reader.open(dataFile);

Bank particle = new Bank(reader.getSchemaFactory().getSchema("REC::Particle"));
Event event = new Event();

EventFilter filter = new EventFilter("11:2212:Xn:X+:X-");

while (reader.hasNext()){
    reader.nextEvent(event);
    event.read(particle);

    PhysicsEvent physEvent = DataManager.getPhysicsEvent(10.6, particle);

    if(filter.isValid(physEvent)){
        Particle p = physEvent.getParticle("[2212]");
        Particle e = physEvent.getParticle("[11]");

        hPe_vz.fill(p.vz()-e.vz());
    }
}

TCanvas c1 = new TCanvas("c1", 500, 500);
c1.draw(hPe_vz);

println("done");