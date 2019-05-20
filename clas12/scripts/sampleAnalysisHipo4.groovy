import org.jlab.groot.data.H1F
import org.jlab.groot.data.TDirectory
import org.jlab.groot.ui.TCanvas
import org.jlab.groot.ui.TGCanvas
import org.jlab.jnp.hipo4.data.Bank
import org.jlab.jnp.hipo4.data.Event
import org.jlab.jnp.hipo4.io.HipoReader
import org.jlab.jnp.physics.EventFilter
import org.jlab.jnp.physics.Particle
import org.jlab.jnp.physics.PhysicsEvent
import org.jlab.jnp.reader.DataManager
import org.jlab.jnp.utils.file.FileUtils

//String dataFile = "/work/clas12/devita/ctofCalib/rec_004013.hipo";


//List<String> dataFiles = FileFinder.getFiles("/w/hallb-scifs17exp/clas12/rg-a/production/recon/calib/v1/unfiltered/005038/*.hipo");
List<String> dataFiles = FileFinder.getFiles("/w/hallb-scifs17exp/clas12/rg-a/production/recon/pass0/v1/unfiltered/005036/*.hipo");



H1F hMxpUncut = new H1F("hMxPUncut", 200, 0.4, 1);
hMxpUncut.setTitle("mx_P");
hMxpUncut.setFillColor(43);

H1F hMxpcut = new H1F("hMxPUncut", 200, 0.4, 1);
hMxpcut.setTitle("mx_P w/ cut");
hMxpcut.setFillColor(43);

H1F hMx2_PePipPim = new H1F("hMx2_PiPipPim", 210, -0.1, 0.1);
hMx2_PePipPim.setTitle("Missing mass squared of pePipPim");

H1F hMP_PePipPim = new H1F("hMP_PiPipPim", 210, -0.1, 2);
hMP_PePipPim.setTitle("Missing momentum of pePipPim");

H1F himPipPimGamUncut = new H1F("himPipPimGamUncut", 150, 0.0, 1.5);
himPipPimGamUncut.setTitle("IM_PipPimXn");
himPipPimGamUncut.setFillColor(43);

H1F hcos = new H1F("hcos", 20, 0.99, 1);
hcos.setTitle("hcos");

//TGCanvas c = new TGCanvas("c", "myCanvas", 500, 600);


//TCanvas c1 = new TCanvas("c1", 500, 600);
//TCanvas c2 = new TCanvas("c2", 500, 600);
//TCanvas c3 = new TCanvas("c3", 500, 600);
//TCanvas c4 = new TCanvas("c4", 500, 600);
//TCanvas c5 = new TCanvas("c5", 500, 600);
//TCanvas c6 = new TCanvas("c6", 500, 600);

//c.addCanvas("c1").addCanvas("c2").addCanvas("c3");
//c.setCanvas("c1");
//c.getCanvas().initTimer(1000);
//c.setCanvas("c2");
//c.getCanvas().initTimer(1000);
//c.setCanvas("c3");
//c.getCanvas().initTimer(1000);

//c1.getCanvas().initTimer(1000);
//c2.getCanvas().initTimer(1000);
//c3.getCanvas().initTimer(1000);
//c4.getCanvas().initTimer(1000);
//c5.getCanvas().initTimer(1000);
//c6.getCanvas().initTimer(1000);

//c.setCanvas("c1");
//c.draw(hMxpUncut);
//c.setCanvas("c2");
//c.draw(hMxpcut);
//c.setCanvas("c3");
//c.draw(hMx2_PePipPim);

//c1.draw(hMxpUncut);
//c2.draw(hMxpcut);
//c3.draw(hMx2_PePipPim);
//c4.draw(hMP_PePipPim);
//c5.draw(himPipPimGamUncut);
//c6.draw(hcos);

TDirectory dir = new TDirectory();
dir.mkdir("/Plots");
dir.cd("/Plots");

// Begin Analysis //

double beamEnergy = 10.6
int nEvents = 0;


for(String dataFile : dataFiles) {
    HipoReader reader = new HipoReader();
    reader.open(dataFile);

    Bank particles = new Bank(reader.getSchemaFactory().getSchema("REC::Particle"));
    Event event = new Event();

    EventFilter filter = new EventFilter("11:2212:211:-211:Xn");

    while (reader.hasNext()) {
        reader.nextEvent(event);
        event.read(particles);

        boolean isClose = false;

        PhysicsEvent physEvent = DataManager.getPhysicsEvent(beamEnergy, particles);
        int pid = particles.getInt("pid",0);

        nEvents++;
        if (nEvents % 10000 == 0) {
            System.out.println("done " + nEvents);
        }

        if (filter.isValid(physEvent)&&pid!=11) {
            Particle mx_P = physEvent.getParticle("[b] + [t] - [11] - [2212]");
            Particle im_PipPimgam = physEvent.getParticle("[211] + [-211] + [Xn]");
            Particle mx_PePipPim = physEvent.getParticle("[b] + [t] - [11] - [2212] - [211] - [-211]");

            int nNeutrals = physEvent.countByCharge(0);


            hMx2_PePipPim.fill(mx_PePipPim.mass2());
            hMP_PePipPim.fill(mx_PePipPim.p());

            if (Math.abs(mx_PePipPim.mass2()) < 0.01 && mx_PePipPim.p() > 0.1 && Math.abs(mx_P.mass() - mx_PePipPim.p()) < 1.0) {
                hMxpUncut.fill(mx_P.mass());
                //himPipPimGamUncut.fill(im_PipPimgam.mass());
                for (int i = 0; i < nNeutrals; i++) {
                    hcos.fill(mx_PePipPim.cosTheta(physEvent.getParticleByCharge(0, i)));
                    if (mx_PePipPim.cosTheta(physEvent.getParticleByCharge(0, i)) > 0.99) {
                        isClose = true;
                        //im_PipPimgam.combine(physEvent.getParticleByCharge(0,i),0);
                    }
                }
                if (isClose) {
                    hMxpcut.fill(mx_P.mass());
                    himPipPimGamUncut.fill(im_PipPimgam.mass());
                }
            }
        }
    }

    reader.close();
}

dir.addDataSet(hMxpcut);
dir.addDataSet(hMx2_PePipPim);
dir.addDataSet(hMxpUncut);
dir.addDataSet(hcos);
dir.addDataSet(hMP_PePipPim);


dir.writeFile("/work/clas12/viducic/rho/clas12/isThereARho.hipo");
println("done");


public class FileFinder {

    public static List<String> listOfFiles = new ArrayList<String>();
    private static String newKeyWord = "";


    public static List<String> getFiles(String directory, String wildcard) {
        String newDir = "";
        if (!directory[directory.length() - 1].equals("/")) {
            newDir = directory + "/";
        } else {
            newDir = directory;
        }
        List<String> filesInDirectory = FileUtils.getFileListInDir(directory);

        if (wildcard.contains("*")) {
            newKeyWord = newDir + wildcard.replace("*", ".*");
        } else {
            newKeyWord = newDir + wildcard;
        }
        for (String f : filesInDirectory) {
            if (f.matches(newKeyWord)) {
                listOfFiles.add(f.toString());
            }
        }
        return this.listOfFiles;
    }

    public static List<String> getDirectoryName(String fullPath) {
        List<String> dirFile = new ArrayList<String>();
        int start = fullPath.lastIndexOf("/");
        dirFile.add(0, fullPath.substring(0, start + 1));
        dirFile.add(1, fullPath.substring(start + 1));
        return dirFile;
    }

    public static List<String> getFiles(String fullPath) {
        List<String> dirCombo = getDirectoryName(fullPath);
        return getFiles(dirCombo.get(0), dirCombo.get(1));
    }

}