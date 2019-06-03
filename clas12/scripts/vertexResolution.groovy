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
import org.jlab.jnp.utils.file.FileUtils


//String dataFile = "/work/clas12/devita/ctofCalib/rec_004013.hipo";
//String dataFile = "/w/hallb-scifs17exp/clas12/viducic/data/run_5038_filtered.hipo/"

List<String> dataFiles = FileFinder.getFiles("/w/hallb-scifs17exp/clas12/rg-a/trains/calibration/v4/skim4_inclusive/*");

H1F hPe_vz = new H1F("hPe_vz", 200, -40, 40);
hPe_vz.setTitle("Difference between proton and electron z-vertex");
hPe_vz.setFillColor(45);

H1F he_vz = new H1F("he_vz", 200, -40, 40);
he_vz.setTitle("electron z-vertex");
he_vz.setFillColor(42);

H1F hp_vz = new H1F("hp_vz", 200, -40,40);
hp_vz.setTitle("proton z-vertex");
hp_vz.setFillColor(44);

TDirectory dir = new TDirectory();
dir.mkdir("/Vertex");
dir.cd("/Vertex");

for(String dataFile : dataFiles) {

    HipoReader reader = new HipoReader();
    reader.open(dataFile);

    Bank particle = new Bank(reader.getSchemaFactory().getSchema("REC::Particle"));
    Event event = new Event();

    EventFilter filter = new EventFilter("11:2212:Xn:X+:X-");

    while (reader.hasNext()) {
        reader.nextEvent(event);
        event.read(particle);

        PhysicsEvent physEvent = DataManager.getPhysicsEvent(10.6, particle);

        if (filter.isValid(physEvent)) {
            Particle p = physEvent.getParticle("[2212]");
            Particle e = physEvent.getParticle("[11]");

            if (p.p() > 1 && e.p() > 3) {
                hPe_vz.fill(p.vz() - e.vz());
                he_vz.fill(e.vz());
                hp_vz.fill(p.vz());
            }
        }
    }
}

//TCanvas c1 = new TCanvas("c1", 500, 500);
//c1.draw(hPe_vz);

dir.addDataSet(hPe_vz);
dir.addDataSet(he_vz);
dir.addDataSet(hp_vz);
dir.writeFile("/work/clas12/viducic/rho/clas12/vertexAnalysis.hipo");

println("done");

























public class FileFinder {

    public FileFinder() {
    }

    private static List<String> listOfFiles = new ArrayList<String>();
    private static String newKeyWord = "";
    public static int DEBUG_MODE = 0;

    public static List<String> getFiles(List<String> listOfDirs, String fileName){
        for(String dir : listOfDirs){
            getFiles(dir, fileName);
        }
        return listOfFiles;
    }

    public static List<String> getFilesFromSubdirs(String directory, String wildcard){
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

            for(int var5 = 0; var5 < var4; ++var5) {
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