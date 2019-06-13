import org.jlab.groot.data.H1F
import org.jlab.groot.data.TDirectory
import org.jlab.groot.ui.TCanvas
import org.jlab.groot.ui.TGCanvas
import org.jlab.jnp.hipo.io.HipoWriter
import org.jlab.jnp.hipo4.data.Bank
import org.jlab.jnp.hipo4.data.Event
import org.jlab.jnp.hipo4.io.HipoReader
import org.jlab.jnp.physics.EventFilter
import org.jlab.jnp.physics.Particle
import org.jlab.jnp.physics.PhysicsEvent
import org.jlab.jnp.reader.DataManager
import org.jlab.jnp.utils.benchmark.ProgressPrintout
import org.jlab.jnp.utils.file.FileUtils
import java.util.HashMap;
import java.util.Map;

//X+:X- e theta < 8
//pPipPim theta < 40

//Same as before, get a list of files in a directory that matches a search pattern.  If you want this Class, ask me :)
//List<String> dataFiles = FileFinder.getFiles("/w/hallb-scifs17exp/clas12/rg-a/trains/v2/skim4_inclusive/*");
List<String> dataFiles = FileFinder.getFiles("/w/hallb-scifs17exp/clas12/viducic/data/clas12/testDataFile_filtered_skimmed_2.hipo");

//Step one is declaring histograms.  The histogram class in the JAVA framework is robust with a lot of familiar function
//Here we see the constructor and setTitle/setFillColor methods but there are many more
H1F hMxpUncut = new H1F("hMxPUncut", 140, 0.4, 1.4);
hMxpUncut.setTitle("mx_P w/ |mx2_PePipPim| < 0.01 && mp_PePipPim > 0.1");
hMxpUncut.setFillColor(43);

H1F hCutMxp = new H1F("hCutMxp", 140, 0.4, 1.4);
hCutMxp.setTitle("mx_P w/ cut|mx2_PePipPim| < 0.01 && mp_PePipPim > 0.1 && cosTheta > 0.98");
hCutMxp.setFillColor(43);

H1F hMx2_PePipPim = new H1F("hMx2_PiPipPim", 210, -0.1, 0.1);
hMx2_PePipPim.setTitle("Missing mass squared of pePipPim");

H1F hMP_PePipPim = new H1F("hMP_PiPipPim", 210, -0.1, 2);
hMP_PePipPim.setTitle("Missing momentum of pePipPim");

H1F himPipPimGamUncut = new H1F("himPipPimGamUncut", 140, 0.4, 1.4);
himPipPimGamUncut.setTitle("IM_PipPimXn");
himPipPimGamUncut.setFillColor(43);

//I personally don't like to draw plots to my screen unless I am debugging.  I prefer to save the histograms I make
//to a TDirectory.  A TDirectory can be opened with a TBrowser, just like in ROOT.
//Below i initiate a TDirectory and add some sub directories.  One for the plots I am going to show and one for the cuts
//I am going to make
TDirectory dir = new TDirectory();
//above the directory is initiated, below the subdir is initiated
dir.mkdir("/CutPlots");
//Just like in a terminal, you must cd into the directory you want to work with - TDirectory.cd(String dirName)
dir.mkdir("/Plots");
dir.cd("/CutPlots");

// Begin Analysis //

//Declare the beam energy and event counter.  Two things I am working on is a class that takes run number as an argument
//and returns the beam energy.  This is be very useful for people using the entire spread of RGA runs as it covers
//several beam energies.
//I am also working on a little visual progress bar. Like "[====>   ] x% done" or something along those lines.
double beamEnergy = 10.6
int nEvents = 0;
//ProgressPrintout progress = new ProgressPrintout();

//Loop over files in list, same as before
for (String dataFile : dataFiles) {
    //Update on how many files have been done so far
    println("done " + (dataFiles.indexOf(dataFile) + 1) + " out of " + dataFiles.size() + " files");

    //declare reader and open current file
    HipoReader reader = new HipoReader();
    reader.open(dataFile);

    //Same as before, define bank and event for reader to fill
    Bank particles = new Bank(reader.getSchemaFactory().getSchema("REC::Particle"));
    Event event = new Event();

    //Filter is probably redundant but it's here anyway.
    EventFilter filter = new EventFilter("11:2212:211:-211:Xn:X+:X-");

    //Event loop
    while (reader.hasNext()) {

        //Poor-mans progress bar
        nEvents++;
        if (nEvents % 10000 == 0) {
            System.out.println("done " + nEvents);
        }

        reader.nextEvent(event);
        event.read(particles);
        //Initiate physics event like before.  We will see how it is used shortly
        PhysicsEvent physEvent = DataManager.getPhysicsEvent(beamEnergy, particles);
        //Figure out whether electron is in first row. Useful to see difference when it is and isn't
        int pid = particles.getInt("pid", 0);

        //Time to do some physics if an event passes our filter
        //also require electron in forward tagger becuasuse then our meson event will be in the FD, not the CD
        if (filter.isValid(physEvent) && pid != 11) {

            //Define the "Particles" I will use.  These are basically lorentz vectors with a vertex and other quantities
            //The two below are missing mass of the proton and electron and the missing "particle" of proton, electron,
            //pi+, pi-
            Particle mx_P = physEvent.getParticle("[b] + [t] - [11] - [2212]");
            Particle mx_PePipPim = physEvent.getParticle("[b] + [t] - [11] - [2212] - [211] - [-211]");

            //fill histograms with the missing mass squared of pepi+pi- to find a 2 sigma cut around zero. If the mm2 is
            //close to zero and the missing momentum of pepi+pi- is >0, it could be a photon event.
            hMx2_PePipPim.fill(mx_PePipPim.mass2());
            hMP_PePipPim.fill(mx_PePipPim.p());

            //Because I am skimming for events semi-inclusively, I need to identify which particles could be photons
            //In order to do this, I loop over all the neutral events in an event and test the angle between the neutral
            //and the missing mass of the pepi+pi-.
            //here I declare the variables I'll need to do this testing.  We can see another use of PhysicsEvent below
            int nNeutrals = physEvent.countByCharge(0);
            double bestCos = -2.0;
            //double pgam;
            double im_PipPimGam;

            //try best match method

            //Here is where we do the actual testing. Our first cuts are on the missing momentum and mass2 of pepi+pi-
            //if the missing mass2 of pepi+pi- is < 0.01 and > -0.01, and the missing momentum is > 0.1, the neutral loop
            //executes
            if (Math.abs(mx_PePipPim.mass2()) < 0.02 && mx_PePipPim.p() > 0.1) {
                //here i loop over the neutral particles.  I define a particle gam.  I test the angle between this
                //particle and the missing vector, like i said before
                for (int i = 0; i < nNeutrals; i++) {
                    Particle gam = physEvent.getParticleByCharge(0, i);
                    //The Particle class has a cosTheta method that returns the cos of the angle between two particles
                    //we keep track of the best cosTheta
                    if (mx_PePipPim.cosTheta(gam) > bestCos) {
                        //if we find a particle with a better costheta, we store all the information from that particle
                        //such as the costheta, momentum of the neutral and the invariant mass of the pi+pi-neutral
                        bestCos = mx_PePipPim.cosTheta(gam);
                        //pgam = gam.p()
                        Particle im_ppg = physEvent.getParticle("[211] + [-211]");
                        //Here I declare a particle of pi+ pi- and i combine it with the gam particle if it has a better
                        //costheta.  I store the invariant mass in a variable becuase this Particle is not initialized
                        //outside of the loop. For people new to programming, if you define an object inside of a loop,
                        //you cannot use it outside of that loop in Java (python can.)
                        im_ppg.combine(gam, 0);
                        im_PipPimGam = im_ppg.mass();
                    }

                }
            //if (Math.abs(mx_P.mass() - pgam) < 1.0) {
                    //For comparison's sake, I fill a histogram with the missing mass of the pe system without any cuts
                    //on cos theta
                    hMxpUncut.fill(mx_P.mass());
                    //Then I fill the invariant mass histogram and missing mass histogram is the best costheta was > .98
                    if (bestCos > 0.98) {
                        hCutMxp.fill(mx_P.mass());
                        himPipPimGamUncut.fill(im_PipPimGam);
                    }
             //   }
            }
        }
    }
    //close the reader
    reader.close();
}

//here we add out histograms to the subdirectories in our Tdirectory we want them in
dir.addDataSet(hMx2_PePipPim);
dir.addDataSet(hMP_PePipPim);

dir.cd("/Plots");
dir.addDataSet(hMxpUncut);
dir.addDataSet(hCutMxp);
dir.addDataSet(himPipPimGamUncut);

//Very important step.  Be sure to actually write your directory to a file, or else it's useless
dir.writeFile("/work/clas12/viducic/rho/clas12/sampleRhoAnalysis_0.hipo");
//Tell me the script is finishes executing
println("done");

////////////////////////// FIN //////////////////////////////////////////////////////

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

