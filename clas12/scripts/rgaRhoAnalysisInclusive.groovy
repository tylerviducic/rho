import org.jlab.groot.data.H1F
import org.jlab.groot.data.TDirectory
import org.jlab.jnp.hipo4.data.Bank
import org.jlab.jnp.hipo4.data.Event
import org.jlab.jnp.hipo4.io.HipoReader
import org.jlab.jnp.physics.EventFilter
import org.jlab.jnp.physics.Particle
import org.jlab.jnp.physics.PhysicsEvent
import org.jlab.jnp.reader.DataManager
import org.jlab.jnp.utils.file.FileUtils

List<String> dataFiles = FileFinder.getFiles("/w/hallb-scifs17exp/clas12/viducic/data/rga/*");

H1F hMxp = new H1F("hMxP", 140, 0.4, 1.4);
hMxp.setTitle("mx_P w/ |mx2_PePipPim| < 0.01 && me_PePipPim < 0.1");
hMxp.setFillColor(42);

H1F hCutMxp = new H1F("hCutMxp", 140, 0.4, 1.4);
hCutMxp.setTitle("mx_P w/ cut|mx2_PePipPim| < 0.01 && mp_PePipPim > 0.1 && |mx_PePipPim.p() - pgam| < 0.1 && osTheta > 0.99");
hCutMxp.setFillColor(42);

H1F hMx2_PePipPim = new H1F("hMx2_PiPipPim", 210, -0.1, 0.1);
hMx2_PePipPim.setTitle("Missing mass squared of pePipPim");
hMx2_PePipPim.setFillColor(42);

H1F hMe_PePipPim = new H1F("hMe_PiPipPim", 210, -0.1, 2);
hMe_PePipPim.setTitle("Missing momentum of pePipPim w/ |mx2_PePipPim| < 0.01");
hMe_PePipPim.setFillColor(42);

H1F himPipPim = new H1F("himPipPim", 140, 0.4, 1.4);
himPipPim.setTitle("IM_PipPim w/ cut|mx2_PePipPim| < 0.01 && me_PePipPim < 0.1");
himPipPim.setFillColor(42);


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
            Particle im_PipPim = physEvent.getParticle("[211] + [-211]");

            if(Math.abs(e.theta()) < 5 && Math.abs(pip.theta()) < 35 && Math.abs(pim.theta()) < 35 && Math.abs(p.theta()) < 35) {
                hMx2_PePipPim.fill(mx_PePipPim.mass2());
                if (Math.abs(mx_PePipPim.mass2()) < 0.01) {
                    hMe_PePipPim.fill(mx_PePipPim.e());
                }

                if(Math.abs(mx_PePipPim.mass2()) < 0.01 && mx_PePipPim.e() > 0.2){
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

dir.writeFile("/work/clas12/viducic/rho/clas12/results/inclusiveRhoAnalysis_RGA.hipo");
println("done");


////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


public class FileFinder {

    public FileFinder() {
    }

    private static List<String> listOfFiles = new ArrayList<String>();
    private static String newKeyWord = "";
    public static int DEBUG_MODE = 0;

    public static List<String> getFiles(List<String> listOfDirs, String fileName) {
        for (String dir : listOfDirs) {
            getFiles(dir, fileName);
        }
        return listOfFiles;
    }

    public static List<String> getFilesFromSubdirs(String directory, String wildcard) {
        List<String> listOfDirs = getSubdirs(directory);
        return getFiles(listOfDirs, wildcard);
    }


    public static List<String> getFiles(String directory, String wildcard) {
        String newDir = "";
        if (!directory.endsWith("/")) {
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

    public static List<String> getSubdirs(String directory) {
        if (DEBUG_MODE > 0) {
            System.out.println(">>> scanning directory : " + directory);
        }

        List<String> dirList = new ArrayList();
        File[] dirs = (new File(directory)).listFiles();
        if (dirs == null) {
            if (DEBUG_MODE > 0) {
                System.out.println(">>> scanning directory : directory does not exist");
            }

            return dirList;
        } else {
            File[] var3 = dirs;
            int var4 = dirs.length;

            for (int var5 = 0; var5 < var4; ++var5) {
                File dir = var3[var5];
                if (dir.isDirectory()) {
                    if (!dir.getName().startsWith(".") && !dir.getName().endsWith("~")) {
                        dirList.add(dir.getAbsolutePath());
                    } else if (DEBUG_MODE > 0) {
                        System.out.println("[FileUtils] ----> skipping file : " + dir.getName());
                    }
                }
            }

            return dirList;
        }
    }
}
