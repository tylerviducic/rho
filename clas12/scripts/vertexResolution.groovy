import org.jlab.groot.data.H1F
import org.jlab.groot.data.TDirectory
import org.jlab.groot.ui.TCanvas
import org.jlab.jnp.hipo4.data.Bank
import org.jlab.jnp.hipo4.data.Event
import org.jlab.jnp.hipo4.io.HipoReader
import org.jlab.jnp.physics.EventFilter
import org.jlab.jnp.physics.Particle
import org.jlab.jnp.physics.PhysicsEvent
import org.jlab.jnp.reader.DataManager


//String dataFile = "/work/clas12/devita/ctofCalib/rec_004013.hipo";
String dataFile = "/w/hallb-scifs17exp/clas12/viducic/run_5038_filtered.hipo/"

H1F hPe_vz = new H1F("hPe_vz", 200, -40, 40);
hPe_vz.setTitle("Difference between proton and electron z-vertex");
hPe_vz.setFillColor(45);

H1F he_vz = new H1F("he_vz", 200, -40, 40);
hPe_vz.setTitle("electron z-vertex");
hPe_vz.setFillColor(42);

H1F hp_vz = new H1F("hp_vz", 200, -40,40);
hp_vz.setTitle("proton z-vertex");
hPe_vz.setFillColor(44);

TDirectory dir = new TDirectory();
dir.mkdir("/Vertex");
dir.cd("/Vertex");


HipoReader reader = new HipoReader();
reader.open(dataFile);

Bank particle = new Bank(reader.getSchemaFactory().getSchema("REC::particle"));
Event event = new Event();

EventFilter filter = new EventFilter("11:2212:Xn:X+:X-");

while (reader.hasNext()){
    reader.nextEvent(event);
    event.read(particle);

    PhysicsEvent physEvent = DataManager.getPhysicsEvent(10.6, particle);

    if(filter.isValid(physEvent)){
        Particle p = physEvent.getParticle("[2212]");
        Particle e = physEvent.getParticle("[11]");

        if(p.p()> 1 && e.p() > 3) {
            hPe_vz.fill(p.vz() - e.vz());\
        }
        he_vz.fill(e.vz());
        hp_vz.fill(p.vz());
    }
}

//TCanvas c1 = new TCanvas("c1", 500, 500);
//c1.draw(hPe_vz);

dir.addDataSet(hPe_vz);
dir.addDataSet(he_vz);
dir.addDataSet(hp_vz);
dir.writeFile("/work/clas12/viducic/rho/clas12/vertexAnalysis.hipo");

println("done");