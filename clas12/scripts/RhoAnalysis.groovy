//-------------------------------------------------------------------------------
//------------------------Created by Tyler Viducic-------------------------------
//----------------------------August 29 2018-------------------------------------
//-------------------------------God Speed---------------------------------------
//-------------------------------------------------------------------------------


import org.jlab.groot.data.H1F
import org.jlab.groot.ui.TCanvas
import org.jlab.jnp.hipo.data.HipoEvent
import org.jlab.jnp.hipo.data.HipoGroup
import org.jlab.jnp.hipo.data.HipoNode
import org.jlab.jnp.hipo.io.HipoReader
import org.jlab.jnp.physics.EventFilter
import org.jlab.jnp.physics.Particle
import org.jlab.jnp.physics.PhysicsEvent

//String dataFile = "/home/tylerviducic/research/rho/clas12/data/run_43526_full_filtered.hipo";
//String dataFile = "/home/tylerviducic/research/rho/clas12/data/run_43491_full_filtered.hipo";
//String dataFile = "/home/physics/research/rho/clas12/data/run_43526_full_filtered.hipo";
String dataFile = "/work/clas12/viducic/g11_data_filtered.hipo"
//String inputFile = args[0];

H1F h100 = new H1F("h100", 100, 0.4, 1.2);
h100.setTitle("mx_P");
h100.setFillColor(43);

H1F h101 = new H1F("h101", 100, 0.4, 1.2);
h101.setTitle("mx_P_cut");
h101.setFillColor(42);

TCanvas c1 = new TCanvas("c1", 500, 600);
c1.getCanvas().initTimer(1000);
c1.divide(1, 2);
c1.cd(0);
c1.draw(h100);
c1.cd(1);
c1.draw(h101);


HipoReader reader = new HipoReader();
reader.open(dataFile);

EventFilter filter = new EventFilter("2212:211:-211:22");

while (reader.hasNext()) {


    HipoEvent event = reader.readNextEvent();
    float beam = findBeamEnergy(event);

    PhysicsEvent physEvent = setPhysicsEvent(beam, event);
    if (filter.isValid(physEvent)) {
        //System.out.println(physEvent.toLundString());
        Particle mx_P = physEvent.getParticle("[b] + [t] - [2212]");
        Particle mx_PPipPim = physEvent.getParticle("[b] + [t] - [2212] -[211] - [-211]");
        Particle mx_PPipPimGam = physEvent.getParticle("[b] + [t] - [2212] -[211] - [-211]-[22]");
        h100.fill(mx_P.mass());

        if (Math.abs(mx_PPipPimGam.mass2()) < 0.01 && Math.abs(mx_PPipPim.mass2()) < 0.005) {
            h101.fill(mx_P.mass());
        }
    }


}

println("done");

// defining method because getPhysicsEvent only works for one type of bank

public static PhysicsEvent setPhysicsEvent(double beam, HipoEvent event) {

    PhysicsEvent physEvent = new PhysicsEvent();
    physEvent.setBeamParticle(new Particle(11, 0.0D, 0.0D, beam));
    if (!event.hasGroup("EVENT::particle")) {
        return physEvent;
    } else {
        HipoGroup group = event.getGroup("EVENT::particle");
        HipoNode nodePx = group.getNode("px");
        HipoNode nodePy = group.getNode("py");
        HipoNode nodePz = group.getNode("pz");
        HipoNode nodeVx = group.getNode("vx");
        HipoNode nodeVy = group.getNode("vy");
        HipoNode nodeVz = group.getNode("vz");
        HipoNode nodeStatus = group.getNode("status");
        int nrows = group.getMaxSize();

        for (int i = 0; i < nrows; ++i) {
            int pid = group.getNode("pid").getInt(i);
            int status = nodeStatus.getInt(i);
            int detector = 1;
            if (status >= 2000 && status < 3000) {
                detector = 2;
            }

            if (status >= 4000) {
                detector = 3;
            }

            Particle p = new Particle();
            if (pid != 0) {
                p.initParticle(pid, (double) nodePx.getFloat(i), (double) nodePy.getFloat(i), (double) nodePz.getFloat(i), (double) nodeVx.getFloat(i), (double) nodeVy.getFloat(i), (double) nodeVz.getFloat(i));
            } else {
                p.initParticleWithPidMassSquare(pid, 0, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D);
            }

            p.setStatus(detector);
            physEvent.addParticle(p);
        }

        return physEvent;
    }
}

public static float findBeamEnergy(HipoEvent event) {
    if (event.hasGroup("TAGGER::tgpb")) {
        HipoGroup group = event.getGroup("TAGGER::tgpb")
        HipoNode node = group.getNode("time");
        int nrows = node.getDataSize();
        float smallest = Math.abs(node.getFloat(0) - event.getGroup("HEADER::info").getNode("stt").getFloat(0));
        int index = 0;
        for (int i = 1; i < nrows; i++) {
            if (Math.abs(node.getFloat(i) - event.getGroup("HEADER::info").getNode("stt").getFloat(0)) < smallest) {
                smallest = node.getFloat(i);
                index = i;
            }
        }
        return group.getNode("energy").getFloat(index);
    } else return 0;

}


