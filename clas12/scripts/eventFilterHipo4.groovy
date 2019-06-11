import org.jlab.jnp.hipo4.data.Bank
import org.jlab.jnp.hipo4.data.Event
import org.jlab.jnp.hipo4.data.SchemaFactory
import org.jlab.jnp.hipo4.io.HipoReader
import org.jlab.jnp.hipo4.io.HipoWriter
import org.jlab.jnp.physics.EventFilter
import org.jlab.jnp.physics.PhysicsEvent
import org.jlab.jnp.reader.DataManager
import org.jlab.jnp.utils.file.FileUtils

//Get list of files from subdirectories.  Class to be implemented in the main software package soon. If this functionality
//is useful for you, let me know and I will send you the FileFinder class
List<String> dataFiles = FileFinder.getFilesFromSubdirs("/w/hallb-scifs17exp/clas12/rg-a/production/recon/pass0/v5/mon", "*");

//Declare an event filter using lundPID.
//In this case, 11(e), 2212(p), 211(pi+), -211(pi-), Xn(any other neutrals)
EventFilter filter = new EventFilter("11:2212:211:-211:Xn:X+:X-");

//Here I skim ~60 runs and write them to a single file. In order to do that, the writer must be declared
//outside of the event loop.  This shows how to do it.  It must have the same SchemaFactory as the the input files
//So below is how to set it up properly
//Open first file in file list and declare a writer with the schema factory that the reader returns. Close that reader.
HipoReader firstReader = new HipoReader();
firstReader.open(dataFiles[0]);
HipoWriter writer = new HipoWriter(firstReader.getSchemaFactory());
firstReader.close();

//Open file you want to write to.  It will overwrite if the file already exists
writer.open("/w/hallb-scifs17exp/clas12/viducic/data/clas12/testDataFile_filtered_2.hipo");

//Begin looping over the files in our datafile list
for(String dataFile : dataFiles){
    //Open a hipowriter to open and read the datafile.  !!This is NOT the same reader we used before!!
    HipoReader reader = new HipoReader();
    reader.open(dataFile);

    //The new hipo4 format makes use of the Bank class and an empty Event to read the information in from the file.
    //Hopefully this makes sense in a few lines
    Bank particles = new Bank(reader.getSchemaFactory().getSchema("REC::Particle"));
    Event event = new Event();

    //Loop over events in the data file and fill the event/bank
    while(reader.hasNext()){
        //This fills our empty event object with the event
        reader.nextEvent(event);
        //this gets the relevant bank information and fills it
        event.read(particles);

        //Construct a physics event. We will look at how powerful this class is a little later
        PhysicsEvent physEvent = DataManager.getPhysicsEvent(10.6, particles);

        //If the physics event passes the filter, write it to a file
        if(filter.isValid(physEvent)){
            writer.addEvent(event);
        }
    }
}
//Close the writer
writer.close();
































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
