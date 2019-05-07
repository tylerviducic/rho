
//-------------------------------------------------------------------------------
//------------------------Created by Tyler Viducic-------------------------------
//----------------------------August 29 2018-------------------------------------
//-------------------------------God Speed---------------------------------------
//-------------------------------------------------------------------------------


import org.jlab.groot.data.H1F
import org.jlab.groot.data.TDirectory
import org.jlab.groot.fitter.DataFitter
import org.jlab.groot.math.F1D
import org.jlab.groot.ui.TCanvas
import org.jlab.jnp.hipo.data.HipoEvent
import org.jlab.jnp.hipo.data.HipoGroup
import org.jlab.jnp.hipo.data.HipoNode
import org.jlab.jnp.hipo.io.HipoReader
import org.jlab.jnp.physics.EventFilter
import org.jlab.jnp.physics.Particle
import org.jlab.jnp.physics.PhysicsEvent
import org.jlab.jnp.physics.ReactionFilter
import org.jlab.jnp.reader.DataManager

//String dataFile = "/home/tylerviducic/research/rho/clas12/data/run_43526_full_filtered.hipo";
//String dataFile = "/home/tylerviducic/research/rho/clas12/data/run_43491_full_filtered.hipo";
//String dataFile = "/home/physics/research/rho/clas12/data/run_43526_full_filtered.hipo";
//String dataFile = "/work/clas12/viducic/g11_data_filtered.hipo";
//String dataFile = "/home/physics/research/rho/clas12/data/data_clas_006715.evio.00000.hipo";
String dataFile = "/w/hallb-scifs17exp/clas12/viducic/rho/clas12/filtered_run_006715.hipo"
//String dataFile = args[0];

//Declare constants

double mRho = 0.770;
double mP = 0.938272;

//Declare cut constants

double cutRhoRegion = 0.06;
double cutPGam = 0.1;
double cutMePPipPim = 0.1;
double cutMxPPipPimGam = 0.001;
double cutMxPPipPim = 0.005;
double cutMePPipPimPgamSubtract = 0.1;



//Declare Histograms
H1F hMxpUncut = new H1F("hMxPUncut", 300, 0.0, 3.0);
hMxpUncut.setTitle("mx_P");
hMxpUncut.setFillColor(43);

H1F hMx2_PePipPim = new H1F("hMx2_PiPipPim", 210, -0.1, 2);
hMx2_PePipPim.setTitle("Missing mass squared of pePipPim");

H1F hMP_PePipPim = new H1F("hMP_PiPipPim", 210, -0.1, 2);
hMx2_PePipPim.setTitle("Missing momentum of pePipPim");


H1F himPipPimGamUncut = new H1F("himPipPimGamUncut", 300, 0.0, 3.0);
himPipPimGamUncut.setTitle("IM_PipPimGam");
himPipPimGamUncut.setFillColor(43);




//Declare Canvas

TCanvas c1 = new TCanvas("c1", 500, 600);
TCanvas c2 = new TCanvas("c2", 500, 600);
TCanvas c3 = new TCanvas("c3", 500, 600);
TCanvas c4 = new TCanvas("c4", 500, 600);

c1.getCanvas().initTimer(1000);
c2.getCanvas().initTimer(1000);
c3.getCanvas().initTimer(1000);
c4.getCanvas().initTimer(1000);


//Open File

HipoReader reader = new HipoReader();
reader.open(dataFile);

//Set event filter

EventFilter filter = new EventFilter("11:2212:211:-211:Xn");


// Begin Particle Loop

int nEvents  = 0;

while (reader.hasNext()) {


    HipoEvent event = reader.readNextEvent();
    //float beam = findBeamEnergy(event);
    float beam = 10.2;

//get particle data


    //PhysicsEvent physEvent = setPhysicsEvent(beam, event);
    PhysicsEvent physEvent = DataManager.getPhysicsEvent(beam, event);
    nEvents++;
    if(nEvents%10000 == 0){
        System.out.println("done " + nEvents);
    }
    if (filter.isValid(physEvent) ) {
    //if (tfilter.isValid(physEvent)) {
        //System.out.println(physEvent.toLundString());
        Particle mx_P = physEvent.getParticle("[b] + [t] - [11] - [2212]");
        Particle im_PipPimgam = physEvent.getParticle("[211] + [-211] + [22]");
        Particle mx_PePipPim = physEvent.getParticle("[b] + [t] - [11] - [2212] - [211] - [-211]");



//Fill Histograms

        //System.out.println("Missing mass is: " + mx_P.mass());
        //System.out.println("Invariant mass is: " + im_PipPimgam.mass());
        hMxpUncut.fill(mx_P.mass());
        himPipPimGamUncut.fill(im_PipPimgam.mass());
        hMx2_PePipPim.fill(mx_PePipPim.mass2());
        hMP_PePipPim.fill(mx_PePipPim.p());


    }


}

c1.draw(hMxpUncut);
c2.draw(himPipPimGamUncut);
c3.draw(hMx2_PePipPim);
c4.draw(hMP_PePipPim);


reader.close();

println("done");

//######################################################################################################################
//######################################################################################################################
//######################################################################################################################



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
