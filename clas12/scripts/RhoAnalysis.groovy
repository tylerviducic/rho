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

//String dataFile = "/home/tylerviducic/research/rho/clas12/data/run_43526_full_filtered.hipo";
//String dataFile = "/home/tylerviducic/research/rho/clas12/data/run_43491_full_filtered.hipo";
//String dataFile = "/home/physics/research/rho/clas12/data/run_43526_full_filtered.hipo";
String dataFile = "/work/clas12/viducic/g11_data_filtered.hipo";
//String inputFile = args[0];

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

//

//Declare Histograms
H1F hMxpUncut = new H1F("hMxPUncut", 100, 0.4, 1.2);
hMxpUncut.setTitle("mx_P");
hMxpUncut.setFillColor(43);

H1F hMxp = new H1F("hMxp", 100, 0.4, 1.2);
hMxp.setTitle("mx_P_cut");
hMxp.setFillColor(42);

H1F hPGam = new H1F("hPGam", "Energy of detected photon [GeV]", "N", 250, 0, 2.5);
hPGam.setTitle("PGam [GeV]");
hPGam.setFillColor(44);

H1F hMePPipPim = new H1F("hMePPipPimp", "Missing Energy of PPipPim [GeV]", "N", 250, 0, 2.5);
hMePPipPim.setTitle("Missing Energy of PPipPim [GeV]");
hMePPipPim.setFillColor(45);

H1F hMePPipPimpGam = new H1F("hMePPipPimpGam", "Missing Energy of PPipPim - PGam [GeV]", "N", 70, -0.2, 0.5);
hMePPipPimpGam.setTitle("Missing Energy of PPipPim - PGam [GeV]");
hMePPipPimpGam.setFillColor(41);

H1F hMxPPipPim = new H1F("hMxPPipPim", "Missing mass#^2 of PPipPim [GeV]#^2", "N", 100, -0.05, 0.05);
hMxPPipPim.setTitle("Missing mass#^2 of PPipPim [GeV]#^2");
hMxPPipPim.setFillColor(41);

H1F hMxPPipPimGam = new H1F("hMxPPipPimGam", "Missing mass#^2 of PPipPimGam [GeV]#^2", "N", 100, -0.01, 0.002);
hMxPPipPimGam.setTitle("Missing mass#^2 of PPipPimGam [GeV]#^2");
hMxPPipPimGam.setFillColor(41);

H1F hImPPipPim = new H1F("hImPPipPim", "IM(PimPim) [GeV]", "N", 100, 0, 1);
hImPPipPim.setTitle("Invariant Mass of PiPPim [GeV]");
hImPPipPim.setFillColor(42);

H1F hpP = new H1F("hpP", "p(p) [GeV]", "N", 100, 0, 2);
hpP.setTitle("Proton momentum [GeV]");

ArrayList<H1F> hSignal = new ArrayList<H1F>();
for (int i = 0; i < 66; i++) {
    H1F h = new H1F("hSignal" + (i + 1), "MX^2(PPipPim) [GeV^2]", "N", 100, -0.05, 0.05);
    h.setTitle("Signal plot for IM(PipPim) = " + (double)((i + 24) / 100) + " +/- .005" + " [GeV^2]")
    hSignal.add(h);
}

TDirectory dir = new TDirectory();
dir.mkdir("/Signal");
dir.mkdir("/Cuts");

dir.cd("/Signal");
dir.addDataSet(hMxPPipPim);
for (int i = 0; i < 66; i++) {
    dir.addDataSet(hSignal.get(i));
}


//Declare Canvas

TCanvas c1 = new TCanvas("c1", 500, 600);
TCanvas c2 = new TCanvas("c2", 500, 600);
TCanvas c3 = new TCanvas("c3", 500, 600);


c1.getCanvas().initTimer(1000);
c1.getCanvas().initTimer(1000);
c1.divide(1, 3);
c1.cd(0);
c1.draw(hMxpUncut);
c1.cd(1);
c1.draw(hMxp);
c1.cd(2);
c1.draw(hpP);

c2.getCanvas().initTimer(1000);
c2.divide(1, 3);
c2.cd(0);
c2.draw(hPGam);
c2.cd(1);
c2.draw(hMePPipPim);
c2.cd(2);
c2.draw(hMePPipPimpGam);

c3.getCanvas().initTimer(1000);
c3.divide(1, 3);
c3.cd(0);
c3.draw(hMxPPipPimGam);
c3.cd(1);
c3.draw(hMxPPipPim);
c3.cd(2);
c3.draw(hImPPipPim);

//Open File

HipoReader reader = new HipoReader();
reader.open(dataFile);

//Set event filter

EventFilter filter = new EventFilter("2212:211:-211:22");

// Begin Particle Loop

while (reader.hasNext()) {


    HipoEvent event = reader.readNextEvent();
    float beam = findBeamEnergy(event);

//get particle data

    PhysicsEvent physEvent = setPhysicsEvent(beam, event);
    if (filter.isValid(physEvent)) {
        //System.out.println(physEvent.toLundString());
        Particle mx_P = physEvent.getParticle("[b] + [t] - [2212]");
        Particle mx_PPipPim = physEvent.getParticle("[b] + [t] - [2212] -[211] - [-211]");
        Particle mx_PPipPimGam = physEvent.getParticle("[b] + [t] - [2212] -[211] - [-211]-[22]");
        Particle pgam = physEvent.getParticle("[22]");
        Particle me_PPipPim = physEvent.getParticle("[b] + [t] - [2212] - [211] - [-211]");
        Particle im_PipPim = physEvent.getParticle("[211] + [-211]");
        Particle pP = physEvent.getParticle("[2212]");

//Fill Histograms

        if (pgam.e() > cutPGam && me_PPipPim.e() > cutMePPipPim && Math.abs(mx_PPipPim.mass2()) < cutMxPPipPim
                && Math.abs(me_PPipPim.e() - pgam.e()) < cutMePPipPimPgamSubtract
                && Math.abs(mx_PPipPimGam.mass2()) < cutMxPPipPimGam) {
            hMxpUncut.fill(mx_P.mass());
        }
        if (Math.abs(mx_PPipPimGam.mass2()) < 0.01 && Math.abs(mx_PPipPim.mass2()) < 0.005) {
            hMxp.fill(mx_P.mass());
        }

        if (Math.abs(mx_P.mass() - mRho) < cutRhoRegion && me_PPipPim.e() > cutMePPipPim) {
            hPGam.fill(pgam.e());
        }

        if (Math.abs(mx_P.mass() - mRho) < cutRhoRegion && pgam.e() > cutPGam) {
            hMePPipPim.fill(me_PPipPim.e());
        }
        if (pgam.e() > cutPGam && me_PPipPim.e() > cutMePPipPim && Math.abs(mx_P.mass() - mRho) < cutRhoRegion
                && Math.abs(mx_PPipPimGam.mass2()) < cutMxPPipPimGam && Math.abs(mx_PPipPim.mass2()) < cutMxPPipPim) {
            hMePPipPimpGam.fill(me_PPipPim.e() - pgam.e());
        }

        if (pgam.e() > cutPGam && me_PPipPim.e() > cutMePPipPim && Math.abs(mx_P.mass() - mRho) < cutRhoRegion
                && Math.abs(me_PPipPim.e() - pgam.e()) < cutMePPipPimPgamSubtract
                && Math.abs(mx_PPipPim.mass2()) < cutMxPPipPim) {
            hMxPPipPimGam.fill(mx_PPipPimGam.mass2());
        }

        if (pgam.e() > cutPGam && me_PPipPim.e() > cutMePPipPim && Math.abs(mx_P.mass() - mRho) < cutRhoRegion
                && Math.abs(me_PPipPim.e() - pgam.e()) < cutMePPipPimPgamSubtract
                && Math.abs(mx_PPipPimGam.mass2()) < cutMxPPipPimGam) {
            hMxPPipPim.fill(mx_PPipPim.mass2());
        }

        if (pgam.e() > cutPGam && me_PPipPim.e() > cutMePPipPim
                && Math.abs(me_PPipPim.e() - pgam.e()) < cutMePPipPimPgamSubtract
                && Math.abs(mx_P.mass() - mRho) < cutRhoRegion && Math.abs(mx_PPipPim.mass2()) < cutMxPPipPim
                && Math.abs(mx_PPipPimGam.mass2()) < cutMxPPipPimGam) {
            hImPPipPim.fill(im_PipPim.mass())
        }

        if (pgam.e() > cutPGam && me_PPipPim.e() > cutMePPipPim
                && Math.abs(me_PPipPim.e() - pgam.e()) < cutMePPipPimPgamSubtract
                && Math.abs(mx_P.mass() - mRho) < cutRhoRegion && Math.abs(mx_PPipPim.mass2()) < cutMxPPipPim
                && Math.abs(mx_PPipPimGam.mass2()) < cutMxPPipPimGam && Math.abs(mx_PPipPim.mass2()) < cutMxPPipPim){
            hpP.fill(pP.p());
        }


        for (int i = 0; i < 66; i++) {
            double x = 0.24 + ((double)(i) / 100.0);

            if (pgam.e() > cutPGam && me_PPipPim.e() > cutMePPipPim && Math.abs(mx_P.mass() - mRho) < cutRhoRegion
                    && Math.abs(me_PPipPim.e() - pgam.e()) < cutMePPipPimPgamSubtract
                    && Math.abs(mx_PPipPimGam.mass2()) < cutMxPPipPimGam
                    && Math.abs(im_PipPim.mass() - x) < 0.005) {
                hSignal.get(i).fill(mx_PPipPim.mass2());
            }
        }
    }


}

F1D f1 = new F1D("f1", "[amp]*gaus(x,[mean],[sigma])", -0.19, 0.03);
f1.setParameter(0, 400);
f1.setParameter(1, 0.02);
f1.setParameter(2,0.15);
f1.setLineColor(42);

DataFitter.fit(f1, hMePPipPimpGam, "N");
c2.cd(2);
c2.draw(f1, "same");
f1.show();

dir.cd("/Cuts");

dir.addDataSet(hpP);
dir.addDataSet(hImPPipPim);
dir.addDataSet(hMxPPipPimGam);
dir.addDataSet(hPGam);
dir.addDataSet(hMePPipPimpGam);


dir.writeFile("myAnalysis.hipo");

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


