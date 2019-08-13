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

//List<String> dataFiles = FileFinder.getFiles("/w/hallb-scifs17exp/clas12/viducic/data/rga/v2/*");
List<String> dataFiles = FileFinder.getFilesFromSubdirs("/w/hallb-scifs17exp/clas12/viducic/data/rga/v1", "*");

H1F hMxpFD = new H1F("hMxP", 230, 0.2, 2.5);
hMxpFD.setTitle("mx_P w/ |mx2_PePipPim| < 0.01 && me_PePipPim < 0.1");
hMxpFD.setFillColor(42);

H1F hCutMxp = new H1F("hCutMxp", 230, 0.2, 2.5);
hCutMxp.setTitle("mx_P w/ cut|mx2_PePipPim| < 0.01 && mp_PePipPim > 0.1 && |mx_PePipPim.p() - pgam| < 0.1 && osTheta > 0.99");
hCutMxp.setFillColor(42);

H1F hMx2_PePipPimFD = new H1F("hMx2_PiPipPim", 210, -0.1, 0.1);
hMx2_PePipPimFD.setTitle("Missing mass squared of pePipPim");
hMx2_PePipPimFD.setFillColor(42);

H1F hMe_PePipPimFD = new H1F("hMe_PiPipPim", 210, -0.1, 2);
hMe_PePipPimFD.setTitle("Missing momentum of pePipPim w/ |mx2_PePipPim| < 0.01");
hMe_PePipPimFD.setFillColor(42);

H1F himPipPimFD = new H1F("himPipPimFD", 230, 0.2, 2.5);
himPipPimFD.setTitle("IM_PipPim w/ cut|mx2_PePipPim| < 0.01 && me_PePipPim < 0.1");
himPipPimFD.setFillColor(42);

H1F hMxpCD = new H1F("hMxP", 230, 0.2, 2.5);
hMxpCD.setTitle("mx_P w/ |mx2_PePipPim| < 0.01 && me_PePipPim < 0.1");
hMxpCD.setFillColor(42);

H1F hMx2_PePipPimCD = new H1F("hMx2_PiPipPim", 210, -0.1, 0.1);
hMx2_PePipPimCD.setTitle("Missing mass squared of pePipPim");
hMx2_PePipPimCD.setFillColor(42);

H1F hMx2_PePipPimGamCD = new H1F("hMx2_PiPipPim", 210, -0.1, 0.1);
hMx2_PePipPimGamCD.setTitle("Missing mass squared of pePipPim");
hMx2_PePipPimGamCD.setFillColor(42);

H1F hMe_PePipPimCD = new H1F("hMe_PiPipPim", 210, -0.1, 2);
hMe_PePipPimCD.setTitle("Missing momentum of pePipPim w/ |mx2_PePipPim| < 0.01");
hMe_PePipPimCD.setFillColor(42);

H1F himPipPimCD = new H1F("himPipPimFD", 230, 0.2, 2.5);
himPipPimCD.setTitle("IM_PipPim w/ cut|mx2_PePipPim| < 0.01 && me_PePipPim < 0.1");
himPipPimCD.setFillColor(42);

double eThetaCut = Math.toRadians(5);
double pPipPimThetaCut = Math.toRadians(35);
double mx2PePipPimCut = 0.01;
double mePePipPimCut = 0.2;


TDirectory dir = new TDirectory();
dir.mkdir("/ForwardCuts");
dir.mkdir("/ForwardPlots");
dir.mkdir("/CentralCuts");
dir.mkdir("/CentralPlots");

double beamEnergy = 10.6;
int nEvents = 0;

for (String dataFile : dataFiles) {
    println("done " + (dataFiles.indexOf(dataFile) + 1) + " out of " + dataFiles.size() + " files");

    HipoReader reader = new HipoReader();
    try {
        reader.open(dataFile);
    }catch(IndexOutOfBoundsException e){
        continue;
    }
    Bank particles = new Bank(reader.getSchemaFactory().getSchema("REC::Particle"));
    Bank conf = new Bank(reader.getSchemaFactory().getSchema("RUN::config"));
    Event event = new Event();

    EventFilter filter = new EventFilter("11:2212:211:-211:22:Xn:X+:X-");
        while (reader.hasNext()) {
            nEvents++;
            if (nEvents % 10000 == 0) {
                System.out.println("done " + nEvents);
            }

            reader.nextEvent(event);
            event.read(particles);
            event.read(conf);

            PhysicsEvent physEvent = DataManager.getPhysicsEvent(beamEnergy, particles);

            if (filter.isValid(physEvent)) {
                Particle p = physEvent.getParticle("[2212]");
                Particle e = physEvent.getParticle("[11]");
                Particle pip = physEvent.getParticle("[211]");
                Particle pim = physEvent.getParticle("[-211]");
                Particle gam = physEvent.getParticle("[22]");
                Particle mx_P = physEvent.getParticle("[b] + [t] - [11] - [2212]");
                Particle mx_PePipPim = physEvent.getParticle("[b] + [t] - [11] - [2212] - [211] - [-211]");
                Particle mx_PePipPimGam = physEvent.getParticle("[b] + [t] - [11] - [2212] - [211] - [-211] - [22]");
                Particle im_PipPim = physEvent.getParticle("[211] + [-211] + [22]");


                if (e.theta() < eThetaCut && p.theta() < pPipPimThetaCut && pip.theta() < pPipPimThetaCut && pim.theta() < pPipPimThetaCut) {
                    hMx2_PePipPimFD.fill(mx_PePipPim.mass2());
                    hMx2_PePipPimGamCD.fill(mx_PePipPimGam.mass2());
                    if (Math.abs(mx_PePipPim.mass2()) < mx2PePipPimCut) {
                        hMe_PePipPimFD.fill(mx_PePipPim.e());
                    }

                    if (Math.abs(mx_PePipPim.mass2()) < mx2PePipPimCut && mx_PePipPim.e() > mePePipPimCut) {
                        hMxpFD.fill(mx_P.mass());
                        himPipPimFD.fill(im_PipPim.mass());
                    }

                } else if (e.theta() > eThetaCut) {//central
                    hMx2_PePipPimCD.fill(mx_PePipPim.mass2());
                    if (Math.abs(mx_PePipPim.mass2()) < mx2PePipPimCut) {
                        hMe_PePipPimCD.fill(mx_PePipPim.e());
                    }

                    if (Math.abs(mx_PePipPim.mass2()) < mx2PePipPimCut && mx_PePipPim.e() > mePePipPimCut
                            && Math.abs(mx_PePipPimGam.mass2()) < 0.01 && Math.abs(mx_PePipPim.e() - gam.e()) < 0.1 && gam.e() > 0.2) {
                        hMxpCD.fill(mx_P.mass());
                        himPipPimCD.fill(im_PipPim.mass());
                    }
                }
            }
        }
        reader.close();
}

dir.cd("/CentralCuts");
dir.addDataSet(hMx2_PePipPimCD);
dir.addDataSet(hMe_PePipPimCD);
dir.addDataSet(hMx2_PePipPimGamCD);
dir.cd("/CentralPlots");
dir.addDataSet(hMxpCD);
dir.addDataSet(himPipPimCD);
dir.cd("/ForwardCuts");
dir.addDataSet(hMx2_PePipPimFD);
dir.addDataSet(hMe_PePipPimFD);
dir.cd("/ForwardPlots");
dir.addDataSet(hMxpFD);
dir.addDataSet(himPipPimFD);

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
