import org.jlab.groot.data.H1F
import org.jlab.groot.data.TDirectory
import org.jlab.jnp.hipo4.data.Bank
import org.jlab.jnp.hipo4.data.Event
import org.jlab.jnp.hipo4.io.HipoReader
import org.jlab.jnp.physics.EventFilter
import org.jlab.jnp.physics.Particle
import org.jlab.jnp.physics.PhysicsEvent
import org.jlab.jnp.reader.DataManager

List<String> dataFiles = FileFinder.getFiles("/w/hallb-scifs17exp/clas12/viducic/data/rga/*");

H1F hMxp = new H1F("hMxP", 140, 0.4, 1.4);
hMxp.setTitle("mx_P w/ |mx2_PePipPim| < 0.01 && me_PePipPim < 0.1");
hMxp.setFillColor(44);

H1F hCutMxp = new H1F("hCutMxp", 140, 0.4, 1.4);
hCutMxp.setTitle("mx_P w/ cut|mx2_PePipPim| < 0.01 && mp_PePipPim > 0.1 && |mx_PePipPim.p() - pgam| < 0.1 && osTheta > 0.99");
hCutMxp.setFillColor(44);

H1F hMx2_PePipPim = new H1F("hMx2_PiPipPim", 210, -0.1, 0.1);
hMx2_PePipPim.setTitle("Missing mass squared of pePipPim");
hMx2_PePipPim.setFillColor(44);

H1F hMe_PePipPim = new H1F("hMe_PiPipPim", 210, -0.1, 2);
hMe_PePipPim.setTitle("Missing momentum of pePipPim w/ |mx2_PePipPim| < 0.01");
hMe_PePipPim.setFillColor(44);

H1F himPipPim = new H1F("himPipPim", 140, 0.4, 1.4);
himPipPim.setTitle("IM_PipPim w/ cut|mx2_PePipPim| < 0.01 && me_PePipPim < 0.1");
himPipPim.setFillColor(44);


TDirectory dir = new TDirectory();
dir.mkdir("/CutPlots");

dir.mkdir("/Plots");
dir.cd("/CutPlots");

double beamEnergy = 10.6;
int nEvents = 0;

for (String dataFile : dataFiles) {
    println("done " + (dataFiles.indexOf(dataFile) + 1) + " out of " + dataFiles.size() + " files");

    HipoReader reader = new HipoReader();
    reader.open(dataFile);

    Bank particles = new Bank(reader.getSchemaFactory().getSchema("REC::Particle"));
    Bank conf = new Bank(reader.getSchemaFactory().getSchema("RUN::config"));
    Event event = new Event();

    EventFilter filter = new EventFilter("11:2212:211:-211:Xn:X+:X-");

    while (reader.hasNext()){
        nEvents++;
        if (nEvents % 10000 == 0) {
            System.out.println("done " + nEvents);
        }

        reader.nextEvent(event);
        event.read(particles);
        event.read(conf);

        PhysicsEvent physEvent = DataManager.getPhysicsEvent(beamEnergy, particles);

        if(filter.isValid(physEvent)){
            Particle p = physEvent.getParticle("[2212]");
            Particle e = physEvent.getParticle("[11]");
            Particle pip = physEvent.getParticle("[211]");
            Particle pim = physEvent.getParticle("[-211]");
            Particle mx_P = physEvent.getParticle("[b] + [t] - [11] - [2212]");
            Particle mx_PePipPim = physEvent.getParticle("[b] + [t] - [11] - [2212] - [211] - [-211]");
            Particle im_PipPim = physEvent.getParticle(('[211] + [-211]'))

            if(Math.abs(e.theta()) < 5 && Math.abs(pip.theta()) < 35 && Math.abs(pim.theta()) < 35 && Math.abs(p.theta()) < 35) {
                hMx2_PePipPim.fill(mx_PePipPim.mass2());
                if (Math.abs(mx_PePipPim.mass2()) < 0.01) {
                    hMe_PePipPim.fill(mx_PePipPim.e());
                }

                if(Math.abs(mx_PePipPim.mass2()) < 0.01 && mx_PePipPim.e() > 0.1){
                    hMxp.fill(mx_P.mass());
                    himPipPim.fill(im_PipPim.mass());
                }

            }

        }
    }
    reader.close();
}

dir.addDataSet(hMx2_PePipPim);
dir.addDataSet(hMe_PePipPim);

dir.cd("/Plots");
dir.addDataSet(hMxp);
dir.addDataSet(himPipPim);

dir.writeFile("/work/clas12/viducic/rho/clas12/results/exclusiveRhoAnalysis_RGA.hipo");
println("done");