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

//This line returns a list of files in a given directory matching the search pattern after the *.  In this case, it
//returns all of the .hipo files in the skim4_inclusive directory
//List<String> dataFiles = FileFinder.getFiles("/w/hallb-scifs17exp/clas12/rg-a/trains/calibration/v4/skim4_inclusive/*.hipo");
List <String> dataFiles = FileFinder.getFiles("/w/hallb-scifs17exp/clas12/viducic/data/clas12/testDataFile_filtered_skimmed_0.hipo");

//Here I declare all of my histograms that I will be using.  For this portion of the talk, I am only looking at
//invariant mass of the two pions and the missing mass of the proton, so I only need two.
//The histogram class has many methods associated with it. Shown here are "setTitle" and "setFillColor"

H1F hCutMxp = new H1F("hCutMxp", 120, 0.4, 1.2);
hCutMxp.setTitle("mx_P w/ cut|mx2_PePipPimGam| < 0.01 && mp_PePipPim > 0.1");
hCutMxp.setFillColor(44);

H1F himPipPimGamUncut = new H1F("himPipPimGamUncut", 150, 0.4, 1.2);
himPipPimGamUncut.setTitle("IM_PipPimXn");
himPipPimGamUncut.setFillColor(42);

//Here I set up a TDirectory that I can use to save the histograms that I make.  I personally prefer this to drawing
//histograms on multiple canvases
TDirectory dir = new TDirectory();

//Within the TDirectory We can have sub directories.  For example, if I apply two different sets of cuts to the same
//dataset, I can save them to different directories to keep them separate.
dir.mkdir("/Plots");
dir.cd("/Plots");
dir.mkdir("/CutPlots");

// Begin Analysis //

//Declare the beam evergy in a variable. I am currently writing a class that can be used to return the beam energy as a
//function of run number and hope to have that done soon for anyone who is working with multiple runs across varying
//beam energies
double beamEnergy = 10.6;

//Start event counter because I'm impatient and like to know what my progress/file is
int nEvents = 0;

//Begin looping over the files that I collected in a list in line 1. Gagik hopes to include this functionality in the
//software package soon with similar functionality to TChain in ROOT
for(String dataFile : dataFiles) {

    //Declare the reader needed to open and read hipo files and read it.
    HipoReader reader = new HipoReader();
    reader.open(dataFile);

    //Declare bank that we will reference using the reader.getSchemaFactory method.  This is physics analysis so we are
    //only interested in "REC::Particle"
    Bank particles = new Bank(reader.getSchemaFactory().getSchema("REC::Particle"));

    //Declare our event. This will be more clear in a second.
    Event event = new Event();

    //Declare our eventFilter.  We are only interested in events with an electron, proton, pi+, and pi- exclusive.
    EventFilter filter = new EventFilter("11:2212:211:-211:22");

    //Update on how many files from our file list have been opened.
    println("done " + (dataFiles.indexOf(dataFile)+1) + " out of " + dataFiles.size() + " files");

    //Loop over all events in the file
    while (reader.hasNext()) {

        //Fill our empty event object using the reader.nextEvent method.  This is why we needed to declare it before
        reader.nextEvent(event);
        //Fill the event with the information from the given bank, in this case "REC::Particle"
        event.read(particles);

        //Use the DataManager class to extract physics events from the REC::Particle bank.
        //PhysicsEvent is a very powerful class that I hope will be on display soon
        PhysicsEvent physEvent = DataManager.getPhysicsEvent(beamEnergy, particles);

        //iterate the event counter and update on every 10000 events. Should probably change to a percentage counter but
        //I'm lazy and it works well enough if you know how many events you have.
        nEvents++;
        if (nEvents % 10000 == 0) {
            System.out.println("done " + nEvents);
        }

        //Here is the meat and potatoes of the code.  This is where the magic (physics) happens. In this example, that
        //physics is kinda lame but it's just an example.
        if (filter.isValid(physEvent)) {

            //Declare a particle.  This particle will be the beam + the target - the electron and proton and the
            //pi+ + pi-
            //This essentially returns a 4 vector for that quantity. so (b_E + t_E - e_E - p_E, b_px + e_px...etc
            Particle mx_P = physEvent.getParticle("[b] + [t] - [11] - [2212]");
            Particle im_PipPim = physEvent.getParticle("[211] + [-211] + [22]");
            Particle mx_PePipPim = physEvent.getParticle("[b] + [t] - [11] - [2212] - [211] - [-211]");
            Particle mx_PePipPimGam = physEvent.getParticle("[b] + [t] - [11] - [2212] - [211] - [-211] - [22]");
            Particle gam = physEvent.getParticle("[22]");

            //I fill the missing mass histogram with the mass of that 4-vector, which is the missing mass of the proton
            //and electron.  What we hope, in this case, is that we see a rho resonance peak at 770 MeV/c
            if (Math.abs(mx_PePipPimGam.mass2()) < 0.01 && mx_PePipPim.p() > 0.1) {
                hCutMxp.fill(mx_P.mass());
                himPipPimGamUncut.fill(im_PipPim.mass());

            }
            //Same here but with the 4-vector of the two pions.  We hope to see a rho.
            //himPipPimGamUncut.fill(im_PipPim.mass());

        }
    }

    //Close the reader. Clean code or something.a
    reader.close();
}

//Now we must add our histograms to our TDirectory
dir.addDataSet(hCutMxp);

//To switch between directories, we use the TDirectory.cd() method.
dir.cd("/CutPlots");
dir.addDataSet(himPipPimGamUncut);

//Now that we have our histograms in the TDirectory, we must write the directory to a file.
dir.writeFile("/work/clas12/viducic/rho/clas12/sampleRhoAnalysis_PipPim_0.hipo");

//Tells me when the code has finished executing.
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
