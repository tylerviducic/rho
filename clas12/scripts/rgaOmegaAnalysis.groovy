import com.sun.xml.internal.ws.model.ParameterImpl
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

List<String> dataFiles = FileFinder.getFiles("/w/hallb-scifs17exp/clas12/rg-a/trains/v16_v2/skim8_ep/*");


H1F hMxp = new H1F("hMxp", 200, 0, 2);
H1F hIMPipPimPi0 = new H1F("hIMPipPimPi0", 200, 0, 2);
H1F hIMGamGam = new H1F("hIMGamGam", 100, -0.1, 0.5);

H1F hPCone = new H1F("hPCone", 90, 0, 90);
H1F hPipCone = new H1F("hPipCone", 90, 0, 90);
H1F hPimCone = new H1F("hPimCone", 90, 0, 90);
H1F hGam1Cone = new H1F("hGam1Cone", 90, 0, 90);
H1F hGam2Cone = new H1F("hGam2Cone", 90, 0, 90);

TDirectory dir = new TDirectory();
dir.mkdir("/Cuts");
dir.mkdir("/Plots");

EventFilter filter = new EventFilter("11:2212:211:-211:22:22");

double beamEnergy = 10.6;
double coneAngleCut = 10;

for(String dataFile : dataFiles){
    HipoReader reader = new HipoReader();
    reader.open(dataFile);

    Bank particles = new Bank(reader.getSchemaFactory().getSchema("REC::Particle"));
    Bank conf = new Bank(reader.getSchemaFactory().getSchema("RUN::config"));
    Event event = new Event();

    while(reader.hasNext()){
        reader.nextEvent(event);
        event.read(particles);
        event.read(conf);

        PhysicsEvent physEvent = DataManager.getPhysicsEvent(beamEnergy, particles);

        if(filter.isValid(physEvent)){

            // Setting up cone angle variables
            Particle p = physEvent.getParticle("[2212]");
            Particle pim = physEvent.getParticle("[-211]");
            Particle pip = physEvent.getParticle("[211]");
            Particle gam1 = physEvent.getParticle("[22, 0]");
            Particle gam2 = physEvent.getParticle("[22, 1]");

            Particle mxPipPimGamGam = physEvent.getParticle("[b] + [t] - [11] - [211] - [-211] - [22, 0] - [22, 1]");
            Particle mxPPimGamGam = physEvent.getParticle("[b] + [t] - [11] - [2212] - [-211] - [22, 0] - [22, 1]");
            Particle mxPPipGamGam = physEvent.getParticle("[b] + [t] - [11] - [2212] - [211] - [22, 0] - [22, 1]");
            Particle mxPPipPimGam1 = physEvent.getParticle("[b] + [t] - [11] - [2212] - [211] - [-211] - [22, 0]");
            Particle mxPPipPimGam2 = physEvent.getParticle("[b] + [t] - [11] - [2212] - [211] - [-211] - [22, 1]");

            pCone = Math.toDegrees(p.theta() - mxPipPimGamGam.theta());
            pimCone = Math.toDegrees(pim.theta() - mxPPipGamGam.theta());
            pipCone = Math.toDegrees(pip.theta() - mxPPimGamGam.theta());
            gam1Cone = Math.toDegrees(gam1.theta() - mxPPipPimGam2.theta());
            gam2Cone = Math.toDegrees(gam2.theta() - mxPPipPimGam1.theta());

            Particle mxp = physEvent.getParticle("[b] + [t] - [2212] - [11]");
            Particle imPipPimPi0 = physEvent.getParticle("[211] + [-211] + [22,0] + [22,1]");
            Particle imGamGam = physEvent.getParticle("[22,0] + [22,1]");

            hIMGamGam.fill(imGamGam.mass());
            hPCone.fill(pCone);
            hPimCone.fill(pimCone);
            hPipCone.fill(pipCone);
            hGam1Cone.fill(gam1Cone);
            hGam2Cone.fill(gam2Cone);

            if(imGamGam.mass() > 0.05 && pCone < coneAngleCut && pipCone < coneAngleCut && pimCone < coneAngleCut
                && gam1Cone < coneAngleCut && gam2Cone < coneAngleCut){
                hIMPipPimPi0.fill(imPipPimPi0.mass());
                hMxp.fill(mxp.mass());
            }
        }
    }
    reader.close();
}

dir.cd("/Cuts");
dir.addDataSet(hIMGamGam);
dir.addDataSet(hPCone, hPimCone, hPipCone, hGam1Cone, hGam2Cone);
dir.cd("/Plots");
dir.addDataSet(hMxp);
dir.addDataSet(hIMPipPimPi0);

dir.writeFile("/work/clas12/viducic/rho/clas12/results/exclusiveOmegaAnalysis_RGA.hipo");
println("done");

















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
