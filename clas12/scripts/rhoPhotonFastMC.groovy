import org.jlab.geom.prim.Line3D
import org.jlab.geom.prim.Path3D
import org.jlab.geom.prim.Point3D
import org.jlab.geom.prim.Shape3D
import org.jlab.geom.prim.Triangle3D
import org.jlab.groot.data.H2F
import org.jlab.groot.data.TDirectory
import org.jlab.jnp.physics.Particle
import org.jlab.jnp.physics.ParticleList
import org.jlab.jnp.physics.PhysicsEvent
import org.jlab.jnp.reader.LundReader
import org.jlab.jnp.utils.file.FileUtils


Calorimeter eCal = new Calorimeter();
DriftChamber dc = new DriftChamber();
List<String> dataFiles = FileFinder.getFiles("/work/clas12/avakian/mc/mcaugust2019/lund/pythia/claspyth11.21.41-0.5.0.4.0.3/clas*.dat");

H2F eDetected = new H2F("eDetected", 90, 0, 90, 90, 0, 90);
H2F eGamDetected = new H2F("eGanDetected", 90, 0, 90, 90, 0, 90);
H2F gamUndetected = new H2F("eDetected", 90, 0, 90, 90, 0, 90);
H2F ePiPiDetected = new H2F("eDetected", 90, 0, 90, 90, 0, 90);

H2F eDetectedCut = new H2F("eDetectedCut", 90, 0, 90, 90, 0, 90);
H2F eGamDetectedCut = new H2F("eGanDetectedCut", 90, 0, 90, 90, 0, 90);
H2F gamUndetectedCut = new H2F("eDetectedCut", 90, 0, 90, 90, 0, 90);
H2F ePiPiDetectedCut = new H2F("eDetectedCut", 90, 0, 90, 90, 0, 90);

TDirectory dir = new TDirectory();
dir.mkdir("/Cut");
dir.mkdir("/Uncut");

for(String dataFile : dataFiles){
    LundReader reader = new LundReader();
    reader.acceptStatus(1);
    reader.addFile(dataFile);
    reader.open();

    PhysicsEvent event = new PhysicsEvent();

    while (reader.nextEvent(event)){

        event.setBeamParticle(new Particle(11, 0, 0, 10.6));
        event.setTargetParticle(new Particle(2212, 0, 0, 0));

        ParticleList particles = event.getParticleList();

        Particle p = event.getParticleByPid(2212, 1);
        Particle e = event.getParticleByPid(11, 1);
        Particle pip = event.getParticleByPid(211, 0);
        Particle pim = event.getParticleByPid(-211, 0);
        Particle gam = event.getParticleByPid(22, 0);
        Particle pipPim = event.getParticle("[211] + [-211]");

        StraightLine pLine = new StraightLine(p);
        StraightLine electronLine = new StraightLine(e);
        StraightLine pipLine = new StraightLine(pip);
        StraightLine pimLine = new StraightLine(pim);
        StraightLine gammaLine = new StraightLine(gam);

        Path3D pPath = pLine.getPath();
        Path3D ePath = electronLine.getPath();
        Path3D pipPath = pipLine.getPath();
        Path3D pimPath = pimLine.getPath();
        Path3D gamPath = gamLine.getPath();

        Line3D eLine = ePath.getLine(0);
        Line3D gamLine = gamPath.getLine(0);

        if(eCal.hasIntersection(eLine) && dc.hasHitsInAllLayers(eLine)){
            eDetected.fill(Math.toDegrees(pipPim.theta()), Math.toDegrees(pipPim.phi()));
            if(eCal.hasIntersection(gamLine)){
                eGamDetected.fill(Math.toDegrees(pipPim.theta()), Math.toDegrees(pipPim.phi()));
            }
            else if (eCal.hasIntersection(pipPath.getLine(0)) && eCal.hasIntersection(pimPath.getLine(0))
                && dc.hasHitsInAllLayers(pipPath.getLine(0)) && dc.hasHitsInAllLayers(pimPath.getLine(0))){
                ePiPiDetected.fill(Math.toDegrees(gam.theta()), Math.toDegrees(gam.phi()));
            }
            else if(gam.theta() > Math.toRadians(45) && gam.theta() < Math.toRadians(135)){
                gamUndetected.fill(Math.toDegrees(pipPim.theta()), Math.toDegrees(pipPim.phi()));
            }
        }

        if (e.theta() < Math.toRadians(4.5)){
            if(eCal.hasIntersection(eLine) && dc.hasHitsInAllLayers(eLine)){
                eDetectedCut.fill(Math.toDegrees(pipPim.theta()), Math.toDegrees(pipPim.phi()));
                if(eCal.hasIntersection(gamLine)){
                    eGamDetectedCut.fill(Math.toDegrees(pipPim.theta()), Math.toDegrees(pipPim.phi()));
                }
                else if (eCal.hasIntersection(pipPath.getLine(0)) && eCal.hasIntersection(pimPath.getLine(0))
                        && dc.hasHitsInAllLayers(pipPath.getLine(0)) && dc.hasHitsInAllLayers(pimPath.getLine(0))){
                    ePiPiDetectedCut.fill(Math.toDegrees(gam.theta()), Math.toDegrees(gam.phi()));
                }
                else if(gam.theta() > Math.toRadians(45) && gam.theta() < Math.toRadians(135)){
                    gamUndetectedCut.fill(Math.toDegrees(pipPim.theta()), Math.toDegrees(pipPim.phi()));
                }
            }
        }
    }
}

dir.cd("/Uncut");
dir.addDataSet(eDetected, eGamDetected, ePiPiDetected, gamUndetected);
dir.cd("/Cut");
dir.addDataSet(eDetectedCut, eGamDetectedCut, ePiPiDetectedCut, gamUndetectedCut);

dir.writeFile("/work/clas12/viducic/rho/clas12/results/rhoFastMCResults.hipo");


// ################################################################################################################## //
// ################################################################################################################## //


public class StraightLine {

    Path3D path = new Path3D();
    Particle myParticle = new Particle();


    public StraightLine(Particle myParticle) {
        this.myParticle = myParticle;
        getStartPoint(myParticle);
    }

    public void getStartPoint(Particle particle){
        this.path.addPoint(particle.vertex().x(), particle.vertex().y(), particle.vertex().z());
    }

    private void makePath(){
        this.path.addPoint(1500 * myParticle.px(), 1500*myParticle.py(), 1500* myParticle.pz());
    }

    public Path3D getPath(){
        makePath();
        return this.path;
    }
}

public class Detector {
    String name;
    ArrayList<Shape3D> components = new ArrayList<>();

    public Detector(){}

    public Detector(String name){
        this.name = name;
    }

    public Detector(String name, ArrayList<Shape3D> components) {
        this.components = components;
    }

    public void addComponent(Shape3D shape){
        this.components.add(shape);
    }

    public void removeComponent(int sector){
        this.components.remove(sector);
    }

    public void removeComponent(Shape3D shape){
        this.components.remove(shape);
    }

    public Shape3D getComponent(int sector){
        return this.components.get(sector);
    }

    public boolean hasIntersection(Line3D line){
        Iterator<Shape3D> iter = components.iterator();
        while (iter.hasNext()){
            if (iter.next().hasIntersection(line)){
                return true;
            }
        }
        return false;
    }

    public int intersection(Line3D line, ArrayList<Point3D> point) {
        Iterator<Shape3D> iter = components.iterator();
        int count = 0;
        while (iter.hasNext()) {
            Shape3D detectorComponent = iter.next();
            if (detectorComponent.hasIntersection(line)) {
                count += detectorComponent.intersection(line, point);
            }
        }
        return count;
    }

}

public class Calorimeter extends Detector {

    double distance = 721.7;
    double tilt     = 25.0;

    public Calorimeter() {
        this.name = "Cal";
        this.initCal();
    }

    //b = 305.013
    //a = 86.179
    //translate to 721.723
    //rotate 25 deg in y
    //rotate
    //convert deg to rad

    private void initCal(){

        ArrayList<Shape3D> list = new ArrayList<>();

        for(int i = 0; i < 6; i++){
            Triangle3D tri = createSector();
            tri.translateXYZ(0.0,0.0,distance);
            tri.rotateY(Math.toRadians(tilt));
            tri.rotateZ(Math.toRadians(60*i));
            Shape3D  shape = new Shape3D();
            shape.addFace(tri);
            this.addComponent(shape);
        }
        /* Triangle3D slice = new Triangle3D(
                      0.0,   0.0, 50.0,
                    197.1, 385.2, 50.0,
                   -197.1, 385.2, 50.0);

           slice.rotateZ(30 * 0.0174533);
           slice.rotateY(25 * 0.0174533);
           for(int i = 0; i < 6; i++){
               Shape3D calSlice = new Shape3D();
               calSlice.addFace(new Triangle3D(slice));
               calSlice.moveTo(0,0,700);
               this.components.add(calSlice);
               slice.rotateZ(60 * 0.0174533);
           }*/
    }


    public Triangle3D createSector(){
        double a = 86.179;
        double b = 305.013;
        return new Triangle3D(
                a,   -394.2/2, 0.0,
                a,    394.2/2, 0.0,
                -b,    0.0,  0.0);
    }

}

public class DriftChamberSuperlayer extends Detector {

    int superLayerNumber;
    double wirePlaneDistance;
    double thetaMin;
    double distanceToTarget;
    double tilt = 25;

    public DriftChamberSuperlayer(int superLayerNumber, double wirePlaneDistance, double thetaMin, double distanceToTarget) {
        this.name = "driftChamber";
        this.superLayerNumber = superLayerNumber;
        this.thetaMin = thetaMin;
        this.wirePlaneDistance = wirePlaneDistance;
        this.distanceToTarget = distanceToTarget;
    }

    public DriftChamberSuperlayer(int superLayerNumber){
        this.superLayerNumber = superLayerNumber;
        SuperLayerParams params = new SuperLayerParams();
        this.thetaMin = params.getTHMin(superLayerNumber);
        this.distanceToTarget = params.getDist2Targ(superLayerNumber);
        this.wirePlaneDistance = params.getWPD(superLayerNumber);
    }

    private double height(){
        return 111 * 4 * this.wirePlaneDistance * Math.cos(Math.toRadians(30));
    }

    private double distanceBelowX(){
        return this.distanceToTarget*(Math.tan(Math.toRadians(25 - this.thetaMin)));
    }

    public Triangle3D createSector(){
        return new Triangle3D(height() - distanceBelowX(), -height()*Math.tan(Math.toRadians(30)), 0,
                height() - distanceBelowX(), height()*Math.tan(Math.toRadians(30)),  0,
                -distanceBelowX(),              0,                      0);
    }




    public void initDCSector(){

        for(int i = 0; i < 6; i++){
            Triangle3D sector = createSector();
            sector.translateXYZ(0,0, this.distanceToTarget);
            sector.rotateY(Math.toRadians(tilt));
            sector.rotateZ(Math.toRadians(i * 60));
            Shape3D shape = new Shape3D();
            shape.addFace(sector);
            this.addComponent(shape);

        }
    }

}

public class SuperLayerParams {

    Map<Integer, HashMap<String, Double>> superLayerParamMap = this.initMap();

    private Map initMap(){
        HashMap<Integer, HashMap<String, Double>> paramMap = new HashMap<>();
        paramMap.put(1, new HashMap<String, Double>(){{
            put("wpdist", 0.3861); put("thmin", 4.694); put("dist2tgt", 228.078);
        }});

        paramMap.put(2, new HashMap<String, Double>(){{
            put("wpdist", 0.4042); put("thmin", 4.495); put("dist2tgt", 238.687);
        }});

        paramMap.put(3, new HashMap<String, Double>(){{
            put("wpdist", 0.6219); put("thmin", 4.812); put("dist2tgt", 351.544);
        }});

        paramMap.put(4, new HashMap<String, Double>(){{
            put("wpdist", 0.6586); put("thmin", 4.771); put("dist2tgt", 371.773);
        }});

        paramMap.put(5, new HashMap<String, Double>(){{
            put("wpdist", 0.9351); put("thmin", 4.333); put("dist2tgt", 489.099);
        }});

        paramMap.put(6, new HashMap<String, Double>(){{
            put("wpdist", 0.9780); put("thmin", 4.333); put("dist2tgt", 511.236);
        }});

        return paramMap;
    }

    public SuperLayerParams() {
    }

    public HashMap<String, Double> getParams(int superLayer){
        return this.superLayerParamMap.get(superLayer);
    }

    public double getTHMin(int superLayer){
        return this.getParams(superLayer).get("thmin");
    }

    public double getWPD(int superLayer){
        return getParams(superLayer).get("wpdist");
    }

    public double getDist2Targ(int superLayer){
        return getParams(superLayer).get("dist2tgt");
    }

}

public class DriftChamber {
    ArrayList<DriftChamberSuperlayer> superLayers = new ArrayList<>();

    public DriftChamber() {
        this.superLayers = initDriftChamber();
    }

    public DriftChamberSuperlayer getLayer(int superlayerNumber){
        return this.superLayers.get(superlayerNumber -1);
    }

    private ArrayList<DriftChamberSuperlayer> initDriftChamber(){
        ArrayList<DriftChamberSuperlayer> superLayers = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            DriftChamberSuperlayer superLayer = new DriftChamberSuperlayer(i + 1);
            superLayer.initDCSector();
            superLayers.add(superLayer);
        }
        return superLayers;
    }

    public boolean hasHitsInAllLayers(Line3D line){
        for(DriftChamberSuperlayer layer : this.superLayers){
            if(!layer.hasIntersection(line)){
                return false;
            }
        }
        return true;
    }
}

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
